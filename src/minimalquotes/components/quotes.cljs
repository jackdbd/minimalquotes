(ns minimalquotes.components.quotes
  (:require
   [minimalquotes.components.buttons :as btn]
   [minimalquotes.firebase.firestore :refer [db-path-delete! db-path-upsert!]]
   [minimalquotes.components.icons :refer [icon-login]]
   [minimalquotes.state :as state]))

(defn quote-card
  "A card for a single quote. If the user is authenticated, he can
  edit/delete/like/share the quote."
  [{:keys [author id on-delete on-edit on-like on-share text user-id]}]
  [:div {:class ["quote" "rounded" "overflow-hidden" "shadow-lg"
                 "max-w-sm sm:max-w-md md:max-w-lg lg:max-w-xl xl:max-w-2xl"]}
   [:p (str text " ― " author)]
   [:div "tags"]
   [:div
    (when user-id
      [btn/button {:data-attributes {:data-id id
                                     :data-operation "edit"}
                   :icon icon-login
                   :on-click on-edit
                   :text "Edit"}])
    (when user-id
      [btn/button {:data-attributes {:data-id id
                                     :data-operation "delete"}
                   :on-click on-delete
                   :text "Delete"}])
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
  "Given a user id, return a function that maps a quote (i.e. a map which
  contains the quote's author, id and text) to a <li> element."
  [user-id]
  (fn m->li [{:keys [author id text]}]
    ^{:key id} [:li
                [quote-card {:author author :id id :text text :user-id user-id}]]))

(defn make-on-quotes-click
  "Exploit DOM event delegation and create a single event handler for all
  operations on all quotes. Each quote must have the necessary data-attributes
  for this handler to work properly."
  [{:keys [on-delete-quote on-edit-quote on-like-quote on-share-quote user-id]}]
  (fn on-click [e]
    (let [quote-id (.. e -target -dataset -id)
          op (.. e -target -dataset -operation)]
      (when (and user-id quote-id op)
        (case op
          "delete" (on-delete-quote user-id quote-id)
          "edit" (on-edit-quote user-id quote-id)
          "like" (on-like-quote user-id quote-id)
          "share" (on-share-quote user-id quote-id)
          (println "Operation not implemented:" op))))))

(defn quotes
  "List of the quotes currently on screen, arranged in a grid layout.
  TODO: add filters for quotes."
  [{:keys [entries on-delete-quote on-edit-quote on-like-quote on-share-quote user-id]}]
  (let [on-click (make-on-quotes-click {:on-delete-quote on-delete-quote
                                        :on-edit-quote on-edit-quote
                                        :on-like-quote on-like-quote
                                        :on-share-quote on-share-quote
                                        :user-id user-id})
        m->li (make-m->li user-id)]
    [:ul {:class ["grid"
                  "grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6"
                  "gap-4"]
          :on-click on-click}
     (map m->li (vals entries))]))

(defn quotes-container
  "Quotes component that extracts its required props from the app state."
  []
  [quotes {:entries @state/quotes
           :on-delete-quote (fn [user-id quote-id]
                              (db-path-delete! {:doc-path (str "quotes/" quote-id)
                                                :firestore @state/db}))
           :on-edit-quote (fn [user-id quote-id]
                            (let [k (keyword quote-id)
                                  quote (k @state/quotes)]
                              (db-path-upsert! {:doc-path (str "quotes/" quote-id)
                                                :firestore @state/db
                                                :m (merge quote {:author "John Smith"})})))
           :on-like-quote (fn [user-id quote-id]
                            (let [doc-path (str "quotes/" quote-id)
                                  k (keyword quote-id)
                                  quote (k @state/quotes)]
                              (if (:likes quote)
                                (db-path-upsert! {:doc-path doc-path
                                                  :firestore @state/db
                                                  :m (update quote :likes inc)})
                                (db-path-upsert! {:doc-path doc-path
                                                  :firestore @state/db
                                                  :m (merge quote {:likes 1})}))))
           :on-share-quote (fn [] (prn "TODO"))
           :user-id (:uid @state/user)}])
