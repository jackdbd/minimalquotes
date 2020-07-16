(ns minimalquotes.components.quote
  (:require
   [minimalquotes.components.actions :refer [actions]]
   [minimalquotes.components.tags :as tags-ns]))

; (def debug-css-card "bg-blue-300")
(def debug-css-card "")

; (def debug-css "bg-gray-300")
(def debug-css "")

(defn quote-card
  "A card for a single quote. If the user is authenticated, he can
  edit/delete/like/share the quote."
  [{:keys [author delete! edit! id on-like on-share tags text user]}]
  [:div {:class ["rounded-lg" "overflow-hidden" "shadow-lg"
                 "p-4"
                 "quote"
                 "flex" "flex-col" "justify-between"
                 debug-css-card
                 "max-w-sm sm:max-w-md md:max-w-lg lg:max-w-xl xl:max-w-2xl"]}
   [:p (str text " ― " author)]
   [:div {:class [debug-css]}
    [tags-ns/tags {:entries tags}]
    [actions {:author author
              :delete! delete!
              :edit! edit!
              :id id
              :on-like on-like
              :on-share on-share
              :tags tags
              :text text
              :user user}]]])
