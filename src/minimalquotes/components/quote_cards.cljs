(ns minimalquotes.components.quote-cards
  (:require
   [devcards.core :as dc :refer [defcard]]
   [minimalquotes.components.quote :refer [quote-card]]
   [minimalquotes.fakes :as fakes]))

(defcard "# Quote")

(defcard "## Quote card for an unauthenticated user")

(defcard quote-unauthenticated-card
  (let [props {:author "Leon Battista Alberti"
               :id fakes/quote-id-0
               :on-share (fn [_] (js/alert (str "anonymous user shares the quote")))
               :tags fakes/tags
               :text "No art, however minor, demands less than total dedication if you want to excel in it"}]
    (dc/reagent [quote-card props])))

(defcard "## Quote card for an authenticated user")

(defcard quote-authenticated-card
  (let [props {:author (:author fakes/quote-0)
               :id fakes/quote-id-0
               :on-delete (fn [_] (js/alert "delete"))
               :on-edit (fn [_] (js/alert "edit"))
               :on-like (fn [user quote-id] (js/alert (str "user " (:display-name user) " likes the quote " quote-id)))
               :on-share (fn [user quote-id] (js/alert (str "user " (:display-name user) " shares the quote " quote-id)))
               :tags fakes/tags
               :text (:text fakes/quote-0)
               :user fakes/user}]
    (dc/reagent [quote-card props])))
