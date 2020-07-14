(ns minimalquotes.components.quotes-cards
  (:require
   [devcards.core :as dc :refer [defcard]]
   [minimalquotes.components.quotes :refer [quote-card quotes]]
   [minimalquotes.fakes :as fakes]))

(defcard "# Quotes")

(defcard "## Quote card for an unauthenticated user")

(declare quote-unauthenticated-card)
(defcard quote-unauthenticated-card
  (let [props {:author "Leon Battista Alberti"
               :id fakes/quote-id-0
               :on-share (fn [_] (js/alert (str "anonymous user shares the quote")))
               :text "No art, however minor, demands less than total dedication if you want to excel in it"
               :user-id nil}]
    (dc/reagent [quote-card props])))

(defcard "## Quote card for an authenticated user")

(declare quote-authenticated-card)
(defcard quote-authenticated-card
  (let [props {:author "Leon Battista Alberti"
               :id fakes/quote-id-0
               :on-delete (fn [_] (js/alert (str "user " fakes/user-id " deletes the quote " fakes/quote-id-0)))
               :on-edit (fn [_] (js/alert (str "user " fakes/user-id " edits the quote " fakes/quote-id-0)))
               :on-like (fn [_] (js/alert (str "user " fakes/user-id " likes the quote " fakes/quote-id-0)))
               :on-share (fn [_] (js/alert (str "user " fakes/user-id " shares the quote " fakes/quote-id-0)))
               :text "No art, however minor, demands less than total dedication if you want to excel in it"
               :user-id fakes/user-id}]
    (dc/reagent [quote-card props])))

(defcard "## Quotes for an unauthenticated user")

(declare quotes-unauthenticated-card)
(defcard quotes-unauthenticated-card
  (let [props {:entries fakes/quotes
               :on-delete-quote (fn [user-id quote-id]
                                  (js/alert (str "user " user-id " deletes quote " quote-id)))
               :on-edit-quote (fn [user-id quote-id]
                                (js/alert (str "user " user-id " edits quote " quote-id)))
               :on-like-quote (fn [user-id quote-id]
                                (js/alert (str "user " user-id " likes quote " quote-id)))
               :on-share-quote (fn [user-id quote-id]
                                 (js/alert (str "user " user-id " shares quote " quote-id)))
               :user-id nil}]
    (dc/reagent [quotes props])))

(defcard "## Quotes for an authenticated user")

(declare quotes-authenticated-card)
(defcard quotes-authenticated-card
  (let [props {:entries fakes/quotes
               :on-delete-quote (fn [user-id quote-id]
                                  (js/alert (str "user " user-id " deletes quote " quote-id)))
               :on-edit-quote (fn [user-id quote-id]
                                (js/alert (str "user " user-id " edits quote " quote-id)))
               :on-like-quote (fn [user-id quote-id]
                                (js/alert (str "user " user-id " likes quote " quote-id)))
               :on-share-quote (fn [user-id quote-id]
                                 (js/alert (str "user " user-id " shares quote " quote-id)))
               :user-id fakes/user-id}]
    (dc/reagent [quotes props])))
