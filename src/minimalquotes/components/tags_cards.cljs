(ns minimalquotes.components.tags-cards
  (:require
    [devcards.core :as dc :refer [defcard]]
    [minimalquotes.components.tags :refer [tag tags]]
    [minimalquotes.fakes :as fakes]))

(defcard "# Tag & Tags")

(defcard "## Tag")

(defcard tag-card
  (dc/reagent [tag fakes/tag-love]))

(defcard "## Tags")

(defcard tags-card
  "Tags with default margins and no click handler."
  (let [props {:entries fakes/tags}] (dc/reagent [tags props])))

(defcard tags-custom-margins-card
  "Tags with custom margins."
  (let [props {:entries fakes/tags
               :margin-tailwind-class "m-3"}]
    (dc/reagent [tags props])))
