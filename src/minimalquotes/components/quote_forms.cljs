(ns minimalquotes.components.quote-forms
  (:require [fork.reagent :as fork]
            [minimalquotes.components.buttons :as btn]
            [minimalquotes.components.icons :refer [icon-edit icon-trash]]
            [minimalquotes.components.modal :refer [modal!]]
            ["react-bootstrap-typeahead" :refer [Typeahead]]
            [vlad.core :as vlad]))

(def form-css-classes ["bg-white" "shadow-md" "rounded" "px-8" "pt-6" "pb-8"])
(def label-css-classes ["block" "text-gray-700" "text-sm" "font-bold" "mb-2"])
(def input-css-classes
  ["shadow" "appearance-none" "rounded" "w-full" "border" "leading-tight"
   "text-gray-700" "py-2 px-3" "focus:outline-none" "focus:shadow-outline"])

; (defn tag->option
;   [[tag-id tag]]
;   ^{:key tag-id} [:option {:value (:name tag)}])

; (defn f->li
;   []
;   [:li "one datalist with all tags, per tag selected"])

(defn tag->tag-with-id [[tag-id tag]] (merge tag {:id tag-id}))

(defn- make-quote-form
  [{:keys [on-click-cancel tags]}]
  (fn quote-form-inner [{:keys [errors form-id handle-blur handle-change
                                handle-submit submitting? state touched
                                values]}]
    [:form {:class form-css-classes, :id form-id, :on-submit handle-submit}
     [:div {:class ["mb-4"]}
      [:label {:class label-css-classes, :for "quote-text"} "Text:"]
      [:input
       {:id "quote-text",
        :auto-focus true,
        :class input-css-classes,
        :data-testid "quote-text",
        :name "quote-text",
        :on-blur handle-blur,
        :on-change handle-change,
        :value (values "quote-text")}]
      (when (touched "quote-text")
        [:div (first (get errors (list "quote-text")))])]
     [:div {:class ["mb-4"]}
      [:label {:class label-css-classes, :for "quote-author"} "Author:"]
      [:input
       {:id "quote-author",
        :class input-css-classes,
        :data-testid "quote-author",
        :name "quote-author",
        :on-blur handle-blur,
        :on-change handle-change,
        :value (values "quote-author")}]
      (when (touched "quote-author")
        [:div (first (get errors (list "quote-author")))])]
     [:div {:class ["mb-4"]}
      [:label {:class label-css-classes, :for "input-tags-typeahead"} "Tags:"]
      [:> Typeahead
       {:class-name input-css-classes,
        :clear-button true,
        ;  :default-selected [(first (vals tags)) (last (vals tags))]
        :id "input-tags-typeahead",
        :label-key "name",
        :multiple true,
        :on-blur handle-blur,
        :on-change (fn [^js js-values]
                     (let [values (js->clj js-values)
                           names (map #(get % "name") values)]
                       (swap! state assoc-in [:values :tags] names))),
        :options (map tag->tag-with-id tags)}]]
     [:div {:class ["flex" "items-center" "justify-between"]}
      [btn/button {:on-click on-click-cancel, :text "Cancel"}]
      [btn/submit {:disabled submitting?, :text "Confirm"}]]]))

(def validation
  (vlad/join (vlad/attr ["quote-text"]
                        (vlad/chain (vlad/present) (vlad/length-over 5)))
             (vlad/attr ["quote-author"]
                        (vlad/chain (vlad/present) (vlad/length-in 1 15)))))

(defn quote-form
  "Stateful form to create/edit a quote.
  Form's state is managed by fork. Form's validation is implemented with vlad."
  [{:keys [on-click-cancel on-submitted-values quote-author quote-text tags],
    :or {quote-author "", quote-text ""}}]
  ; TODO: make :default-selected tags with ids
  (let [config {:clean-on-unmount? true,
                :initial-values {"quote-author" quote-author,
                                 "quote-text" quote-text},
                :on-submit (fn [m] (on-submitted-values (:values m))),
                :prevent-default? true,
                :validation #(vlad/field-errors validation %)}
        f (make-quote-form {:on-click-cancel on-click-cancel, :tags tags})]
    [fork/form config f]))

(defn button-add-new-quote-modal
  [{:keys [on-submitted-values tags]}]
  (let [on-click-cancel #(modal! nil)
        f (fn [values] (on-submitted-values values) (modal! nil))]
    [btn/button
     {:on-click #(modal! [quote-form
                          {:on-click-cancel on-click-cancel,
                           :on-submitted-values f,
                           :tags tags}]),
      :text "Add quote"}]))

(defn button-edit-quote-modal
  [{:keys [quote-author tags quote-text on-submitted-values]}]
  (let [on-click-cancel #(modal! nil)
        f (fn [values] (on-submitted-values values) (modal! nil))]
    (prn "button-edit-quote-modal" tags)
    [btn/button
     {:icon icon-edit,
      :on-click #(modal! [quote-form
                          {:on-click-cancel on-click-cancel,
                           :on-submitted-values f,
                           :tags tags,
                           :quote-author quote-author,
                           :quote-text quote-text}]),
      :text "Edit"}]))

(defn delete-quote-dialog
  [{:keys [quote-author on-click-cancel on-click-confirm]}]
  [:div {:class ["bg-white" "shadow-md" "rounded" "px-8" "pt-6" "pb-8"]}
   [:p (str "Are you sure you want to delete this quote by " quote-author "?")]
   [:div {:class ["flex" "items-center" "justify-between"]}
    [btn/button {:on-click on-click-cancel, :text "Cancel"}]
    [btn/button {:on-click on-click-confirm, :text "Delete"}]]])

(defn button-delete-quote-modal
  [{:keys [on-delete quote-author]}]
  (let [on-click-cancel #(modal! nil)
        on-click-confirm (fn [_] (on-delete quote-author) (modal! nil))]
    [btn/button
     {:icon icon-trash,
      :on-click #(modal! [delete-quote-dialog
                          {:quote-author quote-author,
                           :on-click-cancel on-click-cancel,
                           :on-click-confirm on-click-confirm}]),
      :text "Delete"}]))
