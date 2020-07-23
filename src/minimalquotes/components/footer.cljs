(ns minimalquotes.components.footer
  (:require ["firebase/app" :as firebase]
            [minimalquotes.utils :refer [log-error]]))

(defn- on-success
  [result]
  (prn "Delete success!")
  (js/console.log (js/JSON.stringify result nil 2)))

(defn footer
  []
  [:footer
   {:on-click (fn [_]
                (let [cloud-function-name "recursiveDelete"
                      f (.httpsCallable (firebase/functions)
                                        cloud-function-name)]
                  (prn "TODO: call " cloud-function-name " with a valid token")
                  (comment (-> (f #js {:path "deleteme/"})
                               (.then on-success)
                               (.catch log-error)))))} "Footer here"])
