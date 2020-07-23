(ns minimalquotes.components.quote-cards
  (:require [devcards.core :as dc :refer [defcard]]
            [minimalquotes.components.modal :refer [modal-window]]
            [minimalquotes.components.quote :refer [quote-card]]
            [minimalquotes.fakes :as fakes]))

(defcard "# Quote")

(defcard "## Quote card for an unauthenticated user")

(defcard
 quote-unauthenticated-card
 (let
   [props
    {:id fakes/quote-id-0,
     :on-share (fn [_] (js/alert (str "anonymous user shares the quote"))),
     :quote-author "Leon Battista Alberti",
     :quote-text
     "No art, however minor, demands less than total dedication if you want to excel in it",
     :tags fakes/tags}]
   (dc/reagent [quote-card props])))

(defcard "## Quote card for an authenticated user")

(defn quote-example
  []
  (let [props {:id fakes/quote-id-0,
               :on-delete (fn [_] (js/alert "delete")),
               :on-edit (fn [_] (js/alert "edit")),
               :on-like (fn [user quote-id]
                          (js/alert (str "user " (:display-name user)
                                         " likes the quote " quote-id))),
               :on-share (fn [user quote-id]
                           (js/alert (str "user " (:display-name user)
                                          " shares the quote " quote-id))),
               :tags fakes/tags,
               :quote-author (:author fakes/quote-0),
               :quote-text (:text fakes/quote-0),
               :user fakes/user}]
    [:<> [modal-window] [quote-card props]]))

(defcard
 quote-authenticated-card
 "Actions that require confirmation (Edit, Delete) must be confirmed in a modal
  window."
 (dc/reagent [quote-example]))
