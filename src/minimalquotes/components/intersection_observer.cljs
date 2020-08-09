(ns minimalquotes.components.intersection-observer
  (:require
    [goog.object :as object]
    ["@researchgate/react-intersection-observer" :as react-intersection-observer]))

(def ReactIntersectionObserver (object/get react-intersection-observer "default"))

(defn intersection-observer
  "Reagent wrapper for ReactIntersectionObserver."
  [options target]
  [:> ReactIntersectionObserver options target])
