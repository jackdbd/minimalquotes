(ns minimalquotes.firebase.auth
  "Handle authentication in Firebase with several authentication providers."
  (:require
   ["firebase/app" :as firebase]
   [minimalquotes.firebase.firestore :refer [db-docs-subscribe! db-path-upsert!]]
   [minimalquotes.state :as state]))

(defn sign-in-with-google
  []
  (println "sign-in-with-google")
  (let [provider (firebase/auth.GoogleAuthProvider.)]
    (.signInWithPopup (firebase/auth) provider)))

; TODO sign-in-with-facebook
; TODO sign-in-with-github
; TODO sign-in-with-twitter

(defn sign-out
  []
  (println "sign-out")
  (.signOut (firebase/auth)))

(defn- next-or-observer
  "Observer for changes to the user's sign-in state."
  [user]
  ; (println "=== user ===" (js/JSON.stringify user))
  (if user
    (let [uid (.-uid user)
          m {:display-name (.-displayName user)
             :email (.-email user)
             :photo-url (.-photoURL user)
             :uid uid}]
      (db-docs-subscribe! {:collection "quotes"
                           :firestore @state/db
                           :ratom-collection state/quotes})
      (reset! state/user m)
      (db-path-upsert! {:doc-path (str "users/" uid) :m m}))
    (reset! state/user nil)))

(defn- on-error [e]
  (.error js/console "ERROR ===" e))

(defn on-auth-state-changed
  "Adds an observer for changes to the user's sign-in state."
  []
  (println "on-auth-state-changed")
  (.onAuthStateChanged (firebase/auth) next-or-observer on-error))
