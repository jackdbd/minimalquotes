(ns minimalquotes.components.modal
  (:require [reagent.core :as r]))

; https://code.thheller.com/blog/shadow-cljs/2017/11/06/improved-externs-inference.html
(set! *warn-on-infer* true)

(def modal-window-id "modal-window")
(def modal-event-type "modal")

(defn- nop [])

(defn- validate-modal-window!
  []
  (let [n (.-length (.querySelectorAll js/document (str "#" modal-window-id)))]
    (when (not= 1 n)
      (throw (js/Error. (str "There are "
                             n
                             " components with id = "
                             modal-window-id
                             " on this page. There should be only 1."))))))

(defn modal!
  "Dispatch a custom event to open/close a modal."
  [hiccup-content]
  (let [elem (.querySelector js/document (str "#" modal-window-id))
        event (js/CustomEvent. modal-event-type
                               #js {:detail {:child hiccup-content}})]
    (.dispatchEvent elem event)))

;; TODO: add spec

(defn modal-window
  "A modal window that blocks user interaction when has a child to render."
  [{:keys [modal-backdrop-css-class modal-window-css-class
           modal-window-hidden-css-class on-after-close on-after-open
           should-close-on-esc]
    :or {modal-backdrop-css-class "modal-backdrop"
         modal-window-css-class "modal-window"
         modal-window-hidden-css-class "modal-window--hidden"
         on-after-close nop
         on-after-open nop
         should-close-on-esc true}}]
  (let [child (r/atom nil)
        close-modal (fn [] (reset! child nil) (on-after-close))
        open-modal
        (fn [hiccup-content] (reset! child hiccup-content) (on-after-open))
        on-keydown (fn [e] (when (and (= (.. e -key) "Escape")) (close-modal)))
        modal-listener
        (fn [e]
          (let [detail (.. e -detail)]
            (if (:child detail) (open-modal (:child detail)) (close-modal))))
        did-mount (fn [_]
                    (validate-modal-window!)
                    (let [elem (.querySelector js/document
                                               (str "#" modal-window-id))]
                      (.addEventListener elem modal-event-type modal-listener)
                      (when should-close-on-esc
                        (.addEventListener js/document "keydown" on-keydown))))
        will-unmount
        (fn [_]
          (let [elem (.querySelector js/document (str "#" modal-window-id))]
            (.removeEventListener elem modal-event-type modal-listener)
            (when should-close-on-esc
              (.removeEventListener js/document "keydown" on-keydown))))
        on-backdrop-click
        (fn [^js e] (when (.. e -target -dataset -backdrop) (close-modal)))
        reagent-render
        (fn []
          [:div
           {:id modal-window-id
            :class [modal-window-css-class
                    (when (nil? @child) modal-window-hidden-css-class)]}
           (when @child
             [:div
              {:class [modal-backdrop-css-class]
               :data-backdrop "true"
               :on-click on-backdrop-click} @child])])]
    (r/create-class {:display-name "modal-window"
                     :component-did-mount did-mount
                     :component-will-unmount will-unmount
                     :reagent-render reagent-render})))
