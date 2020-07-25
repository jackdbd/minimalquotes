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
  [{:keys [id on-delete on-edit on-like on-share quote-author quote-text tags
           user]}]
  (prn "=== quote-card tags ===" tags)
  [:div
   {:class ["rounded-lg" "overflow-hidden" "shadow-lg" "p-4" "quote"
            pattern-classes "flex" "flex-col" "justify-between" debug-css-card
            "max-w-sm sm:max-w-md md:max-w-lg lg:max-w-xl xl:max-w-2xl"]}
   [:p {:class ["text-gray-800"]} (str quote-text " ― " quote-author)]
   [:div {:class [debug-css]} [tags-ns/tags {:entries tags}]
    [actions
     {:id id,
      :on-delete on-delete,
      :on-edit on-edit,
      :on-like on-like,
      :on-share on-share,
      :tags tags,
      :quote-author quote-author,
      :quote-text quote-text,
      :user user}]]])
