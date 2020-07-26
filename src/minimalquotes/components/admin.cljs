(ns minimalquotes.components.admin
  (:require [minimalquotes.components.buttons :as btn]
            [minimalquotes.firebase.firestore :refer
             [db-doc-create! db-path-delete! db-path-upsert! now
              server-timestamp]]
            [minimalquotes.components.forms :refer
             [button-add-new-tag-modal button-edit-tag-modal]]
            [minimalquotes.components.icons :refer [icon-signal icon-trash]]
            [minimalquotes.state :as state]
            [minimalquotes.utils :refer [k->str]]))

(def label-css-classes ["block" "text-gray-700" "text-sm" "font-bold" "mb-2"])

(defn user
  [{:keys [user-id user]}]
  [:div {:class ["flex" "justify-between" "border" "items-center"]}
   [:span (:displayName user)]
   [:div
    (if (:isAdmin user)
      [btn/button
       {:icon icon-signal,
        :data-attributes {:data-operation "unmake-admin",
                          :data-user-id user-id},
        :text "Unmake Admin"}]
      [btn/button
       {:icon icon-signal,
        :data-attributes {:data-operation "make-admin", :data-user-id user-id},
        :text "Make Admin"}])
    [btn/button
     {:data-attributes {:data-operation "delete", :data-user-id user-id},
      :icon icon-trash,
      :text "Delete"}]]])

(defn user->li [[k m]] ^{:key k} [:li [user {:user m, :user-id (k->str k)}]])

(defn users
  []
  [:<> [:label {:class label-css-classes, :for "users"} "Users:"]
   [:ul
    {:title "users",
     :on-click
     (fn [^js e]
       (let [user-id (.. e -target -dataset -userId)
             k (keyword user-id)
             op (.. e -target -dataset -operation)
             db @state/db
             doc-path (str "users/" user-id)]
         (when user-id
           (case op
             "delete" (db-path-delete! {:doc-path doc-path, :firestore db})
             "make-admin"
             (db-path-upsert! {:doc-path doc-path,
                               :firestore db,
                               :m (merge (k @state/users)
                                         {:isAdmin true,
                                          :lastEditedAt (server-timestamp),
                                          :lastEditedBy (:uid @state/user)})})
             "unmake-admin"
             (db-path-upsert! {:doc-path doc-path,
                               :firestore db,
                               :m (merge (k @state/users)
                                         {:isAdmin false,
                                          :lastEditedAt (server-timestamp),
                                          :lastEditedBy (:uid @state/user)})})
             (prn (str "Not implemented for op: " op))))))}
    (map user->li @state/users)]])

(defn tag
  [{:keys [tag-id tag]}]
  (let [color (:color tag)
        description (:description tag)
        name (:name tag)
        k (keyword tag-id)]
    [:div {:class ["flex" "justify-between" "border" "items-center"]}
     [button-edit-tag-modal
      {:on-submitted-values
       (fn [m]
         (db-path-upsert! {:doc-path (str "tags/" tag-id),
                           :firestore @state/db,
                           :m (merge (k @state/tags)
                                     m
                                     {:lastEditedAt (server-timestamp),
                                      :lastEditedBy (:uid @state/user)})})),
       :tag-color color,
       :tag-description description,
       :tag-name name}]
     [:div
      [btn/button
       {:data-attributes {:data-operation "delete", :data-tag-id tag-id},
        :icon icon-trash,
        :text "Delete"}]]]))

(defn tag->li [[k m]] ^{:key k} [:li [tag {:tag m, :tag-id (k->str k)}]])

(defn tags
  []
  [:<> [:label {:class label-css-classes, :for "tags"} "Tags"]
   [:ul
    {:title "tags",
     :on-click (fn [^js e]
                 (let [tag-id (.. e -target -dataset -tagId)
                       op (.. e -target -dataset -operation)
                       db @state/db
                       doc-path (str "tags/" tag-id)]
                   (when tag-id
                     (case op
                       "delete" (db-path-delete! {:doc-path doc-path,
                                                  :firestore db})
                       (prn (str "Not implemented for op: " op))))))}
    (map tag->li @state/tags)]
   [button-add-new-tag-modal
    {:on-submitted-values (fn [m]
                            (let [firestore @state/db
                                  user @state/user
                                  user-id (:uid user)]
                              (db-doc-create!
                               {:collection "tags",
                                :firestore firestore,
                                :m (merge m
                                          {:createdAt (server-timestamp),
                                           :createdBy user-id})})))}]])
