(ns minimalquotes.firebase.auth
  "Handle authentication in Firebase with several authentication providers."
  (:require
   ["firebase/app" :as firebase]
   [minimalquotes.firebase.firestore :refer [db-path-upsert!]]
   [minimalquotes.state :as state]))

(defn sign-in-with-google
  []
  (let [provider (firebase/auth.GoogleAuthProvider.)]
    (.signInWithPopup (firebase/auth) provider)))

; TODO sign-in-with-facebook
; TODO sign-in-with-github
; TODO sign-in-with-twitter

(defn sign-out
  []
  (.signOut (firebase/auth)))

(defn- next-or-observer
  "Observer for changes to the user's sign-in state."
  [^js user]
  (if user
    (let [uid (.-uid user)
          m {:display-name (.-displayName user)
             :email (.-email user)
             :photo-url (.-photoURL user)
             :uid uid}]
      (reset! state/user m)
      (db-path-upsert! {:doc-path (str "users/" uid) :firestore @state/db :m m}))
    (reset! state/user nil)))

(defn- on-error [e]
  (js/console.error "=== Error: onAuthStateChanged ===" e))

(defn on-auth-state-changed
  "Adds an observer for changes to the user's sign-in state."
  []
  (.onAuthStateChanged (firebase/auth) next-or-observer on-error))
