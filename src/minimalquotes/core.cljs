(ns minimalquotes.core
  "Entry point of the app."
  (:require
    [accountant.core :as accountant]
    [clerk.core :as clerk]
    [goog.object :as object]
    [minimalquotes.components.error-boundary :refer [error-boundary]]
    [minimalquotes.pages.core :refer [current-page page-for]]
    [minimalquotes.firebase.init :refer [init-firebase!]]
    [minimalquotes.routes :refer [router]]
    [minimalquotes.subscriptions :refer [subscribe-tags!]]
    [reagent.core :as r]
    [reagent.dom :as rdom]
    [reagent.session :as session]
    [reitit.frontend :as rf]))

; (defn callback [^js entries ^js observer]
;   (prn "entries" entries "observer" observer))
; (def options #js {:root (js/document.querySelector "#app")
;                   :threshold 1.0})
; (def observer (js/IntersectionObserver. callback options))
; (prn "options" options)
; (prn "IntersectionObserver" observer)

; (def target (js/document.querySelector "#foo"))
; (prn "target" target)
; (.observe observer target)

(defn nav-handler
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

(defn on-catch
  [^js err ^js info]
  (let [component-stack (object/getValueByKeys info #js ["componentStack"])
        error-stack (object/getValueByKeys err #js ["stack"])]
    (js/console.warn "TODO: send to Sentry" "component-stack" component-stack)
    (js/console.warn "TODO: send to Sentry" "error-stack" error-stack)))

(defn ^:dev/after-load mount-root
  "Render the top-level component for this app."
  []
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [error-boundary {:on-catch on-catch}
                  [current-page]] root-el)))

(defn ^:export main
  "Run application startup logic."
  []
  (when goog.DEBUG (dev-setup))
  (init-firebase!)
  (subscribe-tags!)
  (hook-browser-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
