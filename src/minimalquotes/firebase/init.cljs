(ns minimalquotes.firebase.init
  "Initialize Firebase with all the SDKs used in this app."
  (:require
   ["firebase/app" :as firebase]
   ;; require Firebase SDKs for side-effects
   ["firebase/analytics"]
   ["firebase/auth"]
   ["firebase/firestore"]
   ["firebase/performance"]
   ["firebaseui" :as firebaseui]
   [minimalquotes.firebase.auth :refer [on-auth-state-changed]]
   [minimalquotes.state :as state]))

(defn init-firebase-ui!
  "Initialize the FirebaseUI widget with authentication providers and a redirect
  to a URL. We cannot mount it in the DOM yet."
  [sign-in-success-url]
  (let [AuthUI (.. firebaseui -auth -AuthUI)
        ui (AuthUI. (firebase/auth))
        google (.. firebase -auth -GoogleAuthProvider -PROVIDER_ID)
        facebook (.. firebase -auth -FacebookAuthProvider -PROVIDER_ID)
        twitter (.. firebase -auth -TwitterAuthProvider -PROVIDER_ID)
        email #js {:provider (.. firebase -auth -EmailAuthProvider -PROVIDER_ID)
                   :requireDisplayName true}
        ui-config #js {:signInOptions #js [google facebook twitter email]
                       :signInSuccessUrl sign-in-success-url}]
    (swap! state/state assoc :firebase-ui ui)
    (swap! state/state assoc :firebase-ui-config ui-config)))

(defn init-firebase!
  "Initialize Firebase auth and Firestore database."
  []
  (firebase/initializeApp
   #js {:apiKey "AIzaSyALIE7Cxbr2X9158aPJtsO_DkwQaPUQqxU"
        :appId "1:152587589583:web:7bb705180bd399576524ba"
        :authDomain "minimalquotes-5c472.firebaseapp.com"
        :databaseURL "https://minimalquotes-5c472.firebaseio.com"
        :measurementId "G-GHSLN6SEZE"
        :messagingSenderId "152587589583"
        :projectId "minimalquotes-5c472"
        :storageBucket "minimalquotes-5c472.appspot.com"})
  (firebase/analytics)
  (firebase/performance)
  (init-firebase-ui! "http://localhost:3000/")
  (reset! state/db (firebase/firestore))
  (on-auth-state-changed))
