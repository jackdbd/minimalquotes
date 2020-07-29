(ns minimalquotes.state
  (:require [reagent.core :as r]))

;; db will be the Firestore database
(def state
  (r/atom {:db nil
           :is-admin-signed-in? false
           :quotes {}
           :subscriptions {}
           :tags {}}))

(def db (r/cursor state [:db]))
(def favorite-quotes (r/cursor state [:user :favoriteQuotes]))
(def is-admin-signed-in? (r/cursor state [:is-admin-signed-in?]))
(def quotes (r/cursor state [:quotes]))
(def subscriptions (r/cursor state [:subscriptions]))
(def tags (r/cursor state [:tags]))
;; (def user (r/cursor state [:user]))
;; (def users (r/cursor state [:users]))
