(ns minimalquotes.state
  (:require [reagent.core :as r]))

;; db will be the Firestore database
(def state (r/atom {:db nil
                    :quotes {}
                    :user {}}))

(def db (r/cursor state [:db]))
(def quotes (r/cursor state [:quotes]))
(def user (r/cursor state [:user]))
