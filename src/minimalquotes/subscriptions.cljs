(ns minimalquotes.subscriptions
  "This namespace contains all subscriptions to Firestore.
   The caller will need to unsubscribe from a subscription when appropriate
   (e.g. when the user logs off and he no longer has permissions to access
   certain resources).
   Unsubscribing from a subscription just removes the listener. The caller will
   need to perform cleanup operations if required (e.g. resetting state to a
   previous value)."
  (:require [minimalquotes.firebase.firestore :refer
             [db-doc-subscribe! db-docs-change-subscribe! db-docs-subscribe!]]
            [minimalquotes.state :as state]))

(defn log-change!
  [doc-change]
  (prn (str "Document id " (.. doc-change -doc -id) " " (.. doc-change -type))))

(defn subscribe-user!
  "Set a subscription for the document associated with the currently
  authenticated user. Call this function when the user logs in, and unsubscribe
  when the user logs out."
  [user-id]
  (let [unsubscribe! (db-doc-subscribe! {:doc-path (str "users/" user-id),
                                         :firestore @state/db,
                                         :ratom state/user})]
    (swap! state/subscriptions assoc :user unsubscribe!)))

(defn subscribe-collection!
  "TODO"
  [{:keys [collection ratom]}]
  (let [k (keyword collection)
        unsubscribe! (db-docs-subscribe! {:collection collection,
                                          :firestore @state/db,
                                          :ratom ratom})]
    (swap! state/subscriptions assoc k unsubscribe!)))

(defn subscribe-quotes!
  "Set a subscription for the documents in the `quotes` collection."
  []
  (subscribe-collection! {:collection "quotes", :ratom state/quotes}))

(defn subscribe-tags!
  "Set a subscription for the documents in the `tags` collection."
  []
  (subscribe-collection! {:collection "tags", :ratom state/tags}))

(defn subscribe-quotes-changes!
  []
  (let [unsubscribe! (db-docs-change-subscribe! {:collection "quotes",
                                                 :f log-change!,
                                                 :firestore @state/db})]
    (prn "unsubscribe!" unsubscribe!)))

(defn subscribe-users!
  "Set a subscription for the documents in the `users` collection."
  []
  (subscribe-collection! {:collection "users", :ratom state/users}))