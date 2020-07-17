(ns minimalquotes.components.quotes
  (:require
   [minimalquotes.firebase.firestore :refer [db-doc-create!
                                             db-path-delete!
                                             db-path-upsert!
                                             now
                                             quotes-with-tag
                                             server-timestamp]]
   [minimalquotes.components.quote :refer [quote-card]]
   [minimalquotes.components.quote-forms :refer [button-add-new-quote-modal]]
   [minimalquotes.state :as state]
   [minimalquotes.utils :refer [k->str]]))

; (def debug-css "bg-green-200")
(def debug-css "")

(defn make-m->li
  "Given a user id and the callbacks that perform side-effects on a quote,
  return a function that maps a quote (id + values) to a <li> element."
  [{:keys [delete-quote! edit-quote! user]}]
  (fn m->li
    [[k {:keys [author tags text]
         :as m}]]
    ;; (prn "m->li" "m" m "user" user)
    (let [doc-id (k->str k)
          delete! (partial delete-quote! doc-id)
          edit! (partial edit-quote! doc-id m)]
      ^{:key doc-id} [:li {:class ["flex" "items-stretch"
                                   debug-css]}
                      [quote-card {:delete! delete!
                                   :edit! edit!
                                   :id doc-id
                                   :tags tags
                                   :quote-author author
                                   :quote-text text
                                   :user user}]])))

(defn make-on-quotes-click
  "Create a single on-click event handler for operations on quotes that don't
  require confirmation from the user (i.e. no modals appear).
  This single event handler exploits DOM event delegation. For this to work
  property, each quote must have the necessary data-attributes.
  TODO: decide which operations should be allowed for anonymous users."
  [{:keys [on-click-tag on-like-quote on-share-quote user]}]
  (fn on-click [^js e]
    (let [quote-id (.. e -target -dataset -id)
          op (.. e -target -dataset -operation)
          tag-name (.. e -target -dataset -tag)]
      (when tag-name
        (on-click-tag tag-name))

      (when (and user quote-id op)
        (case op
          "like" (on-like-quote user quote-id)
          "share" (on-share-quote user quote-id)
          (prn (str "Not implemented for op: " op)))))))

(defn quotes
  "List of the quotes currently on screen, arranged in a grid layout.
  TODO: add filters for quotes."
  [{:keys [delete-quote! edit-quote! entries on-add-quote on-click-tag
           on-like-quote on-share-quote user]}]
  ;; (prn "=== quotes entries ===" entries)
  (let [on-click (make-on-quotes-click {:on-click-tag on-click-tag
                                        :on-like-quote on-like-quote
                                        :on-share-quote on-share-quote
                                        :user user})
        m->li (make-m->li {:delete-quote! delete-quote!
                           :edit-quote! edit-quote!
                           :user user})]
    [:ul {:class ["grid"
                  "grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4"
                  "gap-4"]
          :on-click on-click}
     (when user
       [button-add-new-quote-modal {:on-confirm on-add-quote}])
     (map m->li entries)]))

(defn form-tags->m-tag
  [form-tags]
  (let [tag-names (map #(.trim %) form-tags)
        f (fn [acc cv] (assoc acc (keyword cv) true))]
    (reduce f {} tag-names)))

;; TODO: spec for entries

(defn quotes-container
  "Quotes component that extracts its required props from the app state."
  []
  (let [firestore @state/db
        user @state/user
        user-id (:uid user)
        ; A quote in @state/quotes contains only a map (or array?) of tags
        f (fn [[k m]]
            (let [tags (select-keys @state/tags (keys (:tags m)))
                  entry {k (assoc m :tags tags)}]
              ;; (prn "entry" entry "k" k)
              entry))
        entries (reduce into {} (map f @state/quotes))]
    [quotes {:entries entries
             :on-add-quote (fn [m-form]
                             (let [m-tag (form-tags->m-tag (:tags m-form))
                                   q (assoc m-form :tags m-tag)]
                               (db-doc-create! {:collection "quotes"
                                                :firestore firestore
                                                :m (merge q {:createdAt (server-timestamp)
                                                             :createdBy user-id})})))
             :on-click-tag (fn [name]
                             (quotes-with-tag firestore state/quotes name))
             :delete-quote! (fn [quote-id]
                              (db-path-delete! {:doc-path (str "quotes/" quote-id)
                                                :firestore firestore}))
             :edit-quote! (fn [quote-id m-state m-form]
                            (let [m-tag (form-tags->m-tag (:tags m-form))
                                  q (assoc m-form :tags m-tag)]
                              (db-path-upsert! {:doc-path (str "quotes/" quote-id)
                                                :firestore firestore
                                                :m (merge m-state q {:lastEditedAt (server-timestamp)
                                                                     :lastEditedBy user-id})})))
             :on-like-quote (fn [user quote-id]
                              (let [doc-path (str "quotes/" quote-id)
                                    k (keyword quote-id)
                                    q (k entries)]
                                ;; TODO: the tags in q are the tags from state,
                                ;; which come from the tags collection, but I
                                ;; would like to have just the data structure
                                ;; called m-tag (see above).
                                (prn "TODO: like this quote and add it to favorite quotes" user quote-id)
                                (if (:likes q)
                                  (db-path-upsert! {:doc-path doc-path
                                                    :firestore firestore
                                                    :m (update q :likes inc)})
                                  (db-path-upsert! {:doc-path doc-path
                                                    :firestore firestore
                                                    :m (merge q {:likes 1})}))))
             :on-share-quote (fn [user quote-id] (prn "TODO: share quote" user quote-id))
             :user user}]))
