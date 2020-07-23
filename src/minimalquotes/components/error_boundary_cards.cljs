(ns minimalquotes.components.error-boundary-cards
  (:require [devcards.core :as dc :refer [defcard]]
            [minimalquotes.components.buttons :as btn]
            [minimalquotes.components.error-boundary :refer [error-boundary]]
            [reagent.core :as r]))

(defn throw-in-3-seconds
  []
  (let [seconds-elapsed (r/atom 0)]
    (fn []
      (js/setTimeout #(swap! seconds-elapsed inc) 1000)
      (when (= 3 @seconds-elapsed) (throw (js/Error. "Boom")))
      [:div "Throwing error in " (- 3 @seconds-elapsed) " seconds"])))

(defcard "# Error Boundary")

(defcard
 "An error boundary catches JavaScript errors anywhere in its child
          component tree, logs those errors, and displays a fallback UI instead
          of the component tree that crashed.")

(defcard error-boundary-card (dc/reagent [error-boundary [throw-in-3-seconds]]))

(defcard
 "An error boundary does not catch errors inside event handlers
          https://reactjs.org/docs/error-boundaries.html#how-about-event-handlers")

(defcard error-boundary-event-handler-card
         (dc/reagent [error-boundary
                      [btn/button
                       {:on-click #(throw (js/Error. "Boom")),
                        :text "Click to throw (but not catch)"}]]))
