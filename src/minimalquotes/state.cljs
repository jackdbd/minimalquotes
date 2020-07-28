(ns minimalquotes.state
  (:require [reagent.core :as r]))

(def initial-user-state nil)
(def initial-users-state {})

;; db will be the Firestore database
(def state
  (r/atom {:db nil
           :quotes {}
           :subscriptions {}
           :tags {}
           :user initial-user-state
           :users initial-users-state}))

(def db (r/cursor state [:db]))
(def favorite-quotes (r/cursor state [:user :favoriteQuotes]))
(def quotes (r/cursor state [:quotes]))
(def subscriptions (r/cursor state [:subscriptions]))
(def tags (r/cursor state [:tags]))
(def user (r/cursor state [:user]))
(def users (r/cursor state [:users]))
