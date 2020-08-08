(ns minimalquotes.components.error-boundary-cards
  (:require
    [devcards.core :as dc :refer [defcard]]
    [goog.object :as object]
    [minimalquotes.components.buttons :as btn]
    [minimalquotes.components.error-boundary :refer [error-boundary]]
    [reagent.core :as r]))

(defn throw-in-3-seconds
  "Component that always throws after 3 seconds it was mounted in the DOM."
  []
  (let [seconds-elapsed (r/atom 0)]
    (fn []
      (js/setTimeout #(swap! seconds-elapsed inc) 1000)
      (when (= 3 @seconds-elapsed)
        (throw (js/Error. "Boom (this is the error message)")))
      [:div "Throwing error in " (- 3 @seconds-elapsed) " seconds"])))

(defcard "# Error Boundary")

(defcard "An error boundary catches JavaScript errors anywhere in its child 
          component tree, logs those errors, and displays a fallback UI instead 
          of the component tree that crashed.")

(defn on-catch
  [^js err ^js info]
  (let [component-stack (object/getValueByKeys info #js ["componentStack"])
        error-stack (object/getValueByKeys err #js ["stack"])]
    (js/console.warn "TODO: send to Sentry" "component-stack" component-stack)
    (js/console.warn "TODO: send to Sentry" "error-stack" error-stack)))

(defcard error-boundary-card
  (let [props {:on-catch on-catch}]
    (dc/reagent [error-boundary props
                 [throw-in-3-seconds]])))

(defcard "React [Error Boundaries](https://reactjs.org/docs/error-boundaries.html#how-about-event-handlers) do not catch errors inside event handlers.")

(defcard error-boundary-event-handler-card
  (let [props {:on-catch on-catch}]
    (dc/reagent [error-boundary props
                 [btn/button {:on-click #(throw (js/Error. "Boom"))
                              :text "Click to throw (but not catch)"}]])))
