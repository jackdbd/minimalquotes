(ns minimalquotes.core
  (:require [accountant.core :as accountant]
            [clerk.core :as clerk]
            [minimalquotes.pages.core :refer [current-page page-for]]
            [minimalquotes.firebase.firestore :refer
             [db-docs-subscribe! db-docs-change-subscribe!]]
            [minimalquotes.firebase.init :refer [init-firebase!]]
            [minimalquotes.routes :refer [router]]
            [minimalquotes.state :as state]
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
                     ;  (prn "route-params" route-params)
                     ;  (prn "query-params" query-params)
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
    (rdom/render [current-page] root-el)))

(defn log-change!
  [doc-change]
  (prn (str "Document id " (.. doc-change -doc -id) " " (.. doc-change -type))))

(defn set-public-subscriptions!
  "Set subscriptions for publicly accessible documents and collections.
  Private documents and collections (e.g. favorite quotes) cannot be subscribed
  to at this point, only later when the user authenticates."
  []
  (let [unsubscribe-from-quotes! (db-docs-subscribe! {:collection "quotes",
                                                      :firestore @state/db,
                                                      :ratom state/quotes})
        unsubscribe-from-tags! (db-docs-subscribe! {:collection "tags",
                                                    :firestore @state/db,
                                                    :ratom state/tags})
        unsubscribe-from-quotes-changes!
        (db-docs-change-subscribe!
         {:collection "quotes", :f log-change!, :firestore @state/db})]
    (swap! state/subscriptions assoc :quotes unsubscribe-from-quotes!)
    (swap! state/subscriptions assoc
      :quotes-changes
      unsubscribe-from-quotes-changes!)
    (swap! state/subscriptions assoc :tags unsubscribe-from-tags!)))

(defn ^:export main
  "Run application startup logic."
  []
  (when goog.DEBUG (dev-setup))
  (init-firebase!)
  (set-public-subscriptions!)
  (hook-browser-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
