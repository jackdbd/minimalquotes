(ns minimalquotes.components.quotes-cards
  (:require
   [devcards.core :as dc :refer [defcard]]
   [minimalquotes.components.quotes :refer [quotes]]
   [minimalquotes.fakes :as fakes]))

(defcard "# Quotes component")

(defcard "## Quotes for an unauthenticated user.")

(declare quotes-unauthenticated-card)
(defcard quotes-unauthenticated-card
  (dc/reagent [quotes fakes/quotes]))

(defcard "## Quotes for an authenticated user.")

(declare quotes-authenticated-card)
(defcard quotes-authenticated-card
  (dc/reagent [quotes fakes/quotes]))
