(ns minimalquotes.firebase.auth
  "Handle authentication in Firebase with several authentication providers."
  (:require
    [minimalquotes.subscriptions :refer
     [subscribe-favorite-quotes! subscribe-user!]]
    [minimalquotes.state :as state]
    [minimalquotes.utils :refer [log-error]]))

(defn sign-in-with-google
  []
  (let [provider (js/firebase.auth.GoogleAuthProvider.)]
    (.signInWithPopup (js/firebase.auth) provider)))

; TODO sign-in-with-github
; TODO sign-in-with-twitter

(defn sign-out
  []
  (.signOut (js/firebase.auth)))

(defn on-next
  "This is the `next` callback for the observer of changes to the user's sign-in
  state."
  [^js/firebase.User user]
  (if user
    (do
      (let [force-refresh-token true]
        (-> (.getIdTokenResult user force-refresh-token)
            (.then (fn [token-result] (subscribe-user! user token-result)))
            (.catch log-error)))
      (subscribe-favorite-quotes!))
    (do
      (prn "No logged in user" user)
      (when-let [unsubscribe-favorite-quotes! (get @state/subscriptions :favorite_quotes)]
        (unsubscribe-favorite-quotes!))
      (reset! state/is-admin-signed-in? false))))

; I would like to make this observer :private, but it's not possible to enforce
; def or defn as private in ClojureScript.
; https://clojurescript.org/about/differences#_special_forms
(def observer #js {:error log-error :next on-next})

(defn on-auth-state-changed
  []
  (.onAuthStateChanged (js/firebase.auth) observer))
