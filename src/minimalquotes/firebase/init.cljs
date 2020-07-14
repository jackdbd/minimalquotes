(ns minimalquotes.firebase.init
  "Initialize Firebase with all the SDKs used in this app."
  (:require
   ["firebase/app" :as firebase]
   ;; require Firebase SDKs for side-effects
   ["firebase/analytics"]
   ["firebase/auth"]
   ["firebase/firestore"]
   [minimalquotes.firebase.auth :refer [on-auth-state-changed]]
   [minimalquotes.state :as state]))

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
  (reset! state/db (firebase/firestore))
  (on-auth-state-changed))
