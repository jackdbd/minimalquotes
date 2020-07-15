(ns minimalquotes.components.quotes
  (:require
   [minimalquotes.components.buttons :as btn]
   [minimalquotes.firebase.firestore :refer [db-doc-create!
                                             db-path-delete!
                                             db-path-upsert!
                                             now]]
   [minimalquotes.components.icons :refer [icon-login]]
   [minimalquotes.components.quote-editor :refer [button-add-new-quote-modal
                                                  button-delete-quote-modal
                                                  button-edit-quote-modal]]
   [minimalquotes.state :as state]
   [minimalquotes.utils :refer [k->str]]))

(defn quote-card
  "A card for a single quote. If the user is authenticated, he can
  edit/delete/like/share the quote."
  [{:keys [author delete! edit! id on-like on-share text user-id]}]
  [:div {:class ["quote" "rounded" "overflow-hidden" "shadow-lg"
                 "max-w-sm sm:max-w-md md:max-w-lg lg:max-w-xl xl:max-w-2xl"]}
   [:p (str text " ― " author)]
   [:div "tags"]
   [:div
    (when user-id
      [:<>
       [button-edit-quote-modal {:author author
                                 :on-confirm edit!
                                 :text text}]
       [button-delete-quote-modal {:author author
                                   :on-confirm delete!}]])
    (when user-id
      [btn/button {:data-attributes {:data-id id
                                     :data-operation "like"}
                   :icon icon-login
                   :on-click on-like
                   :text "Like"}])
    [btn/button {:data-attributes {:data-id id
                                   :data-operation "share"}
                 :on-click on-share
                 :text "Share"}]]])

(defn make-m->li
  "Given a user id and the callbacks that perform side-effects on a quote,
  return a function that maps a quote (id + values) to a <li> element."
  [{:keys [delete-quote! edit-quote! user-id]}]
  (fn m->li [[k-quote-id m]]
    (let [doc-id (k->str k-quote-id)
          delete! (partial delete-quote! doc-id)
          edit! (partial edit-quote! doc-id m)]
      ; (prn "m->li" "k-quote-id" k-quote-id "doc-id" doc-id "user-id" user-id "m" m)
      ^{:key doc-id} [:li
                      [quote-card {:author (:author m)
                                   :delete! delete!
                                   :edit! edit!
                                   :id doc-id
                                   :text (:text m)
                                   :user-id user-id}]])))

(defn make-on-quotes-click
  "Create a single on-click event handler for operations on quotes that don't
  require confirmation from the user (i.e. no modals appear).
  This single event handler exploits DOM event delegation. For this to work
  property, each quote must have the necessary data-attributes.
  TODO: decide which operations should be allowed for anonymous users."
  [{:keys [on-like-quote on-share-quote user-id]}]
  (fn on-click [e]
    (let [quote-id (.. e -target -dataset -id)
          op (.. e -target -dataset -operation)]
      ; (prn "make-on-quotes-click" user-id quote-id op)
      (when (and user-id quote-id op)
        (case op
          "like" (on-like-quote quote-id)
          "share" (on-share-quote quote-id)
          (println "Operation not implemented:" op))))))

(defn quotes
  "List of the quotes currently on screen, arranged in a grid layout.
  TODO: add filters for quotes."
  [{:keys [delete-quote! edit-quote! entries on-add-quote on-like-quote on-share-quote user-id]}]
  (let [on-click (make-on-quotes-click {:on-like-quote on-like-quote
                                        :on-share-quote on-share-quote
                                        :user-id user-id})
        m->li (make-m->li {:delete-quote! delete-quote!
                           :edit-quote! edit-quote!
                           :user-id user-id})]
    [:ul {:class ["grid"
                  "grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6"
                  "gap-4"]
          :on-click on-click}
     [button-add-new-quote-modal {:on-confirm on-add-quote}]
     (map m->li entries)]))

(defn quotes-container
  "Quotes component that extracts its required props from the app state."
  []
  (let [entries @state/quotes
        firestore @state/db
        user-id (:uid @state/user)]
    [quotes {:entries entries
             :on-add-quote (fn [m]
                             (db-doc-create! {:collection "quotes"
                                              :firestore firestore
                                              :m (merge m {:createdAt (now)
                                                           :createdBy user-id})}))
             :delete-quote! (fn [quote-id]
                              (db-path-delete! {:doc-path (str "quotes/" quote-id)
                                                :firestore firestore}))
             :edit-quote! (fn [quote-id m-old m-new]
                            (db-path-upsert! {:doc-path (str "quotes/" quote-id)
                                              :firestore firestore
                                              :m (merge m-old m-new {:lastEditedAt (now)
                                                                     :lastEditedBy user-id})}))
             :on-like-quote (fn [quote-id]
                              (let [doc-path (str "quotes/" quote-id)
                                    k (keyword quote-id)
                                    quote (k entries)]
                                (if (:likes quote)
                                  (db-path-upsert! {:doc-path doc-path
                                                    :firestore firestore
                                                    :m (update quote :likes inc)})
                                  (db-path-upsert! {:doc-path doc-path
                                                    :firestore firestore
                                                    :m (merge quote {:likes 1})}))))
             :on-share-quote (fn [] (prn "TODO: share quote"))
             :user-id user-id}]))
