(ns minimalquotes.firebase.firestore
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async.interop :refer-macros [<p!]]
            ["firebase/app" :as firebase]
            [minimalquotes.state :as state]
            [minimalquotes.utils :refer [log-error]]))

; https://code.thheller.com/blog/shadow-cljs/2017/11/06/improved-externs-inference.html
(set! *warn-on-infer* true)

(defn- on-successful-creation
  [doc]
  (js/console.log "Doc written to Firestore with ID:" (.. doc -id)))

(defn- on-successful-update [])

(defn- on-successful-deletion [])

(defn- update-state-from-firestore!
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

(defn update-state!
  [{:keys [^js doc-snapshot ratom]}]
  (let [m (js->clj (.data doc-snapshot) :keywordize-keys true)]
    (reset! ratom m)))

(defn db-docs-subscribe!
  "Listen to the changes in a Firestore collection and update local app state."
  [{:keys [collection firestore ratom]}]
  (let [^js coll-ref (.collection firestore collection)
        f (partial update-state-from-firestore! {:ratom ratom})
        observer #js
                  {:error log-error
                   :next (fn [^js query-snapshot]
                            (reset! ratom {})
                            (.forEach query-snapshot f))}]
    (.onSnapshot coll-ref observer)))

(defn db-doc-subscribe!
  "Listen to the changes of a Firestore document and update local app state."
  [{:keys [doc-path firestore ratom]}]
  (let [^js doc-ref (.doc firestore doc-path)
        observer
        #js
         {:error log-error
          :next
          (fn [^js doc-snapshot]
             (let [hasPendingWrites (goog.object/getValueByKeys
                                         doc-snapshot
                                         #js ["metadata" "hasPendingWrites"])]
                (when hasPendingWrites (prn "Source: Local (what to do?)"))
                (prn "")
                (when (goog.object/get (.data doc-snapshot) "isAdmin")
                   (prn " === WELCOME BACK ADMIN ===")
                   (let [unsubscribe-users! (db-docs-subscribe!
                                                 {:collection "users"
                                                   :firestore firestore
                                                   :ratom state/users})]
                      (swap! state/subscriptions assoc
                         :users
                         unsubscribe-users!)))
                (update-state! {:doc-snapshot doc-snapshot :ratom ratom})))}]
    (.onSnapshot doc-ref observer)))

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

; TODO: this should become a cloud function because it must set the roles for
; the newly created user.
(defn add-user-if-first-time!
  "First-time users that authenticate (e.g. by using an identity provider like
  google.com) aren't yet users of this application. So the first time they
  authenticate, we create a document for them in the users collection."
  [{:keys [^js auth-user firestore on-reject uid] :or {on-reject log-error}}]
  (let [doc-path (str "users/" uid)
        doc-ref (.doc firestore doc-path)
        f (fn [doc-snapshot]
            (when (not (.-exists doc-snapshot))
              (let [m {:displayName (goog.object/get auth-user "displayName")
                       :email (goog.object/get auth-user "email")
                       :photoUrl (goog.object/get auth-user "photoUrl")
                       :uid uid}]
                (db-path-upsert!
                  {:doc-path doc-path :firestore firestore :m m}))))]
    (-> (.get doc-ref)
        (.then f)
        (.catch on-reject))))

(defn db-path-delete!
  "Delete a document in Firestore."
  [{:keys [doc-path firestore on-reject on-resolve]
    :or {on-reject log-error on-resolve on-successful-deletion}}]
  (let [doc-ref (.doc firestore doc-path)]
    (-> (.delete doc-ref)
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
    (go (try (let [query-snapshot (<p! (.get ref))]
               (reset! ratom {})
               (.forEach query-snapshot f))
             (catch js/Error err
               (.log js/console (str "=== Error === " (ex-cause err))))))))
