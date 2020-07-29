(ns minimalquotes.components.admin
  (:require
    ["firebase/app" :as firebase]
    [minimalquotes.components.buttons :as btn]
    [minimalquotes.firebase.firestore :refer
     [db-doc-create! db-path-delete! db-path-upsert! now
      server-timestamp]]
    [minimalquotes.components.forms :refer
     [button-add-new-tag-modal button-edit-tag-modal]]
    [minimalquotes.components.icons :refer [icon-signal icon-trash]]
    [minimalquotes.utils :refer [k->str log-error]]))

(def label-css-classes ["block" "text-gray-700" "text-sm" "font-bold" "mb-2"])

(defn user
  [{:keys [user-id user]}]
  [:div {:class ["flex" "justify-between" "border" "items-center"]}
   [:span (:displayName user)]
   [:div
    (if (:isAdmin user)
      [btn/button
       {:icon icon-signal
        :data-attributes {:data-operation "unmake-admin"
                          :data-user-id user-id}
        :text "Unmake Admin"}]
      [btn/button
       {:icon icon-signal
        :data-attributes {:data-operation "make-admin" :data-user-id user-id}
        :text "Make Admin"}])
    [btn/button
     {:data-attributes {:data-operation "delete" :data-user-id user-id}
      :icon icon-trash
      :text "Delete"}]]])

(defn user->li [[k m]] ^{:key k} [:li [user {:user m :user-id (k->str k)}]])

;; TODO: how to get all signed-in users from Firebase Auth?
(def auth-users [])

(defn users
  [{:keys [user-id]}]
  [:<>
   [:label {:class label-css-classes :for "users"} "Users:"]
   [:ul {:title "users"
         :on-click (fn [^js e]
                     (let [uid (.. e -target -dataset -userId)
                           k (keyword uid)
                           op (.. e -target -dataset -operation)]
                       (prn "user-id" uid "k" k "operation" op)
                       (when user-id
                         (case op
                           ;;  "delete" (db-path-delete! {:doc-path doc-path
                           ;;  :firestore db})
                           ;;  "make-admin" (db-path-upsert!
                           ;;                {:doc-path doc-path
                           ;;                 :firestore db
                           ;;                 :m (merge (k auth-users)
                           ;;                           {:isAdmin true
                           ;;                            :lastEditedAt
                           ;;                            (server-timestamp)
                           ;;                            :lastEditedBy
                           ;;                            user-id})})
                           ;;  "unmake-admin" (db-path-upsert!
                           ;;                  {:doc-path doc-path
                           ;;                   :firestore db
                           ;;                   :m (merge (k auth-users)
                           ;;                             {:isAdmin false
                           ;;                              :lastEditedAt
                           ;;                              (server-timestamp)
                           ;;                              :lastEditedBy
                           ;;                              user-id})})
                           (prn (str "Not implemented for op: " op))))))}
    (map user->li auth-users)]])

(defn tag
  [{:keys [firestore tag tag-id user-id]
    :as props}]
  (let [color (:color tag)
        description (:description tag)
        name (:name tag)
        k (keyword tag-id)]
    [:div {:class ["flex" "justify-between" "border" "items-center"]}
     [button-edit-tag-modal {:on-submitted-values (fn [m]
                                                    (db-path-upsert! {:doc-path (str "tags/" tag-id)
                                                                      :firestore firestore
                                                                      :m (merge (k (:tags props))
                                                                                m
                                                                                {:lastEditedAt (server-timestamp)
                                                                                 :lastEditedBy user-id})}))
                             :tag-color color
                             :tag-description description
                             :tag-name name}]
     [:div
      [btn/button
       {:data-attributes {:data-operation "delete" :data-tag-id tag-id}
        :icon icon-trash
        :text "Delete"}]]]))

(defn make-tag->li
  [{:keys [firestore user-id]
    :as props}]
  (fn tag->li
    [[k m]]
    ^{:key k} [:li [tag {:firestore firestore
                         :tag m
                         :tag-id (k->str k)
                         :tags (:tags props)
                         :user-id user-id}]]))

(defn tags
  [{:keys [firestore user-id]
    :as props}]
  (let [tag->li (make-tag->li {:firestore firestore :tags (:tags props) :user-id user-id})]
    [:<>
     [btn/button {:on-click (fn []
                              (let [f (.httpsCallable (firebase/functions) "grantAdminRole")]
                                (-> (f #js {:email "nonexistinguser@gmail.com"})
                                    (.then (fn [x] (prn (.. x -data -result))))
                                    (.catch log-error))))
                  :text "Cloud function grantAdminRole"}]
     [btn/button {:on-click (fn []
                              (let [f (.httpsCallable (firebase/functions) "recursiveDelete")]
                                (-> (f #js {:path "quotes/"})
                                    (.then prn)
                                    (.catch log-error))))
                  :text "Cloud function recursiveDelete"}]
     [btn/button {:on-click (fn []
                              (let [f (.httpsCallable (firebase/functions) "generateFakes")]
                                (-> (f #js {:n 3})
                                    (.then (fn [x] (prn (.. x -data -result))))
                                    (.catch log-error))))
                  :text "Cloud function generateFakes"}]
     [btn/button {:on-click (fn []
                              (let [f (.httpsCallable (firebase/functions) "listAllUsers")]
                                ;; (f #js {:batchSize 10 :userProps #js ["email"
                                ;; "displayName"]})
                                (-> (f #js {:batchSize 10})
                                    (.then (fn [x] (prn (js->clj x))))
                                    (.catch log-error))))
                  :text "Cloud function listAllUsers"}]
     [:label {:class label-css-classes :for "tags"} "Tags"]
     [:ul {:title "tags"
           :on-click (fn [^js e]
                       (let [tag-id (.. e -target -dataset -tagId)
                             op (.. e -target -dataset -operation)
                             doc-path (str "tags/" tag-id)]
                         (when tag-id
                           (case op
                             "delete" (db-path-delete! {:doc-path doc-path
                                                        :firestore firestore})
                             (prn (str "Not implemented for op: " op))))))}
      (map tag->li (:tags props))]
     [button-add-new-tag-modal {:on-submitted-values (fn [m]
                                                       (db-doc-create!
                                                         {:collection "tags"
                                                          :firestore firestore
                                                          :m (merge m
                                                                    {:createdAt (server-timestamp)
                                                                     :createdBy user-id})}))}]]))
