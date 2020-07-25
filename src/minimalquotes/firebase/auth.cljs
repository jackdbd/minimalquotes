(ns minimalquotes.firebase.auth
  "Handle authentication in Firebase with several authentication providers."
  (:require ["firebase/app" :as firebase]
            [minimalquotes.firebase.firestore :refer [add-user-if-first-time!]]
            [minimalquotes.state :as state]
            [minimalquotes.subscriptions :refer [subscribe-user!]]
            [minimalquotes.utils :refer [log-error]]))

(defn sign-in-with-google
  []
  (let [provider (firebase/auth.GoogleAuthProvider.)]
    (.signInWithPopup (firebase/auth) provider)))

; TODO sign-in-with-facebook
; TODO sign-in-with-github
; TODO sign-in-with-twitter

(defn sign-out [] (.signOut (firebase/auth)))

(defn on-next
  "This is the `next` callback for the observer of changes to the user's sign-in
  state."
  [^js auth-user]
  (if auth-user
    (let [uid (goog.object/get auth-user "uid")]
      (add-user-if-first-time!
       {:auth-user auth-user, :firestore @state/db, :uid uid})
      (subscribe-user! uid))
    (when-let [unsubscribe-user! (get @state/subscriptions :user)]
      (unsubscribe-user!)
      (reset! state/user nil))))

; I would like to make this observer :private, but it's not possible to enforce
; def or defn as private in ClojureScript.
; https://clojurescript.org/about/differences#_special_forms
(def observer #js {:error log-error, :next on-next})

(defn on-auth-state-changed [] (.onAuthStateChanged (firebase/auth) observer))
