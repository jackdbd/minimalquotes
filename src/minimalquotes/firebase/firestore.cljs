(ns minimalquotes.firebase.firestore
  (:require [minimalquotes.state :as state]))

(defn- on-successful-update [])
(defn- on-failed-update [err] (js/console.log (ex-cause err)))

(defn- update-from-firestore!
  "Update local app state with a document from Firestore."
  [ratom-collection doc]
  (let [k (keyword (.. doc -id))
        m (js->clj (.data doc) :keywordize-keys true)
        v (assoc m :id k)]
    (swap! ratom-collection assoc k v)))

(defn db-docs-subscribe!
  "Listen to the changes in a Firestore collection and update local app state."
  [{:keys [collection firestore ratom-collection]}]
  (let [coll-ref (.collection firestore collection)
        f (partial update-from-firestore! ratom-collection)]
    (.onSnapshot coll-ref (fn [query-snapshot]
                            (.forEach query-snapshot f)))))

(defn db-path-upsert!
  "Update an existing Firestore document or create a new one."
  [{:keys [doc-path m on-reject on-resolve]
    :or {on-reject on-failed-update
         on-resolve on-successful-update}}]
  (let [db @state/db
        doc-ref (.doc db doc-path)
        doc (clj->js m)]
    (->
     (.set doc-ref doc)
     (.then on-resolve)
     (.catch on-reject))))