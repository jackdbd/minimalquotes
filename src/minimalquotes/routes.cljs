(ns minimalquotes.routes
  (:require
   ;; [reitit.core :refer [router-name routes]]
   ;; [reitit.dev.pretty :as pretty]
   ;; [clojure.spec.alpha :as s]
   [reitit.core :as r]
   [reitit.frontend :as rf]))

(def router
  (rf/router [["/" ::index] ["/about" ::about] ["/admin" ::admin]
              ["/quotes" ::quotes] ["/sign-in" ::sign-in] ["/tags" ::tags]]))

;; (defn path-for
;;   "Name-based (reverse) routing."
;;   [name & [path-params]]
;;   (prn "path-for" name path-params)
;;   (if path-params
;;     (:path (rf/match-by-name router name path-params))
;;     (:path (rf/match-by-name router name))))

(defn path-for
  "Name-based (reverse) routing."
  [name & [query-params]]
  ;; (prn "path-for" name query-params)
  (-> router
      (rf/match-by-name name)
      (r/match->path query-params)))

;; (router-name router)
;; (routes router)
; (rf/match-by-name router :index)
; (rf/match-by-name router :quotes {:tag "love"})