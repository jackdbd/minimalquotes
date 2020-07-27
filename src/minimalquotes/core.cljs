(ns minimalquotes.core
  (:require [accountant.core :as accountant]
            [clerk.core :as clerk]
            [minimalquotes.components.error-boundary :refer [error-boundary]]
            [minimalquotes.pages.core :refer [current-page page-for]]
            [minimalquotes.firebase.init :refer [init-firebase!]]
            [minimalquotes.routes :refer [router]]
            [minimalquotes.subscriptions :refer
             [subscribe-quotes! subscribe-tags!]]
            [reagent.core :as r]
            [reagent.dom :as rdom]
            [reagent.session :as session]
            [reitit.frontend :as rf]))

(defn hook-browser-navigation!
  "Replace the browser's scrolling restoration with clerk's and configure
  accountant as History API navigation manager."
  []
  (clerk/initialize!)
  (accountant/configure-navigation!
   {:nav-handler (fn [path]
                   (let [match (rf/match-by-path router path)
                         name (:name (:data match))
                         route-params (:path-params match)
                         query-params (:query-params match)]
                     ;;  (prn "match" match)
                     ;;  (prn "route-params" route-params)
                     ;;  (prn "query-params" query-params)
                     (r/after-render clerk/after-render!)
                     (session/put! :route
                                   {:current-page (page-for name),
                                    :query-params query-params,
                                    :route-params route-params})
                     (clerk/navigate-page! path))),
    :path-exists? (fn [path] (boolean (rf/match-by-path router path)))}))

(defn dev-setup
  []
  ;; allow to use (prn "foo") in place of (.log js/console "foo")
  (enable-console-print!))

(defn ^:dev/after-load mount-root
  "Render the top-level component for this app."
  []
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [error-boundary [current-page]] root-el)))

(defn ^:export main
  "Run application startup logic."
  []
  (when goog.DEBUG (dev-setup))
  (init-firebase!)
  (subscribe-quotes!)
  (subscribe-tags!)
  (hook-browser-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
