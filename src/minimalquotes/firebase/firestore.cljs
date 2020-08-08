(ns minimalquotes.firebase.firestore
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs.core.async.interop :refer-macros [<p!]]
    [minimalquotes.utils :refer [log-error]]))

; https://code.thheller.com/blog/shadow-cljs/2017/11/06/improved-externs-inference.html
(set! *warn-on-infer* false)

(defn- on-successful-creation
  [doc]
  (js/console.log "Doc written to Firestore with ID:" (.. doc -id)))

(defn on-successful-update [])

(defn on-successful-deletion [])

(defn update-state-from-firestore!
  "Sync a reagent atom-like object (e.g. a reagent cursor) with a document from
  Firestore. When syncing from Firestore, you could optionally add the document
  id in the document itself.
  TODO: to keywordize or not to keywordize?"
  [{:keys [include-doc-id? ratom] :or {include-doc-id? false}} ^js doc]
  (let [k (keyword (.. doc -id))
        m (js->clj (.data doc) :keywordize-keys true)
        v (assoc m :id k)]
    ; (prn "update-state-from-firestore!" (.. doc -id))
    (if include-doc-id? (swap! ratom assoc k v) (swap! ratom assoc k m))))

(defn db-docs-change-subscribe!
  "Subscribe to changes to query results between query snapshots. Whenever a
  document change occurs, invoke function f to handle that document change.
  This function returns an unsubscribe function. Don't forget to call it when
  you no longer need this subscription."
  [{:keys [collection f firestore]}]
  (let [^js coll-ref (.collection firestore collection)
        observer #js
                  {:error log-error
                   :next (fn [^js query-snapshot]
                            (let [doc-changes (.docChanges query-snapshot)]
                               (.forEach doc-changes f)))}]
    (.onSnapshot coll-ref observer)))

; TODO: make function `write` to replace both db-doc-create! and
; db-path-upsert!

(defn db-doc-create!
  "Create a new Firestore document."
  [{:keys [collection firestore m on-reject on-resolve]
    :or {on-reject log-error on-resolve on-successful-creation}}]
  (let [coll-ref (.collection firestore collection)
        doc (clj->js m)]
    (-> (.add coll-ref doc)
        (.then on-resolve)
        (.catch on-reject))))

(defn db-path-upsert!
  "Update an existing Firestore document or create a new one."
  [{:keys [doc-path firestore m on-reject on-resolve]
    :or {on-reject log-error on-resolve on-successful-update}}]
  (let [doc-ref (.doc firestore doc-path)
        doc (clj->js m)]
    (-> (.set doc-ref doc)
        (.then on-resolve)
        (.catch on-reject))))

(defn delete
  "Delete a document in Firestore."
  [^js firestore doc-path
   {:keys [on-error on-success]}]
  (let [doc-ref (.doc firestore doc-path)]
    (go (try (<p! (.delete doc-ref))
             (on-success)
             (catch js/Error err
               (on-error err))))))

(defn now
  "ClojureScript wrapper for firebase.firestore.Timestamp.now()
  https://firebase.google.com/docs/reference/js/firebase.firestore.Timestamp#static-now"
  []
  (.now (.. js/firebase -firestore -Timestamp)))

(defn server-timestamp
  "ClojureScript wrapper for firebase.firestore.FieldValue.serverTimestamp()
  https://medium.com/firebase-developers/the-secrets-of-firestore-fieldvalue-servertimestamp-revealed-29dd7a38a82b"
  []
  (.serverTimestamp (.. js/firebase -firestore -FieldValue)))

; TODO: use clojure spec to validate query methods.
(defn query
  "Query a Firestore collection a single time.
  https://firebase.google.com/docs/reference/js/firebase.firestore.Firestore#collection
  https://firebase.google.com/docs/reference/js/firebase.firestore.Query
  https://firebase.google.com/docs/reference/js/firebase.firestore.Query#where"
  [^js firestore
   collection-path
   {:keys [on-each-snapshot on-first-snapshot on-last-snapshot]}
   {:keys [end-at end-before limit limit-to-last order-by start-after start-at where]}]
  ; TODO: improve warnings
  (when (and limit limit-to-last)
    (js/console.warn "You passed both :limit and :limit-to-last. :limit will NOT be considered."))
  (let [collection-ref (.collection firestore collection-path)
        ref (atom collection-ref)]
    (doseq [[field-path direction-str] order-by]
      (swap! ref #(.orderBy ^js % field-path (if direction-str direction-str "asc"))))
    (doseq [[field-path op-str value] where]
      (swap! ref #(.where ^js % field-path op-str value)))
    (when start-after
      (swap! ref #(.startAfter ^js % start-after)))
    (when start-at
      (swap! ref #(.startAt ^js % start-at)))
    (when end-at
      (swap! ref #(.endAt ^js % end-at)))
    (when end-before
      (swap! ref #(.endBefore ^js % end-before)))
    (when limit
      (swap! ref #(.limit ^js % limit)))
    (when limit-to-last
      (swap! ref #(.limitToLast ^js % limit-to-last)))
    (go (try (let [query-snapshot (<p! (.get @ref))
                   doc-snapshots (.-docs query-snapshot)]
               (when on-first-snapshot
                 (on-first-snapshot (first doc-snapshots)))
               (when on-last-snapshot
                 (on-last-snapshot (last doc-snapshots)))
               (when on-each-snapshot
                 (.forEach query-snapshot on-each-snapshot)))
             (catch js/Error err
               (js/console.error "query error: double check Firestore rules")
               (log-error err))))))

(defn subscribe
  "Observe a Firestore collection ― or a subset of the collection if a query map
  is provided ―  and invoke the `next` callback when it changes."
  ([^js firestore collection-path snapshot-callbacks]
   (subscribe firestore collection-path snapshot-callbacks {}))
  ([^js firestore
    collection-path
    {:keys [on-each-snapshot]}
    {:keys [where]}]
   (let [collection-ref (.collection firestore collection-path)
         ref (atom collection-ref)
         on-next (fn [^js query-snapshot]
                   (let [hasPendingWrites (goog.object/getValueByKeys
                                            query-snapshot
                                            #js ["metadata" "hasPendingWrites"])]
                     (when hasPendingWrites
                       (prn "TODO: what to do with pending writes? Wait?")))
                   (.forEach query-snapshot on-each-snapshot))
         observer #js {:error log-error :next on-next}]
     (doseq [[field-path op-str value] where]
       ; (prn "WHERE" field-path op-str value)
       (swap! ref #(.where ^js % field-path op-str value)))
     (.onSnapshot @ref observer))))
