(ns minimalquotes.components.app
  (:require [minimalquotes.components.header :refer [header]]
            [minimalquotes.components.modal :refer [modal-window]]
            [minimalquotes.components.quotes :refer [quotes-container]]
            [minimalquotes.firebase.auth :as auth]
            [minimalquotes.state :as state]))

;; Useful for debugging
(defn f
  [[id m]]
  (prn "id" id "m" m)
  ^{:key id} [:li (str (:text m) " -- " (:author m))])

(defn app
  []
  (let [user @state/user]
    [:<> [modal-window]
     [:div {:class ["container"]}
      [header
       {:on-login #(auth/sign-in-with-google),
        :on-logout #(auth/sign-out),
        :user user}]
      ;; [:ul (map f @state/quotes)]
      [quotes-container]]]))
