(ns minimalquotes.firebase.firestore)

(defn- on-successful-creation
  [doc]
  (js/console.log "Doc written to Firestore with ID:" (.. doc -id)))

(defn- on-failed-creation
  [err]
  (js/console.error "=== Error: cannot create document === ")
  (js/console.error err))

(defn- on-successful-update [])

(defn- on-failed-update
  [err]
  (js/console.error "=== Error: cannot update document === ")
  (js/console.error err))

(defn- on-successful-deletion [])

(defn- on-failed-deletion
  [err]
  (js/console.error "=== Error: cannot delete document === ")
  (js/console.error err))

(defn- update-state-from-firestore!
  "Sync a reagent atom-like object (e.g. a reagent cursor) with a document from
  Firestore. When syncing from Firestore, you could optionally add the document
  id in the document itself.
  TODO: to keywordize or not to keywordize?"
  [{:keys [include-doc-id? ratom]
    :or {include-doc-id? false}} doc]
  (let [k (keyword (.. doc -id))
        m (js->clj (.data doc) :keywordize-keys true)
        v (assoc m :id k)]
    (if include-doc-id?
      (swap! ratom assoc k v)
      (swap! ratom assoc k m))))

(defn db-docs-subscribe!
  "Listen to the changes in a Firestore collection and update local app state."
  [{:keys [collection firestore ratom]}]
  (let [coll-ref (.collection firestore collection)
        f (partial update-state-from-firestore! {:ratom ratom})]
    (.onSnapshot coll-ref (fn [query-snapshot]
                            (.forEach query-snapshot f)))))

(defn db-doc-create!
  "Create a new Firestore document."
  [{:keys [collection firestore m on-reject on-resolve]
    :or {on-reject on-failed-creation
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
    :or {on-reject on-failed-update
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
    :or {on-reject on-failed-deletion
         on-resolve on-successful-deletion}}]
  (let [doc-ref (.doc firestore doc-path)]
    (->
     (.delete doc-ref)
     (.then on-resolve)
     (.catch on-reject))))
