(ns minimalquotes.core
  "This namespace contains the entrypoint of the app."
  (:require
   [devtools.core :as devtools]
   [minimalquotes.components.app :refer [app]]
   [minimalquotes.firebase.init :refer [init-firebase!]]
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

(declare goog.DEBUG)

(defn ^:export main
  "Run application startup logic."
  []
  (when goog.DEBUG
    (println "=== Check if devtools is setup ===" {:a 123 :b "BBB" :c {:d "d" :e #{1 2 3}}})
    (dev-setup))
  (init-firebase!)
  (mount-root))
