(ns minimalquotes.components.error-boundary
  (:require [minimalquotes.components.buttons :as btn]
            [minimalquotes.utils :refer [log-error]]
            [reagent.core :as r]))

; https://tailwindcss.com/components/alerts/#app
(defn fallback-ui
  [{:keys [on-click]}]
  [:div {:role :alert}
   [:div {:class ["bg-red-500 rounded-t px-4 py-2"]}
    [:h2 {:class ["text-white text-2xl"]} "Ops. Something went wrong."]]
   [:div
    {:class
     ["border border-t-0 border-red-400 rounded-b bg-red-100 px-4 py-3 text-red-700"]}
    [:p "Long explanation about the error..."]]
   [btn/button {:on-click on-click, :text "Try again"}]])

(defn error-boundary
  [component-tree]
  (let [error (r/atom nil)]
    (r/create-class
     {:component-did-catch
      (fn [_ err info]
        (log-error err)
        (prn "Todo: send error to Firebase Crashlytics or Sentry" "info" info)),
      :get-derived-state-from-error (fn [e] (reset! error e) #js {}),
      :reagent-render (fn [component-tree]
                        (if @error
                          [fallback-ui {:on-click #(reset! error nil)}]
                          component-tree))})))
