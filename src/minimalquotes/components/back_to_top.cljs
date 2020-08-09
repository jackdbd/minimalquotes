(ns minimalquotes.components.back-to-top
  (:require
    [goog.fx.dom :as dom]
    [minimalquotes.components.buttons :as btn]
    [minimalquotes.components.icons :refer [icon-to-top]]
    [reagent.core :as r]))

;; Alternative: just scroll to 0 immediately
;; (set! (.. el -scrollTop) 0)
(defn scroll!
  "Scroll animation with goog.fx.dom.
  https://google.github.io/closure-library/api/goog.fx.dom.Scroll.html
  https://gist.github.com/martinklepsch/9565a5ea099c44bc2931"
  [elem [start-left start-top] [end-left end-top] time]
  (let [anim (dom/Scroll. elem #js [start-left start-top] #js [end-left end-top] time)]
    (.play anim)))

(defn back-to-top
  "Button to reset the scrollTop property of an element to 0."
  [{:keys [animation-ms appear-at-px elem-id]
    :or {animation-ms 100 appear-at-px 300}}]
  (let [state (r/atom {})
        css-transition "transition duration-500 ease-in-out"
        css-classes ["opacity-0" "fixed"]
        listener (fn []
                   (let [el (:el @state)]
                     ;;  (prn "listener scrollTop" (.-scrollTop el))
                     (if (> (.-scrollTop el) appear-at-px)
                       (swap! state assoc :css-classes (conj css-classes css-transition "opacity-100"))
                       (swap! state assoc :css-classes (conj css-classes "opacity-0")))))
        did-mount (fn [_]
                    (let [el (js/document.querySelector (str "#" elem-id))]
                      (.addEventListener el "scroll" listener)
                      (swap! state assoc :css-classes css-classes :el el :scroll-top (.-scrollTop el))))
        will-unmount (fn []
                       (let [el (:el @state)]
                         (.removeEventListener el "scroll" listener)))
        on-click (fn []
                   (let [el (:el @state)]
                     (scroll! el [0 (.-scrollTop el)] [0 0] animation-ms)))
        reagent-render (fn []
                         [:div {:class (:css-classes @state)
                                :style {:bottom "25px" :right "25px"}}
                          [btn/button {:icon icon-to-top
                                       :on-click on-click
                                       :text "To top"}]])]
    (r/create-class {:component-did-mount did-mount
                     :component-will-unmount will-unmount
                     :display-name "back-to-top"
                     :reagent-render reagent-render})))
