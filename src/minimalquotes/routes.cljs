(ns minimalquotes.routes
  (:require
    [reitit.core :as r]
    [reitit.frontend :as rf]))

(def router
  (rf/router [["/" ::index]
              ["/about" ::about]
              ["/admin" ::admin]
              ["/quotes" ::quotes]
              ["/sign-in" ::sign-in]
              ["/tags" ::tags]]))

(defn path-for
  "Name-based (reverse) routing."
  [name & [query-params]]
  (-> router
      (rf/match-by-name name)
      (r/match->path query-params)))
