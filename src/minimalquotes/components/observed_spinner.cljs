(ns minimalquotes.components.observed-spinner
  (:require
    [goog.object :as object]
    [lambdaisland.glogi :as log]
    [minimalquotes.components.icons :refer [icon-spinner]]
    [minimalquotes.components.intersection-observer :refer [intersection-observer]]
    [minimalquotes.firebase.firestore :refer [query update-state-from-firestore!]]
    [minimalquotes.state :as state]
    [reagent.core :as r]))

(defn on-each-snapshot
  "Callback to invoke for each QueryDocumentSnapshot.
   https://firebase.google.com/docs/reference/js/firebase.firestore.QueryDocumentSnapshot"
  [^js query-document-snapshot]
  (when query-document-snapshot
    (update-state-from-firestore! {:ratom state/quotes} query-document-snapshot)))

;; TODO: how do I know when to stop showing the spinner? Let's say I have 100
;; documents in a Firestore collection, and I fetched all of them. How do I
;; know that I actually fetched all the documents?

(defn observed-spinner
  "Stateful component that fetches data from Firestore when the spinner within
  the root element becomes 50% visible (TODO: I could make it more flexible).
  This component is used to implement infinite scrolling.
  https://developer.mozilla.org/en-US/docs/Web/API/IntersectionObserverEntry
  I am not sure whether I have to close the core.async channel or not when this
  component unmounts.
  https://stackoverflow.com/questions/28888340/should-clojure-core-async-channels-be-closed-when-not-used-anymore"
  [{:keys [root-id]}]
  (let [local-state (r/atom {:fetching false
                             :root nil
                             :unobserve nil})
        on-first-snapshot (fn [^js query-document-snapshot]
                            (when query-document-snapshot
                              (reset! state/first-quote query-document-snapshot)))
        on-last-snapshot (fn [^js query-document-snapshot]
                           (swap! local-state assoc :fetching false)
                           (when query-document-snapshot
                             (log/debug :fetch-from-firestore {:message (str "last doc id: " (.-id query-document-snapshot))})
                             (reset! state/last-quote query-document-snapshot)))
        did-mount (fn [_]
                    (let [root (js/document.querySelector (str "#" root-id))]
                      (swap! local-state assoc :root root)))
        will-unmount (fn [_]
                       (let [unobserve (:unobserve @local-state)]
                         (unobserve)))
        on-change (fn [^js entry ^js unobserve]
                    ;; (prn "getAllPropertyNames entry"
                    ;; (object/getAllPropertyNames entry false false))
                    (swap! local-state assoc :unobserve unobserve)
                    (let [m {:on-each-snapshot on-each-snapshot
                             :on-first-snapshot on-first-snapshot
                             :on-last-snapshot on-last-snapshot}
                          is-intersecting (object/get entry "isIntersecting")
                          ratio (object/get entry "intersectionRatio")]
                      (when (and is-intersecting (> ratio 0.5))
                        (log/debug :on-change {:message "Fetch next batch of documents from Firestore"})
                        (swap! local-state assoc :fetching true)
                        (query @state/db "quotes" m {:limit 20
                                                     :order-by [["createdAt"]]
                                                     :start-after @state/last-quote}))))
        reagent-render (fn []
                         [intersection-observer {:root (:root @local-state)
                                                 :threshold 0.5
                                                 :onChange on-change}
                          ;; The purpose of the padding p-8 is twofold:
                          ;; 1) to provide some padding-top from the list of
                          ;;    quotes, which is above
                          ;; 2) to leave some space for the bounding box of the
                          ;;    SVG circle and path, so they don't interfere
                          ;;    with the root element's scrolling behavior.
                          [:div {:class ["flex" "items-center" "justify-center" "h-24" "p-8"]}
                           (if (get @local-state :fetching)
                             [icon-spinner {:size 16 :text-color "text-purple-600"}]
                             [:div "No more results"])]])]
    (r/create-class {:component-did-mount did-mount
                     :component-will-unmount will-unmount
                     :display-name "observed-spinner"
                     :reagent-render reagent-render})))
