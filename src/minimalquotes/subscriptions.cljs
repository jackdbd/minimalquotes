(ns minimalquotes.subscriptions
  "This namespace contains all subscriptions to Firestore.
  The caller will need to unsubscribe from a subscription when appropriate
  (e.g. when the user logs off and he no longer has permissions to access
  certain resources).
  Unsubscribing from a subscription just removes the listener. The caller will
  need to perform cleanup operations if required (e.g. resetting state to a
  previous value)."
  (:require
    [minimalquotes.firebase.firestore :refer
     [db-docs-change-subscribe! db-docs-subscribe! db-collection-subscribe!]]
    [minimalquotes.state :as state]))

(defn log-change!
  [^js doc-change]
  (prn (str "Document id " (.. doc-change -doc -id) " " (.. doc-change -type))))

(defn token-result->roles
  [^js/firebase.auth.IdTokenResult token-result]
  (goog.object/getValueByKeys token-result #js ["claims" "roles"]))

(defn admin?
  [^js roles]
  (goog.object/get roles "admin"))

(defn subscribe-user!
  "Set a subscription for the document associated with the currently
  authenticated user. Subscribe when the user logs in, and unsubscribe when
  the user logs out."
  [^js/firebase.User user ^js/firebase.auth.IdTokenResult token-result]
  (let [roles (token-result->roles token-result)]
    ;; (prn "ROLES" roles)
    ;; There might be no roles in the custom claims of the JWT returned by
    ;; Firebase Auth. I still have to trigger a cloud function when a user is
    ;; created. But even when the cloud function is implemented, for a short
    ;; time a new user will have no roles in the custom claims of his JWT.
    (if (and roles (admin? roles))
      (do
        (prn "Subscribe to admin stuff and show admin UI" (.. user -uid))
        (reset! state/is-admin-signed-in? true))
      (do
        (prn "Subscribe to normal (non-admin) stuff" (.. user -uid))
        ;; TODO: not necessary? should set false when logging out
        (reset! state/is-admin-signed-in? false)))))

(defn subscribe-collection!
  "Set a subscription for a Firestore collection in the app state."
  [{:keys [collection ratom]}]
  (let [k (keyword collection)
        unsubscribe! (db-docs-subscribe! {:collection collection
                                          :firestore @state/db
                                          :ratom ratom})]
    (swap! state/subscriptions assoc k unsubscribe!)))

(defn subscribe-quotes!
  "Set a subscription for the documents in the `quotes` collection."
  []
  (subscribe-collection! {:collection "quotes" :ratom state/quotes}))

(defn subscribe-tags!
  "Set a subscription for the documents in the `tags` collection."
  []
  (subscribe-collection! {:collection "tags" :ratom state/tags}))

(defn subscribe-quotes-changes!
  []
  (let [unsubscribe! (db-docs-change-subscribe! {:collection "quotes"
                                                 :f log-change!
                                                 :firestore @state/db})]
    (prn "unsubscribe!" unsubscribe!)))

;; TODO: here I need a more flexible function than db-docs-subscribe! It needs
;; to accept a query
(defn subscribe-favorite-quotes!
  "Set a subscription for the documents in the `favorite_quotes` collection."
  []
  (subscribe-collection! {:collection "favorite_quotes" :ratom state/favorite-quotes}))

(defn subscribe-favorite-quotes-2!
  "Set a subscription for the documents in the `favorite_quotes` collection."
  [^js user]
  (prn "subscribe-favorite-quotes-2! user-id" (.-uid user))
  (db-collection-subscribe! {:collection "favorite_quotes"
                             :firestore @state/db
                             :ratom state/favorite-quotes
                             :user-id (.-uid user)}))