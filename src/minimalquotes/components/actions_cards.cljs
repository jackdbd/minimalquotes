(ns minimalquotes.components.actions-cards
  (:require
   [devcards.core :as dc :refer [defcard]]
   [minimalquotes.components.actions :refer [actions]]
   [minimalquotes.components.modal :refer [modal-window]]
   [minimalquotes.fakes :as fakes]))

(defcard "# Actions")

(defcard "## Actions for an unauthenticated user")

; TODO: how to share a quote for an unauthenticated user?
(defn on-share
  [user quote-id]
  (js/alert (str (:display-name user) " shares the quote: " quote-id)))

(defn on-like
  [user quote-id]
  (js/alert (str (:display-name user) " likes the quote: " quote-id)))

(defcard actions-unauthenticated-card
  "Actions available for an unauthenticated user."
  (let [props {:on-share on-share}]
    (dc/reagent [actions props])))

(defcard "## Actions for an authenticated user")

(defn on-click-action
  [tag-name]
  (js/alert (str "click tag: " tag-name)))

(defn actions-example
  []
  (let [props {:author fakes/author-0
               :delete! (fn []
                          (js/alert "delete"))
               :edit! (fn [m-form]
                        (js/alert (js/JSON.stringify (clj->js m-form) nil 2)))
               :id fakes/quote-id-0
               :on-click-action on-click-action
               :on-like on-like
               :on-share on-share
               :tags fakes/tags
               :text fakes/text-0
               :user fakes/user}]
    [:<>
     [modal-window]
     [actions props]]))

(defcard actions-authenticated-card
  "Actions that require confirmation (Edit, Delete) must be confirmed in a modal
  window. Like and Share require no modals."
  (dc/reagent [actions-example]))
