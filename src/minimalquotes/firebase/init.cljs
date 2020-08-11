(ns minimalquotes.firebase.init
  "Initialize Firebase app, Firebase SDKs or emulator suite, and Firebase UI."
  (:require
    [cljs.core.async :refer [chan go put! >!]]
    [cljs.core.async.interop :refer-macros [<p!]]
    [minimalquotes.utils :refer [log-error]]))

(defn init-firebase-ui
  "Initialize the FirebaseUI widget with several authentication providers and a
  redirect URL to visit upon a successful login.
  Note: We cannot call (.start ui) in this function because it's responsability
  of the application to mount the container for the FirebaseUI in the DOM."
  [sign-in-success-url]
  (let [AuthUI (.. js/firebaseui -auth -AuthUI)
        ui (AuthUI. (js/firebase.auth))
        google (.. js/firebase -auth -GoogleAuthProvider -PROVIDER_ID)
        twitter (.. js/firebase -auth -TwitterAuthProvider -PROVIDER_ID)
        email #js
               {:provider (.. js/firebase -auth -EmailAuthProvider -PROVIDER_ID)
                :requireDisplayName true}
        ui-config #js
                   {:signInOptions #js [google twitter email]
                    :signInSuccessUrl sign-in-success-url}]
    {:ui ui :ui-config ui-config}))

;; Environment variables only available in the development build (built with
;; shadow-cljs watch or shadow-cljs compile). These environment variables are
;; defined in a .envrc file (not tracked in version control) and injected via
;; :closure-defines.
(goog-define DEVELOPMENT false)
(goog-define API_KEY "")
(goog-define APP_ID "")
(goog-define AUTH_DOMAIN "")
(goog-define DATABASE_URL "")
(goog-define MEASUREMENT_ID "")
(goog-define MESSAGING_SENDER_ID "")
(goog-define PROJECT_ID "")
(goog-define STORAGE_BUCKET "")

;; Environment variables only available in the production build (built with
;; shadow-cljs release).
(goog-define PRODUCTION false)

(defn config-firebase-sdks
  "Configure Firebase services if the app is deployed, or Firebase emulators if
  the app is running locally."
  [^js app build-type {:keys [on-firebase-ui-initialized
                              on-firebase-ui-config-initialized
                              on-firestore-emulator-initialized
                              on-firestore-initialized]}]
  (if (= "localhost" (.. js/window -location -hostname))
    (do (prn (str "Firebase app " (.-name app) " (" build-type ") is running locally"))
        (let [{:keys [ui ui-config]} (init-firebase-ui "http://localhost:3000/")]
          (on-firebase-ui-initialized ui)
          (on-firebase-ui-config-initialized ui-config))
        (prn "Configure Cloud Functions emulator")
        (doto (.functions app)
          (.useFunctionsEmulator "http://localhost:5001"))
        (on-firestore-emulator-initialized (doto (js/firebase.firestore)
                                             (.settings #js {:host "localhost:8080" :ssl false}))))
    (do
      (js/firebase.analytics)
      (js/firebase.performance)
      (let [{:keys [ui ui-config]} (init-firebase-ui "https://minimalquotes-5c472.web.app/")]
        (on-firebase-ui-initialized ui)
        (on-firebase-ui-config-initialized ui-config))
      (on-firestore-initialized (js/firebase.firestore)))))

(defn init-firebase
  "Initialize Firebase app, services and emulator suite for either a development
  build, or for a production build.
  The DEVELOPMENT build could be configured immediately, but since the
  PRODUCTION build is configured asynchronously, this function always returns a
  core.async channel for consistency."
  [config-callback-map]
  (let [c (chan)]
    (if DEVELOPMENT
      (let [app (js/firebase.initializeApp #js
                                            {:apiKey API_KEY
                                             :appId APP_ID
                                             :authDomain AUTH_DOMAIN
                                             :databaseURL DATABASE_URL
                                             :measurementId MEASUREMENT_ID
                                             :messagingSenderId MESSAGING_SENDER_ID
                                             :projectId PROJECT_ID
                                             :storageBucket STORAGE_BUCKET})]
        (put! c (config-firebase-sdks app "DEVELOPMENT" config-callback-map)))
      (go (try (let [response (<p! (js/fetch "/__/firebase/init.json"))
                     config (<p! (.json response))
                     app (js/firebase.initializeApp config)]
                 (>! c (config-firebase-sdks app "PRODUCTION" config-callback-map)))
               (catch js/Error err
                 (log-error err)))))
    c))
