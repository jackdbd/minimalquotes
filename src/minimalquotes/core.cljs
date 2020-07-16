(ns minimalquotes.core
  "This namespace contains the entrypoint of the app."
  (:require
   [devtools.core :as devtools]
   [minimalquotes.components.app :refer [app]]
   [minimalquotes.firebase.firestore :refer [db-docs-subscribe! db-docs-change-subscribe!]]
   [minimalquotes.firebase.init :refer [init-firebase!]]
   [minimalquotes.state :as state]
   [reagent.dom :as rdom]))

(defn dev-setup []
  ;; enable https://github.com/binaryage/cljs-devtools
  (devtools/install!)
  ;; This line allows us to use `(println "foo")` in place of (.log js/console "foo")
  (enable-console-print!))

(defn ^:dev/after-load mount-root
  "Render the top-level component for this app."
  []
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [app] root-el)))

(defn log-change!
  [doc-change]
  (prn (str "Document id " (.. doc-change -doc -id) " " (.. doc-change -type))))

(defn set-public-subscriptions!
  "Set subscriptions for publicly accessible documents and collections.
  Private documents and collections (e.g. favorite quotes) cannot be subscribed
  to at this point, only later when the user authenticates."
  []
  (let [unsubscribe-from-quotes! (db-docs-subscribe! {:collection "quotes"
                                                      :firestore @state/db
                                                      :ratom state/quotes})
        unsubscribe-from-tags! (db-docs-subscribe! {:collection "tags"
                                                    :firestore @state/db
                                                    :ratom state/tags})
        unsubscribe-from-quotes-changes! (db-docs-change-subscribe! {:collection "quotes"
                                                                     :f log-change!
                                                                     :firestore @state/db})]
    (swap! state/subscriptions assoc :quotes unsubscribe-from-quotes!)
    (swap! state/subscriptions assoc :quotes-changes unsubscribe-from-quotes-changes!)
    (swap! state/subscriptions assoc :tags unsubscribe-from-tags!)))

(defn ^:export main
  "Run application startup logic."
  []
  (when goog.DEBUG
    (dev-setup))
  (init-firebase!)
  (set-public-subscriptions!)
  (mount-root))
