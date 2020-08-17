(ns minimalquotes.components.global-catch-all
  (:require
    [reagent.core :as r]))

(defn global-catch-all
  "Component to catch errors in event handlers and any uncaught exception."
  [{:keys [on-error-event]}]
  (let [listener (fn [^js err-ev]
                   (on-error-event err-ev))
        did-mount (fn [_]
                    (.addEventListener js/window "error" listener))
        will-unmount (fn []
                       (.removeEventListener js/window "error" listener))
        reagent-render (fn [component-tree]
                         component-tree)]
    (r/create-class {:component-did-mount did-mount
                     :component-will-unmount will-unmount
                     :display-name "global-catch-all"
                     :reagent-render reagent-render})))
