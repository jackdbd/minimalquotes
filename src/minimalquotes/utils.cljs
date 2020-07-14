(ns minimalquotes.utils
  (:require [clojure.string :as str]))

(defn format-price
  [cents]
  (str " €" (/ cents 100)))

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
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (assoc m k newmap))
      m)
    (dissoc m k)))

(defn k->str
  [k]
  (str/replace (str k) ":" ""))
