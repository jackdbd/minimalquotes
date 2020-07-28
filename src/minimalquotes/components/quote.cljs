(ns minimalquotes.components.quote
  (:require [minimalquotes.components.actions :refer [actions]]
            [minimalquotes.components.tags :as tags-ns]))

; (def debug-css-card "bg-blue-300")
(def debug-css-card "")

; (def debug-css "bg-gray-300")
(def debug-css "")

(def pattern-classes "bg-flat-mountains")
; (def pattern-classes "")

(defn quote-card
  "A card for a single quote. If the user is authenticated, he can
  edit/delete/like/share the quote."
  [{:keys [id like-button-text on-delete on-edit on-share on-toggle-like
           quote-author quote-text tags unlike-button-text user]
    :or {like-button-text "Like" unlike-button-text "Unlike"}}]
  (let [k (keyword id)
        liked-quote (k (:favoriteQuotes user))]
    [:div
     {:class ["quote-card" "rounded-lg" "overflow-hidden" "shadow-lg" "p-4"
              pattern-classes "flex" "flex-col" "justify-between" debug-css-card
              "max-w-sm sm:max-w-md md:max-w-lg lg:max-w-xl xl:max-w-2xl"]}
     [:p {:class ["text-gray-800"]} [:span {:class "quote-text"} quote-text]
      [:span " ― "] [:span {:class "quote-author"} quote-author]]
     [:div {:class [debug-css]} [tags-ns/tags {:entries tags}]
      [actions
       {:id id
        :like-button-text like-button-text
        :liked-quote liked-quote
        :on-delete on-delete
        :on-edit on-edit
        :on-share on-share
        :on-toggle-like on-toggle-like
        :tags tags
        :quote-author quote-author
        :quote-text quote-text
        :unlike-button-text unlike-button-text
        :user user}]]]))
