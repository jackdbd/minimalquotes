(ns minimalquotes.firebase.init
  "Initialize Firebase and Firebase UI."
  (:require
    [minimalquotes.firebase.auth :refer [on-auth-state-changed]]
    [minimalquotes.state :as state]))

(defn init-firebase-ui!
  "Initialize the FirebaseUI widget with authentication providers and a redirect
  to a URL. We cannot mount it in the DOM yet."
  [sign-in-success-url]
  (let [AuthUI (.. js/firebaseui -auth -AuthUI)
        ui (AuthUI. (js/firebase.auth))
        google (.. js/firebase -auth -GoogleAuthProvider -PROVIDER_ID)
        facebook (.. js/firebase -auth -FacebookAuthProvider -PROVIDER_ID)
        twitter (.. js/firebase -auth -TwitterAuthProvider -PROVIDER_ID)
        email #js
               {:provider (.. js/firebase -auth -EmailAuthProvider -PROVIDER_ID)
                :requireDisplayName true}
        ui-config #js
                   {:signInOptions #js [google facebook twitter email]
                    :signInSuccessUrl sign-in-success-url}]
    (swap! state/state assoc :firebase-ui ui)
    (swap! state/state assoc :firebase-ui-config ui-config)))

(defn init-firebase!
  "Initialize Firebase app and services."
  []
  (let [app (js/firebase.initializeApp
              #js
               {:apiKey "AIzaSyALIE7Cxbr2X9158aPJtsO_DkwQaPUQqxU"
                :appId "1:152587589583:web:7bb705180bd399576524ba"
                :authDomain "minimalquotes-5c472.firebaseapp.com"
                :databaseURL "https://minimalquotes-5c472.firebaseio.com"
                :measurementId "G-GHSLN6SEZE"
                :messagingSenderId "152587589583"
                :projectId "minimalquotes-5c472"
                :storageBucket "minimalquotes-5c472.appspot.com"})]
    (if (= "localhost" (.. js/window -location -hostname))
      (do (prn (str "Firebase app " (.-name app) " is running locally"))
          (prn "Configure Functions emulator")
          (doto (.functions app)
            (.useFunctionsEmulator "http://localhost:5001"))
          (init-firebase-ui! "http://localhost:3000/")
          (prn "Configure Firestore emulator")
          (reset! state/db
                  (doto (js/firebase.firestore)
                    (.settings #js {:host "localhost:8080" :ssl false}))))
      (do
        (js/firebase.analytics)
        (js/firebase.performance)
        (init-firebase-ui! "https://minimalquotes-5c472.web.app/")
        (reset! state/db (js/firebase.firestore)))))
  (on-auth-state-changed))
