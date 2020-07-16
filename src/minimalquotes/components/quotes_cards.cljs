(ns minimalquotes.components.quotes-cards
  (:require
   [devcards.core :as dc :refer [defcard]]
   [minimalquotes.components.modal :refer [modal-window]]
   [minimalquotes.components.quotes :refer [quotes]]
   [minimalquotes.fakes :as fakes]))

(defcard "# Quotes")

(defcard "## Quotes for an unauthenticated user")

(defcard quotes-unauthenticated-card
  (let [props {:entries fakes/quotes
               :on-delete-quote (fn [user-id quote-id]
                                  (js/alert (str "user " user-id " deletes quote " quote-id)))
               :on-edit-quote (fn [user-id quote-id]
                                (js/alert (str "user " user-id " edits quote " quote-id)))
               :on-like-quote (fn [user-id quote-id]
                                (js/alert (str "user " user-id " likes quote " quote-id)))
               :on-share-quote (fn [user-id quote-id]
                                 (js/alert (str "user " user-id " shares quote " quote-id)))}]
    (dc/reagent [quotes props])))

(defcard "## Quotes for an authenticated user")

(defn quotes-example
  []
  (let [props {:entries fakes/quotes
               :delete-quote! (fn [quote-id] (js/alert (str "delete quote " quote-id)))
               :edit-quote! (fn [quote-id m-original m-edited]
                              (let [m {:quote-id quote-id
                                       :original m-original
                                       :edited m-edited}]
                                (js/console.log (clj->js m))))
               :on-like-quote (fn [user quote-id]
                                (js/alert (str "user " (:display-name user) " likes quote " quote-id)))
               :on-share-quote (fn [user quote-id]
                                 (js/alert (str "user " (:display-name user) " shares quote " quote-id)))
               :user fakes/user}]
    [:<>
     [modal-window]
     [quotes props]]))

(defcard quotes-authenticated-card
  "Actions that require confirmation (Edit, Delete) are confirmed in a modal
  window. See Edit output in the browser console."
  (dc/reagent [quotes-example]))
