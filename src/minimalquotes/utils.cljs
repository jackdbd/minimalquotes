(ns minimalquotes.utils
  (:require
    [clojure.string :as str]
    [lambdaisland.glogi :as log]))

(defn format-price [cents] (str " €" (/ cents 100)))

(defn testing-container
  "The container that should be used to render testing-library react components.
  We want to provide our own container so that the rendered devcards aren't used.
  See also:
  https://testing-library.com/docs/react-testing-library/api#render-options"
  []
  (let [app-div (js/document.createElement "div")]
    (.setAttribute app-div "id" "testing-lib")
    (js/document.body.appendChild app-div)))

(defn dissoc-in
  [m [k & ks]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)] (assoc m k newmap))
      m)
    (dissoc m k)))

(defn k->str [k] (str/replace (str k) ":" ""))

(defn log-error
  "TODO: print better stack traces, either in JS or CLJS."
  [err]
  (js/console.groupCollapsed (str "%c" (.. err -name) ": " (.. err -message))
                             "background: #fff; color: red;")
  (when (.. err -code)
    (js/console.error (.. err -code)))
  (js/console.trace err)
  (js/console.groupEnd)
  
  (let [k (keyword (.. err -name))]
    (log/error k {:message (.. err -message)})))
