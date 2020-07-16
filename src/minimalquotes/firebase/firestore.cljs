(ns minimalquotes.firebase.firestore
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [cljs.core.async.interop :refer-macros [<p!]]
   ["firebase/app" :as firebase]))

; https://code.thheller.com/blog/shadow-cljs/2017/11/06/improved-externs-inference.html
(set! *warn-on-infer* true)

(defn- on-successful-creation
  [doc]
  (js/console.log "Doc written to Firestore with ID:" (.. doc -id)))

(defn- on-successful-update [])

(defn- on-successful-deletion [])

(defn- on-error
  "TODO: print better stack traces, either in JS or CLJS."
  [err]
  (js/console.groupCollapsed (str "%c" (.. err -name) ": "(.. err -message)) "background: #fff; color: red;")
  (js/console.error (.. err -code))
  (js/console.trace err)
  (js/console.groupEnd))

(defn- update-state-from-firestore!
  "Sync a reagent atom-like object (e.g. a reagent cursor) with a document from
  Firestore. When syncing from Firestore, you could optionally add the document
  id in the document itself.
  TODO: to keywordize or not to keywordize?
  TODO: should I store in state the original JS object, without converting it to
        cljs? Probably that's better performance-wise.
        id would be (.. doc -id)
        the object itself would be (.data doc)"
  [{:keys [include-doc-id? ratom]
    :or {include-doc-id? false}} doc]
  (let [k (keyword (.. doc -id))
        m (js->clj (.data doc) :keywordize-keys true)
        v (assoc m :id k)]
    ;; (prn "update-state-from-firestore!" (.. doc -id) doc)
    (if include-doc-id?
      (swap! ratom assoc k v)
      (swap! ratom assoc k m))))

(defn db-docs-subscribe!
  "Listen to the changes in a Firestore collection and update local app state."
  [{:keys [collection firestore ratom]}]
  (let [^js coll-ref (.collection firestore collection)
        f (partial update-state-from-firestore! {:ratom ratom})
        on-query-snapshot (fn [^js query-snapshot]
                            (reset! ratom {})
                            (.forEach query-snapshot f))]
    (.onSnapshot coll-ref on-query-snapshot on-error)))

(defn db-docs-change-subscribe!
  "Subscribe to changes to query results between query snapshots. Whenever a
  document change occurs, invoke function f to handle that document change.
  This function returns an unsubscribe function. Don't forget to call it when
  you no longer need this subscription."
  [{:keys [collection f firestore]}]
  (let [^js coll-ref (.collection firestore collection)
        on-query-snapshot (fn [^js query-snapshot]
                            (let [doc-changes (.docChanges query-snapshot)]
                              (.forEach doc-changes f)))]
    (.onSnapshot coll-ref on-query-snapshot on-error)))

(defn db-doc-create!
  "Create a new Firestore document."
  [{:keys [collection firestore m on-reject on-resolve]
    :or {on-reject on-error
         on-resolve on-successful-creation}}]
  (let [coll-ref (.collection firestore collection)
        doc (clj->js m)]
    (->
     (.add coll-ref doc)
     (.then on-resolve)
     (.catch on-reject))))

(defn db-path-upsert!
  "Update an existing Firestore document or create a new one."
  [{:keys [doc-path firestore m on-reject on-resolve]
    :or {on-reject on-error
         on-resolve on-successful-update}}]
  (let [doc-ref (.doc firestore doc-path)
        doc (clj->js m)]
    (->
     (.set doc-ref doc)
     (.then on-resolve)
     (.catch on-reject))))

(defn db-path-delete!
  "Delete a document in Firestore."
  [{:keys [doc-path firestore on-reject on-resolve]
    :or {on-reject on-error
         on-resolve on-successful-deletion}}]
  (let [doc-ref (.doc firestore doc-path)]
    (->
     (.delete doc-ref)
     (.then on-resolve)
     (.catch on-reject))))

(defn now
  "ClojureScript wrapper for firebase.firestore.Timestamp.now()
  https://firebase.google.com/docs/reference/js/firebase.firestore.Timestamp#static-now"
  []
  (.now (.. firebase -firestore -Timestamp)))

(defn server-timestamp
  "ClojureScript wrapper for firebase.firestore.FieldValue.serverTimestamp()
  https://medium.com/firebase-developers/the-secrets-of-firestore-fieldvalue-servertimestamp-revealed-29dd7a38a82b"
  []
  (.serverTimestamp (.. firebase -firestore -FieldValue)))


(defn quotes-with-tag
  [^js firestore ratom tag-name]
  (let [ref (-> (.collection firestore "quotes")
                (.where (str "tags." tag-name) "==" true))
        f (partial update-state-from-firestore! {:ratom ratom})]
    (go (try
          (let [query-snapshot (<p! (.get ref))]
            (reset! ratom {})
            (.forEach query-snapshot f))
          (catch js/Error err (.log js/console (str "=== Error === " (ex-cause err))))))))
