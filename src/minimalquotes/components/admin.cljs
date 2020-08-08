(ns minimalquotes.components.admin
  (:require
    [minimalquotes.components.buttons :as btn]
    [minimalquotes.firebase.firestore :refer
     [db-doc-create! delete db-path-upsert! now server-timestamp]]
    [minimalquotes.components.forms :refer
     [button-add-new-tag-modal button-edit-tag-modal]]
    [minimalquotes.components.icons :refer [icon-trash]]
    [minimalquotes.state :as state]
    [minimalquotes.utils :refer [k->str log-error]]))

(def label-css-classes ["block" "text-gray-700" "text-sm" "font-bold" "mb-2"])

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
  (let [tag->li (make-tag->li {:firestore firestore :tags (:tags props) :user-id user-id})
        tag-ids (map #(first %) @state/tags)]
    [:<>
     [:p "admin actions"]
     [btn/button {:on-click (fn []
                              (let [f (.httpsCallable (js/firebase.functions) "grantAdminRole")]
                                (-> (f #js {:email "nonexistinguser@gmail.com"})
                                    (.then (fn [x] (prn (.. x -data -result))))
                                    (.catch log-error))))
                  :text "Cloud function grantAdminRole"}]
     [btn/button {:on-click (fn []
                              (let [f (.httpsCallable (js/firebase.functions) "recursiveDelete")]
                                (-> (f #js {:path "quotes/"})
                                    (.then prn)
                                    (.catch log-error))))
                  :text "Cloud function recursiveDelete"}]
     [btn/button {:on-click (fn []
                              (let [f (.httpsCallable (js/firebase.functions) "generateFakeQuotes")]
                                (-> (f #js {:n 10 :tagIds (clj->js tag-ids)})
                                    (.then (fn [x] (prn (.. x -data -result))))
                                    (.catch log-error))))
                  :text "Cloud function generateFakeQuotes"}]
     [btn/button {:on-click (fn []
                              (let [f (.httpsCallable (js/firebase.functions) "generateFakeTags")]
                                (-> (f #js {:n 5})
                                    (.then (fn [x] (prn (.. x -data -result))))
                                    (.catch log-error))))
                  :text "Cloud function generateFakeTags"}]
     [btn/button {:on-click (fn []
                              (let [f (.httpsCallable (js/firebase.functions) "listAllUsers")]
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
                             "delete" (delete firestore
                                              doc-path
                                              {:on-error log-error
                                               :on-success #(swap! state/tags dissoc (keyword tag-id))})
                             (prn (str "Not implemented for op: " op))))))}
      (map tag->li (:tags props))]
     [button-add-new-tag-modal {:on-submitted-values (fn [m]
                                                       (db-doc-create!
                                                         {:collection "tags"
                                                          :firestore firestore
                                                          :m (merge m
                                                                    {:createdAt (server-timestamp)
                                                                     :createdBy user-id})}))}]]))
