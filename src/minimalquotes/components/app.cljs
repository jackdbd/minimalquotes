(ns minimalquotes.components.app
  (:require
   [minimalquotes.components.header :refer [header]]
   [minimalquotes.components.quotes :refer [quotes-container]]
   [minimalquotes.firebase.auth :as auth]
   [minimalquotes.state :as state]))

(defn app
  []
  (let [user @state/user]
    [:div {:class ["container"]}
     [header {:on-login #(auth/sign-in-with-google)
              :on-logout #(auth/sign-out)
              :user user}]
     [quotes-container]]))
