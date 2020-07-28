(ns minimalquotes.components.actions-cards
  (:require [devcards.core :as dc :refer [defcard]]
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
         (let [props {:on-share on-share}] (dc/reagent [actions props])))

(defcard "## Actions for an authenticated user")

(defn on-click-action [tag-name] (js/alert (str "click tag: " tag-name)))

(defn actions-example
  []
  (let [props
        {:id fakes/quote-id-0
         :on-click-action on-click-action
         :on-delete (fn [author] (js/alert (str "Delete quote by " author)))
         :on-edit (fn [values]
                    (js/alert (js/JSON.stringify (clj->js values) nil 2)))
         :on-like on-like
         :on-share on-share
         :quote-author fakes/author-0
         :quote-text fakes/text-0
         :tags fakes/tags
         :user fakes/user}]
    [:<> [modal-window] [actions props]]))

(defcard
  actions-authenticated-card
  "Actions that require confirmation (Edit, Delete) must be confirmed in a modal
  window. Like and Share require no modals."
  (dc/reagent [actions-example]))
