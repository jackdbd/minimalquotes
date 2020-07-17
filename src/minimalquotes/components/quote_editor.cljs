(ns minimalquotes.components.quote-editor
  (:require
   [clojure.string :as str]
   [minimalquotes.components.buttons :as btn]
   [minimalquotes.components.icons :refer [icon-edit icon-trash]]
   [minimalquotes.components.modal :refer [modal!]]
   [minimalquotes.utils :refer [k->str]]))

(def add-quote-form-id "add-quote-form")
(def delete-quote-form-id "delete-quote-form")
(def edit-quote-form-id "edit-quote-form")

(defn form-field-value
  [form-id field]
  (let [selector (str "#" form-id " [data-form-field='" field "']")
        el (.querySelector js/document selector)]
    (. el -value)))

(defn parse-int
  [s]
  (if (= "" s)
    0
    (.parseInt js/window s 10)))

(defn form-field-values
  [form-id]
  {:author (form-field-value form-id "author")
   :tags (.split (form-field-value form-id "tags") ",")
   :text (form-field-value form-id "text")})

(defn form-field
  [{:keys [autofocus id type value] :or {autofocus false type "text"}}]
  [:div {:class ["mb-4"]}
   [:label {:class ["block" "text-gray-700" "text-sm" "font-bold" "mb-2"]
            :for id} (str/capitalize id)]
   [:input {:autoFocus autofocus
            :class "shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            :data-form-field id
            :data-testid (str id "-form-field")
            :defaultValue value
            :id id
            :name id
            :type type}]])

(defn quote-editor-form
  "Form with mutable fields (to avoid re-rendering this component every time we
  make a change to any one of its fields)."
  [{:keys [author id on-cancel on-submit tags text]
    :or {author "" text ""}}]
  [:form {:class ["bg-white" "shadow-md" "rounded" "px-8" "pt-6" "pb-8"]
          :id id
          :on-submit on-submit}
   [form-field {:autofocus true :id "text" :value text}]
   [form-field {:id "author" :value author}]
   [form-field {:id "tags" :value tags}]
   [:div {:class ["flex" "items-center" "justify-between"]}
    [btn/button {:on-click on-cancel
                 :text "Cancel"}]
    [btn/submit]]])

(defn button-add-new-quote-modal
  [{:keys [on-confirm]}]
  (let [on-cancel #(modal! nil)
        on-submit (fn [e]
                    (.preventDefault e)
                    (on-confirm (form-field-values add-quote-form-id))
                    (modal! nil))]
    [btn/button {:text "Add quote"
                 :on-click #(modal! [quote-editor-form {:id add-quote-form-id
                                                        :on-cancel on-cancel
                                                        :on-submit on-submit}])}]))

(defn button-edit-quote-modal
  [{:keys [author tags text on-confirm]}]
  ;; (prn "TAGS in modal" (reduce + "," (map k->str (keys tags))) (interpose "," (keys tags)))
  ;; (prn "interpose" (apply str (interpose "," (map k->str (keys tags)))))
  (let [on-cancel #(modal! nil)
        on-submit (fn [e]
                    (.preventDefault e)
                    (on-confirm (form-field-values edit-quote-form-id))
                    (modal! nil))
        tags-string (apply str (interpose "," (map k->str (keys tags))))]
    [btn/button {:icon icon-edit
                 :on-click #(modal! [quote-editor-form {:author author
                                                        :id edit-quote-form-id
                                                        :on-cancel on-cancel
                                                        :on-submit on-submit
                                                        :tags tags-string
                                                        :text text}])
                 :text "Edit"}]))

(defn delete-quote-form
  [{:keys [author on-cancel on-submit]
    :or {author ""}}]
  [:form {:class ["bg-white" "shadow-md" "rounded" "px-8" "pt-6" "pb-8"]
          :id delete-quote-form-id
          :on-submit on-submit}
   [:p (str "Are you sure you want to delete this quote by " author "?")]
   [:div {:class ["flex" "items-center" "justify-between"]}
    [btn/button {:on-click on-cancel
                 :text "Cancel"}]
    [btn/submit]]])

(defn button-delete-quote-modal
  [{:keys [author on-confirm]}]
  (let [on-cancel #(modal! nil)
        on-submit (fn [e]
                    (.preventDefault e)
                    (on-confirm)
                    (modal! nil))]
    [btn/button {:icon icon-trash
                 :on-click #(modal! [delete-quote-form {:author author
                                                        :on-cancel on-cancel
                                                        :on-submit on-submit}])
                 :text "Delete"}]))
