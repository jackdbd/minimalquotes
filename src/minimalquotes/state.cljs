(ns minimalquotes.state
  (:require [reagent.core :as r]))

;; db will be the Firestore database
(def state
  (r/atom
   {:db nil, :quotes {}, :subscriptions {}, :tags {}, :user nil, :users {}}))

(def db (r/cursor state [:db]))
(def quotes (r/cursor state [:quotes]))
(def subscriptions (r/cursor state [:subscriptions]))
(def tags (r/cursor state [:tags]))
(def user (r/cursor state [:user]))
(def users (r/cursor state [:users]))
