(ns minimalquotes.core
  "Entry point of the app."
  (:require
    [accountant.core :as accountant]
    [clerk.core :as clerk]
    [cljs.core.async :refer [go <!]]
    [goog.object :as object]
    [lambdaisland.glogi :as log]
    [lambdaisland.glogi.console :as glogi-console]
    [minimalquotes.components.error-boundary :refer [sentry-error-boundary]]
    [minimalquotes.components.global-catch-all :refer [global-catch-all]]
    [minimalquotes.firebase.auth :refer [on-auth-state-changed]]
    [minimalquotes.firebase.init :refer [init-firebase]]
    [minimalquotes.pages.core :refer [current-page page-for]]
    [minimalquotes.routes :refer [router]]
    [minimalquotes.state :as state]
    [minimalquotes.subscriptions :refer [subscribe-tags!]]
    [minimalquotes.utils :refer [log-error]]
    [reagent.core :as r]
    [reagent.dom :as rdom]
    [reagent.session :as session]
    [reitit.frontend :as rf]
    ["@sentry/react" :as Sentry]))

(glogi-console/install!)
(log/set-levels {:glogi/root :info})
(log/info :hello {:message "Setup logger"})

(defn nav-handler
  "Navigation handler for accountant."
  [path]
  (let [match (rf/match-by-path router path)
        name (:name (:data match))
        route-params (:path-params match)
        query-params (:query-params match)]
    (r/after-render clerk/after-render!)
    (session/put! :route {:current-page (page-for name)
                          :query-params query-params
                          :route-params route-params})
    (clerk/navigate-page! path)))

(defn path-exists?
  [path]
  (boolean (rf/match-by-path router path)))

(defn hook-browser-navigation!
  "Replace the browser's scrolling restoration with clerk's and configure
  accountant as History API navigation manager."
  []
  (clerk/initialize!)
  (accountant/configure-navigation! {:nav-handler nav-handler
                                     :path-exists? path-exists?}))

(defn dev-setup
  []
  ;; allow to use (prn "foo") in place of (.log js/console "foo")
  (enable-console-print!))

(defn on-error-event
  "Listener for an ErrorEvent. To be used in a global-catch-all component.
  It would be cool to send all app'state to Sentry (serialized), but if the
  message payload is too big, Sentry responds with HTTP 413."
  [^js err-ev]
  (let [err (object/getValueByKeys err-ev #js ["error"])]
    (.captureMessage Sentry #js
                             {"appState" #js {"tags" (js/JSON.stringify (clj->js @state/tags) nil 2)}
                              "errorMessage" (.. err -message)
                              "isAdminSignedIn" @state/is-admin-signed-in?
                              "reporter" "global-catch-all component"})))

(defn init-sentry!
  "Initialize Sentry for error reporting.
  Do not automatically send all errors to Sentry, only unhandled rejections.
  https://forum.sentry.io/t/recommended-way-to-initialize-a-sentry-browser-client/5098/3"
  []
  (.init Sentry #js
                 {:dsn "https://89ca4db4f3b740e6b62af5f64b42a089@o157166.ingest.sentry.io/5392842"
                  :integrations #js
                                  [(Sentry/Integrations.GlobalHandlers. #js {:onerror false :onunhandledrejection true})]}))

(defn ^:dev/after-load mount-root
  "Render the top-level component for this app."
  []
  (init-sentry!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [global-catch-all {:on-error-event on-error-event}
                  [sentry-error-boundary
                   [current-page]]]
                 root-el)))

(def config-callback-map
  "Callbacks to invoke when Firebase SDKs are initialized."
  {:on-firebase-ui-initialized (fn [ui]
                                 (prn "Firebase UI configured")
                                 (swap! state/state assoc :firebase-ui ui))
   :on-firebase-ui-config-initialized (fn [ui-config]
                                        (swap! state/state assoc :firebase-ui-config ui-config))
   :on-firestore-emulator-initialized (fn [firestore]
                                        (prn "Firestore emulator configured")
                                        (reset! state/db firestore))
   :on-firestore-initialized (fn [firestore]
                               (prn "Firestore configured")
                               (reset! state/db firestore))})

(defn ^:export main
  "Run application startup logic."
  []
  (when goog.DEBUG (dev-setup))
  (go (try (<! (init-firebase config-callback-map))
           (on-auth-state-changed)
           (subscribe-tags!)
           (hook-browser-navigation!)
           (accountant/dispatch-current!)
           (mount-root)
           (catch js/Error err
             (log-error err)))))
