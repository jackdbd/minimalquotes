(ns minimalquotes.state
  (:require [reagent.core :as r]))

;; db will be the Firestore database
(def state
  (r/atom {:db nil
           :favorite-quotes {}
           :is-admin-signed-in? false
           :pagination {:first-doc-snapshot nil :last-doc-snapshot nil}
           :quotes {}
           :subscriptions {}
           :tags {}}))

(def db (r/cursor state [:db]))
(def favorite-quotes (r/cursor state [:favorite-quotes]))
(def is-admin-signed-in? (r/cursor state [:is-admin-signed-in?]))

(def first-quote (r/cursor state [:pagination :first-doc-snapshot]))
(def last-quote (r/cursor state [:pagination :last-doc-snapshot]))

(def quotes (r/cursor state [:quotes]))
(def subscriptions (r/cursor state [:subscriptions]))
(def tags (r/cursor state [:tags]))
