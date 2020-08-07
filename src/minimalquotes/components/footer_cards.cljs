(ns minimalquotes.components.footer-cards
  (:require [devcards.core :as dc :refer [defcard]]
            [minimalquotes.components.footer :refer [footer]]))

(defcard "# Footer component")

(defcard footer-card
  (let [props {}]
    (dc/reagent [:div {:class ["flex" "flex-col" "min-h-screen"]}
                 [:header {:class "bg-red-200"} "header"]
                 [:main {:class "bg-green-200" :style {:flex "1 0 auto"}} "main content"]
                 [footer props]])))
