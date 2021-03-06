(ns minimalquotes.components.forms
  (:require
    [fork.reagent :as fork]
    [lambdaisland.glogi :as log]
    [minimalquotes.components.buttons :as btn]
    [minimalquotes.components.icons :refer
     [icon-edit icon-tag icon-trash icon-upload-to-cloud]]
    [minimalquotes.components.modal :refer [modal!]]
    ["react-bootstrap-typeahead" :refer [Typeahead]]
    [vlad.core :as vlad]))

(def form-css-classes ["bg-white" "shadow-md" "rounded" "px-8" "pt-6" "pb-8"])
(def label-css-classes ["block" "text-gray-700" "text-sm" "font-bold" "mb-2"])
(def input-css-classes ["form-input" "mt-1" "block" "w-full"])
(def select-css-classes ["form-select" "mt-1" "block" "w-full"])

(defn tag->tag-with-id
  [[tag-id tag]]
  (merge tag {:id tag-id}))

(defn make-quote-form
  "This function returns a form to create/edit a quote.
  The returned form has no state and no validation."
  [{:keys [on-click-cancel tags]}]
  (fn quote-form-inner [{:keys [errors form-id handle-blur handle-change
                                handle-submit submitting? state touched
                                values]}]
    [:form {:class form-css-classes :id form-id :on-submit handle-submit}
     [:div {:class ["mb-4"]}
      [:label {:class label-css-classes :for "text"} "Text:"]
      [:input {:id "text"
               :auto-focus true
               :class input-css-classes
               :data-testid "text"
               :name "text"
               :on-blur handle-blur
               :on-change handle-change
               :value (values "text")}]
      (when (touched "text")
        [:div (first (get errors (list "text")))])]
     [:div {:class ["mb-4"]}
      [:label {:class label-css-classes :for "author"} "Author:"]
      [:input {:id "author"
               :class input-css-classes
               :data-testid "author"
               :name "author"
               :on-blur handle-blur
               :on-change handle-change
               :value (values "author")}]
      (when (touched "author")
        [:div (first (get errors (list "author")))])]
     [:div {:class ["mb-4"]}
      [:label {:class label-css-classes :for "input-tags-typeahead"} "Tags:"]
      [:> Typeahead
       {:class-name input-css-classes
        :clear-button true
        ;  :default-selected [(first (vals tags)) (last (vals tags))]
        :id "input-tags-typeahead"
        :label-key "name"
        :multiple true
        :on-blur handle-blur
        ;; TODO: this typeahead component seems not to allow to extract new
        ;; tags, only existing ones.
        :on-change (fn [^js js-values]
                     (log/debug :typeahead {:message "input-tags-typeahead change"
                                            :js-values js-values})
                     (let [values (js->clj js-values)
                           values-k (js->clj js-values :keywordize-keys true)
                           names (map #(get % "name") values)]
                       (swap! state assoc-in [:values :tags] names)))
        :options (map tag->tag-with-id tags)}]]
     [:div {:class ["flex" "items-center" "justify-between"]}
      [btn/button {:on-click on-click-cancel :text "Cancel"}]
      [btn/submit {:disabled submitting? :text "Confirm"}]]]))

(def quote-form-validation
  (vlad/join
    (vlad/attr ["text"] (vlad/chain (vlad/present) (vlad/length-over 5)))
    (vlad/attr ["author"] (vlad/chain (vlad/present) (vlad/length-in 1 15)))))

(defn quote-form
  "Stateful form to create/edit a quote.
  Form's state is managed by fork. Form's validation is implemented with vlad."
  [{:keys [on-click-cancel on-submitted-values author text tags]
    :or {author "" text ""}}]
  ; TODO: make :default-selected tags with ids
  (let [config {:clean-on-unmount? true
                :initial-values {"author" author "text" text}
                :on-submit (fn [m] (on-submitted-values (:values m)))
                :prevent-default? true
                :validation #(vlad/field-errors quote-form-validation %)}
        f (make-quote-form {:on-click-cancel on-click-cancel :tags tags})]
    [fork/form config f]))

(defn button-add-new-quote-modal
  [{:keys [on-submitted-values tags]}]
  (let [on-click-cancel #(modal! nil)
        f (fn [values] (on-submitted-values values) (modal! nil))]
    [btn/button {:on-click #(modal! [quote-form {:on-click-cancel on-click-cancel
                                                 :on-submitted-values f
                                                 :tags tags}])
                 :text "Add quote"}]))

(def tag-name-max-length 24)
(def tag-description-max-length 48)

(def tag-form-validation
  (vlad/join
    (vlad/attr ["name"]
               (vlad/chain (vlad/present)
                           (vlad/length-in 3 (+ 1 tag-name-max-length))))
    (vlad/attr ["description"]
               (vlad/chain (vlad/present)
                           (vlad/length-in 1
                                           (+ 1 tag-description-max-length))))))

; TODO: use fieldset?

(defn make-tag-form
  "This function returns a form to create/edit a tag.
  The returned form has no state and no validation."
  [{:keys [on-click-cancel]}]
  (fn tag-form-inner [{:keys [errors form-id handle-blur handle-change
                              set-touched handle-submit state submitting?
                              touched values]}]
    (let [on-name-change (fn [^js e]
                           ;; The user might keep typing and exceed the max
                           ;; allowed length for this input. If we don't set the
                           ;; input to be touched he won't see any errors, which
                           ;; is bad for UX.
                           (set-touched "name")
                           ;; Update form's state.
                           (handle-change e)
                           ;; Compute derived state and update form's state once
                           ;; more.
                           (let [current-length (count (get-in @state [:values "name"]))
                                 remaining-length (- tag-name-max-length current-length)]
                             (swap! state assoc-in
                               [:values "remaining-tag-name-length"]
                               remaining-length)))
          on-description-change (fn [^js e]
                                  (set-touched "description")
                                  (handle-change e)
                                  (let [current-length (count (get-in @state [:values "description"]))
                                        remaining-length (- tag-description-max-length current-length)]
                                    (swap! state assoc-in
                                      [:values "remaining-tag-description-length"]
                                      remaining-length)))]
      [:form {:class form-css-classes :id form-id :on-submit handle-submit}
       [:div {:class ["mb-4"]}
        [:label {:class label-css-classes :for "name"} "Name:"]
        [:input {:id "name"
                 :auto-focus true
                 :class input-css-classes
                 :data-testid "name"
                 :name "name"
                 :on-blur handle-blur
                 :on-change on-name-change
                 :value (values "name")}]
        (let [diff (get-in @state [:values "remaining-tag-name-length"])
              err-message (first (get errors (list "name")))]
          (if err-message
            (when (touched "name") [:div err-message])
            (cond (> diff 0) [:div (str diff " remaining characters.")]
              (= diff 0) [:div "No remaining characters."]
              :else nil)))]
       [:div {:class ["mb-4"]}
        [:label {:class label-css-classes :for "color"} "Color:"]
        [:select {:class select-css-classes
                  :id "color"
                  :name "color"
                  :on-blur handle-blur
                  :on-change handle-change
                  :value (values "color")}
         [:option {:value "blue"} "blue"]
         [:option {:value "gray"} "gray"]
         [:option {:value "green"} "green"]
         [:option {:value "indigo"} "indigo"]
         [:option {:value "orange"} "orange"]
         [:option {:value "purple"} "purple"]
         [:option {:value "pink"} "pink"]
         [:option {:value "red"} "red"]
         [:option {:value "teal"} "teal"]
         [:option {:value "yellow"} "yellow"]]]
       [:div {:class ["mb-4"]}
        [:label {:class label-css-classes :for "description"} "Description:"]
        [:input {:id "description"
                 :class input-css-classes
                 :data-testid "description"
                 :name "description"
                 :on-blur handle-blur
                 :on-change on-description-change
                 :value (values "description")}]
        (let [diff (get-in @state [:values "remaining-tag-description-length"])
              err-message (first (get errors (list "description")))]
          (if err-message
            (when (touched "description") [:div err-message])
            (cond (> diff 0) [:div (str diff " remaining characters.")]
              (= diff 0) [:div "No remaining characters."]
              :else nil)))]
       [:div {:class ["flex" "items-center" "justify-between"]}
        [btn/button {:on-click on-click-cancel :text "Cancel"}]
        [btn/submit {:disabled submitting? :text "Confirm"}]]])))

(defn dissoc-hints
  "Dissoc any hint text values when submitting the form. They are only useful as
  a feedback when filling the form itself."
  [values]
  (dissoc values "remaining-tag-description-length" "remaining-tag-name-length"))

(defn tag-form
  "Stateful form to create/edit a tag.
  Form's state is managed by fork. Form's validation is implemented with vlad."
  [{:keys [on-click-cancel on-submitted-values tag-color tag-description
           tag-name]
    :or {tag-color "red" tag-description "" tag-name ""}}]
  (let [config {:clean-on-unmount? true
                :initial-values {"color" tag-color
                                 "description" tag-description
                                 "name" tag-name}
                :on-submit (fn [m]
                             (on-submitted-values (dissoc-hints (:values m))))
                :prevent-default? true
                :validation #(vlad/field-errors tag-form-validation %)}
        f (make-tag-form {:on-click-cancel on-click-cancel})]
    [fork/form config f]))

(defn button-add-new-tag-modal
  [{:keys [on-submitted-values]}]
  (let [on-click-cancel #(modal! nil)
        f (fn [values] (on-submitted-values values) (modal! nil))]
    [btn/button {:icon icon-tag
                 :on-click #(modal! [tag-form {:on-click-cancel on-click-cancel
                                               :on-submitted-values f}])
                 :text "Add tag"}]))

(defn button-edit-tag-modal
  [{:keys [tag-color tag-description tag-name on-submitted-values]}]
  (let [on-click-cancel #(modal! nil)
        f (fn [values] (on-submitted-values values) (modal! nil))]
    [btn/button {:color tag-color
                 :icon icon-edit
                 :on-click #(modal! [tag-form {:on-click-cancel on-click-cancel
                                               :on-submitted-values f
                                               :tag-color tag-color
                                               :tag-description tag-description
                                               :tag-name tag-name}])
                 :text tag-name}]))

(defn button-edit-quote-modal
  [{:keys [author tags text on-submitted-values]}]
  (let [on-click-cancel #(modal! nil)
        f (fn [values] (on-submitted-values values) (modal! nil))]
    [btn/button {:icon icon-edit
                 :on-click #(modal! [quote-form {:on-click-cancel on-click-cancel
                                                 :on-submitted-values f
                                                 :tags tags
                                                 :author author
                                                 :text text}])
                 :text "Edit"}]))

(defn delete-quote-dialog
  [{:keys [author on-click-cancel on-click-confirm]}]
  [:div {:class ["bg-white" "shadow-md" "rounded" "px-8" "pt-6" "pb-8"]}
   [:p (str "Are you sure you want to delete this quote by " author "?")]
   [:div {:class ["flex" "items-center" "justify-between"]}
    [btn/button {:on-click on-click-cancel :text "Cancel"}]
    [btn/button {:on-click on-click-confirm :text "Delete"}]]])

(defn button-delete-quote-modal
  [{:keys [on-delete quote-author]}]
  (let [on-click-cancel #(modal! nil)
        on-click-confirm (fn [_]
                           (on-delete quote-author)
                           (modal! nil))]
    [btn/button {:icon icon-trash
                 :on-click #(modal! [delete-quote-dialog {:author quote-author
                                                          :on-click-cancel on-click-cancel
                                                          :on-click-confirm on-click-confirm}])
                 :text "Delete"}]))

(defn make-media-capture-form
  "This function returns a form to upload an image.
  The returned form has no state and no validation."
  [{:keys [on-click-cancel]}]
  (fn form-inner [{:keys [form-id handle-blur handle-change handle-submit
                          set-touched state
                          submitting? values]}]
    ; See onMediaFileSelected
    ; https://github.com/firebase/codelab-friendlychat-web/blob/d7e130877d57930858049cdc0887bdac363163af/cloud-functions-start/public/scripts/main.js#L140
    (let [on-change (fn [^js e]
                      (let [blob (first (.. e -target -files))]
                        (handle-change e)
                        (swap! state assoc-in [:values "blob"] blob)))]
      [:form {:class form-css-classes :id form-id :on-submit handle-submit}
       [:div {:class ["mb-4"]}
        [:label {:class label-css-classes :for "media-capture"} "Image:"]
        [:input {:id "media-capture"
                 :accept "image/*"
                 :auto-focus true
                 :capture "camera"
                 :class input-css-classes
                 :data-testid "media-capture"
                 :name "media-capture"
                 :on-blur handle-blur
                 :on-change on-change
                 :type "file"
                 :value (values "media-capture")}]]
       [:div {:class ["flex" "items-center" "justify-between"]}
        [btn/button {:on-click on-click-cancel :text "Cancel"}]
        [btn/submit {:disabled submitting? :text "Upload"}]]])))

(defn media-capture-form
  [{:keys [on-click-cancel on-submitted-values]}]
  (let [config {:clean-on-unmount? true
                :on-submit (fn [m]
                             (on-submitted-values (dissoc-hints (:values m))))
                :prevent-default? true}
        f (make-media-capture-form {:on-click-cancel on-click-cancel})]
    [fork/form config f]))

(defn button-upload-image
  [{:keys [on-submitted-values]}]
  (let [dismiss-modal #(modal! nil)
        f (fn [values]
            (on-submitted-values values)
            (dismiss-modal))]
    [btn/button {:icon icon-upload-to-cloud
                 :on-click #(modal! [media-capture-form {:on-click-cancel dismiss-modal
                                                         :on-submitted-values f}])
                 :text "Upload Image"}]))
