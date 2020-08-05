(ns minimalquotes.firebase.auth
  "Handle authentication in Firebase with several authentication providers."
  (:require
    ; ["firebase/app" :as firebase]
    [minimalquotes.subscriptions :refer [subscribe-user!]]
    [minimalquotes.state :as state]
    [minimalquotes.utils :refer [log-error]]))

(defn sign-in-with-google
  []
  (let [provider (js/firebase.auth.GoogleAuthProvider.)]
    (.signInWithPopup (js/firebase.auth) provider)))

; TODO sign-in-with-facebook
; TODO sign-in-with-github
; TODO sign-in-with-twitter

(defn sign-out
  []
  (prn "=== SIGN OUT ===")
  (.signOut (js/firebase.auth))
  (prn "State after sign-out" @state/state))

(defn on-next
  "This is the `next` callback for the observer of changes to the user's sign-in
  state."
  [^js/firebase.User user]
  (prn "ON NEXT user" user)
  (if user
    (let [force-refresh-token true]
      (-> (.getIdTokenResult user force-refresh-token)
          (.then (fn [token-result] (subscribe-user! user token-result)))
          (.catch log-error)))
    (do
      (prn "No logged in user" user)
      (reset! state/is-admin-signed-in? false))))

; I would like to make this observer :private, but it's not possible to enforce
; def or defn as private in ClojureScript.
; https://clojurescript.org/about/differences#_special_forms
(def observer #js {:error log-error :next on-next})

(defn on-auth-state-changed [] (.onAuthStateChanged (js/firebase.auth) observer))
