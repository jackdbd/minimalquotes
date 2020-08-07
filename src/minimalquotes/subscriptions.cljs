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
     [db-docs-change-subscribe! subscribe update-state-from-firestore!]]
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

(defn make-on-each-snapshot
  [ratom]
  (fn on-each-snapshot
    [^js query-document-snapshot]
    (comment
      (prn "each doc snapshot"
           "id" (.-id query-document-snapshot)
           "data" (.data query-document-snapshot)))
    (update-state-from-firestore! {:ratom ratom} query-document-snapshot)))

(defn subscribe-tags!
  "Set a subscription for the documents in the `tags` collection."
  []
  (let [collection-path "tags"
        k (keyword collection-path)
        m {:on-each-snapshot (make-on-each-snapshot state/tags)}
        unsubscribe (subscribe @state/db collection-path m)]
    (swap! state/subscriptions assoc k unsubscribe)))

(defn subscribe-quotes-changes!
  []
  (let [unsubscribe! (db-docs-change-subscribe! {:collection "quotes"
                                                 :f log-change!
                                                 :firestore @state/db})]
    (prn "unsubscribe!" unsubscribe!)))

(defn subscribe-favorite-quotes!
  "Set a subscription for the documents in the `favorite_quotes` collection."
  [user-id]
  (let [collection-path "favorite_quotes"
        k (keyword collection-path)
        m {:on-each-snapshot (make-on-each-snapshot state/favorite-quotes)}
        q {:where [["userId" "==" user-id]]}
        unsubscribe (subscribe @state/db collection-path m q)]
    (swap! state/subscriptions assoc k unsubscribe)))
