(ns minimalquotes.components.error-boundary
  (:require
    [goog.object :as object]
    [minimalquotes.components.buttons :as btn]
    [minimalquotes.utils :refer [log-error]]
    [reagent.core :as r]
    ["@sentry/react" :as Sentry]
    ["stacktrace-js" :as StackTrace]))

(defn s->li
  [i s]
  ^{:key i} [:li {:class "list-decimal"} s])

(defn fallback-ui
  "Whenever a error boundary catches a render error, show this component instead
  of the original component tree.
  Styling taken from here: https://tailwindcss.com/components/alerts/#app"
  [{:keys [component-stack error-message on-click stack-frames]}]
  [:div {:role :alert}
   [:div {:class ["bg-red-500 rounded-t px-4 py-2"]}
    [:h2 {:class ["text-white text-2xl"]} "Ops. Something went wrong."]]
   [:div {:class ["border border-t-0 border-red-400 rounded-b bg-red-100 px-4 py-3 text-red-700"]}
    [:h3 {:class ["text-xl"]} error-message]
    [:details
     [:summary "Component Stack"]
     component-stack]
    [:details
     [:summary "Stack Frames"]
     [:ul {:class ["pt-2" "pl-4"]}
      (map-indexed s->li stack-frames)]]]
   [btn/button {:on-click on-click :text "Try again"}]])

(defn sentry-fallback-ui-hiccup
  [{:keys [component-stack error-message on-click]}]
  [:div {:role :alert}
   [:div {:class ["bg-red-500 rounded-t px-4 py-2"]}
    [:h2 {:class ["text-white text-2xl"]} "Ops. Something went wrong."]]
   [:div {:class ["border border-t-0 border-red-400 rounded-b bg-red-100 px-4 py-3 text-red-700"]}
    [:h3 {:class ["text-xl"]} error-message]
    [:details
     [:summary "Component Stack"]
     component-stack]]
   [btn/button {:on-click on-click :text "Try again"}]])

(defn error-boundary
  "React error boundary."
  [{:keys [on-catch]} _]
  (let [error (r/atom nil)
        component-stack (r/atom "")
        stack-frames (r/atom [])
        on-stackframes (fn [^js stackframes]
                         (let [sf->str (fn [^js sf]
                                         (.toString sf))]
                           (.forEach stackframes #(swap! stack-frames conj (sf->str %)))))]
    (r/create-class
      {:component-did-catch (fn [_ err ^js info]
                              (reset! component-stack (object/getValueByKeys info #js ["componentStack"]))
                              (-> (StackTrace/fromError err)
                                  (.then on-stackframes)
                                  (.catch log-error))
                              (on-catch err info))
       :get-derived-state-from-error (fn [err]
                                       (reset! error err))
       :reagent-render (fn [_ component-tree]
                         (if @error
                           [fallback-ui {:component-stack @component-stack
                                         :error-message (.. @error -message)
                                         :on-click #(reset! error nil)
                                         :stack-frames @stack-frames}]
                           component-tree))})))

(defn sentry-fallback-ui
  "React element (created from a Hiccup form) to be used as Fallback UI in the
  Sentry ErrorBoundary component."
  [^js props]
  (let [component-stack (object/get props "componentStack")
        error (goog.object/get props "error")
        reset-error (goog.object/get props "resetError")]
    (r/as-element [sentry-fallback-ui-hiccup {:component-stack component-stack
                                              :error-message (.toString error)
                                              :on-click #(reset-error)
                                              :stack-frames []}])))

(defn sentry-error-boundary
  "React ErrorBoundary with Sentry integration.
  https://docs.sentry.io/platforms/javascript/react/#errorboundary-options"
  [component-tree]
  [:> Sentry/ErrorBoundary {:fallback sentry-fallback-ui
                            :showDialog true}
   component-tree])