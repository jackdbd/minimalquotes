(ns minimalquotes.components.footer-cards
  (:require [devcards.core :as dc :refer [defcard]]
            [minimalquotes.components.footer :refer [footer]]))

(defcard "# Footer component")

(defcard footer-card (let [props {}] (dc/reagent [footer props])))
