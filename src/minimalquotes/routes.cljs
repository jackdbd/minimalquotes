(ns minimalquotes.routes
  (:require
   ;;  [reitit.core :refer [router-name routes]]
   ;;  [reitit.dev.pretty :as pretty]
   [reitit.frontend :as rf]))

(def router
  (rf/router [["/" :index] ["/about" :about] ["/quotes" :quotes]
              ["/sign-in" :sign-in] ["/tags" :tags]]))

(defn path-for
  "Name-based (reverse) routing."
  [name & [path-params]]
  (if path-params
    (:path (rf/match-by-name router name path-params))
    (:path (rf/match-by-name router name))))

;; (router-name router)
;; (routes router)
; (rf/match-by-name router :index)
; (rf/match-by-name router :quotes {:tag "love"})