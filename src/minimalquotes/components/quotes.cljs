(ns minimalquotes.components.quotes
  (:require
    [clojure.set]
    [lambdaisland.glogi :as log]
    [minimalquotes.components.back-to-top :refer [back-to-top]]
    [minimalquotes.components.observed-spinner :refer [observed-spinner]]
    [minimalquotes.components.quote :refer [quote-card]]
    [minimalquotes.firebase.firestore :refer
     [db-doc-create! delete db-path-upsert! now server-timestamp]]
    [minimalquotes.state :as state]
    [minimalquotes.utils :refer [k->str log-error]]
    [reagent.session :as session]))

; (def debug-css "bg-green-200")
(def debug-css "")

(defn make-m->li
  "Given a user id and the callbacks that perform side-effects on a quote,
  return a function that maps a quote (id + values) to a <li> element."
  [{:keys [delete-quote! edit-quote! user]}]
  (fn m->li [[k {:keys [author is-liked likes tags text] :as m}]]
    (let [doc-id (k->str k)
          on-delete (partial delete-quote! doc-id)
          on-edit (partial edit-quote! doc-id m)
          toggle-like-button-text (if (= 0 likes) "Like" (str likes " Like"))]
      ^{:key doc-id}
      [:li {:class ["quote-card-list-item" "flex" "items-stretch" debug-css]}
       [quote-card {:id doc-id
                    :is-liked is-liked
                    :like-button-text toggle-like-button-text
                    :on-delete on-delete
                    :on-edit on-edit
                    :tags tags
                    :quote-author author
                    :quote-text text
                    :unlike-button-text toggle-like-button-text
                    :user user}]])))

(defn make-on-quotes-click
  "Create a single on-click event handler for operations on quotes that don't
  require confirmation from the user (i.e. no modals appear).
  This single event handler exploits DOM event delegation. For this to work
  property, each quote must have the necessary data-attributes.
  TODO: decide which operations should be allowed for anonymous users."
  [{:keys [on-share-quote on-toggle-like-quote user]}]
  (fn on-click [^js e]
    (let [quote-id (.. e -target -dataset -id)
          op (.. e -target -dataset -operation)]
      (when (and user quote-id op)
        (case op
          "share" (on-share-quote user quote-id)
          "toggle-like" (on-toggle-like-quote user quote-id)
          (prn (str "Not implemented for op: " op)))))))

(defn quotes
  "List of the quotes currently on screen, arranged in a grid layout.
  TODO: add filters for quotes."
  [{:keys [delete-quote! edit-quote! entries on-share-quote
           on-toggle-like-quote user]}]
  (let [on-click (make-on-quotes-click
                   {:on-toggle-like-quote on-toggle-like-quote
                    :on-share-quote on-share-quote
                    :user user})
        m->li (make-m->li {:delete-quote! delete-quote!
                           :edit-quote! edit-quote!
                           :user user})
        scrolling-container-id "scrolling-container"]
    ; TODO: the height should be 100vh - the height of all fixed height elements
    ; on the page (i.e. just the header in this case)
    [:div {:id scrolling-container-id :style {:overflow-y "scroll" :height "80vh"}}
     [:ul {:class ["grid" "gap-4"
                   "grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4"]
           :on-click on-click}
      (map m->li entries)]
     [observed-spinner {:root-id scrolling-container-id}]
     [back-to-top {:elem-id scrolling-container-id}]]))

(defn form-tags->m-tag
  [form-tags]
  (let [tag-names (map #(.trim %) form-tags)
        f (fn [acc cv] (assoc acc (keyword cv) true))]
    (reduce f {} tag-names)))

;; TODO: spec for entries

(defn present? [[_ v]] v)

(defn favorite?
  [quote-k]
  (let [f (fn [[_ {:keys [quoteId]}]] (keyword quoteId))
        favorites (->> @state/favorite-quotes
                       (map f))
        n (count (filter #(= quote-k %) favorites))]
    (if (> n 0) true false)))

(defn has-tag?
  [quote tag-name]
  (let [searched-tag? (fn [[_ tag]] (= tag-name (:name tag)))
        tag-k (first (first (filter searched-tag? @state/tags)))]
    (if tag-k (if (tag-k (:tags quote)) true false) false)))

(defn make-predicate
  [query-params]
  (fn predicate [[quote-k quote]]
    (let [author-name (:author query-params)
          tag-name (:tag query-params)
          only-favorite (if (:favorite query-params) true false)
          cond-author (if author-name (= author-name (:author quote)) true)
          cond-tag (if tag-name (has-tag? quote tag-name) true)
          cond-favorite (if only-favorite (favorite? quote-k) true)]
      (and cond-author cond-tag cond-favorite))))

(defn favorite-quote-ids
  []
  (let [f (fn [[_ fav]] (keyword (:quoteId fav)))]
    (map f @state/favorite-quotes)))

(defn quote->entry
  "Map a quote to an entry (i.e. a quote with some additional fields)."
  [[k m]]
  (let [n (count (clojure.set/intersection (set [k]) (set (favorite-quote-ids))))
        is-liked (if (> n 0) true false)
        tag-keys (->> (:tags m)
                      (filter present?)
                      (map #(first %)))
        tags (select-keys @state/tags tag-keys)
        entry {k (assoc m :tags tags :is-liked is-liked)}]
    entry))

(defn fav->li
  [[k {:keys [quoteId userId]}]]
  ^{:key k} [:li "fav-id: " (k->str k) " ― " "userId: " userId " ― " "quoteId: " quoteId])

; TODO: do not take selected-quotes (e.g. quotes having a certain tag, favorite
; quotes) from app state. Instead, take them directly with a Firestore query.
(defn quotes-container
  "Quotes component that extracts its required props from the app state."
  []
  (let [^js firestore @state/db
        user (.-currentUser (js/firebase.auth))
        user-id (if user (.-uid user) nil)
        query-params (session/get-in [:route :query-params])
        predicate (make-predicate query-params)
        selected-quotes (filter predicate @state/quotes)
        entries (reduce into {} (map quote->entry selected-quotes))]
    ; (prn "@state/quotes" @state/quotes)
    ; (prn "selected-quotes" selected-quotes)
    [:div
     [:label "debug favorite-quotes"]
     [:ul {:class ["debug favorite-quotes"]}
      (map fav->li @state/favorite-quotes)]
     [quotes {:entries entries
              :delete-quote! (fn [quote-id]
                               (log/debug :delete {:message (str "delete quote-id: " quote-id)})
                               (delete firestore
                                       (str "quotes/" quote-id)
                                       {:on-error log-error
                                        ; TODO: whenever a quote is deleted from
                                        ; Firestore, we remove it from the app
                                        ; state. But this is not enough:
                                        ; obviously
                                        ; a deleted quote can no longer be a
                                        ; favorite quote for any user, so we
                                        ; need a cloud function that removes all
                                        ; documents from the favorite_quotes
                                        ; collection.
                                        :on-success #(swap! state/quotes dissoc (keyword quote-id))}))
              :edit-quote! (fn [quote-id m-state m-form]
                             (let [m-tag (form-tags->m-tag (:tags m-form))
                                   q (assoc m-form :tags m-tag)]
                               (db-path-upsert! {:doc-path (str "quotes/" quote-id)
                                                 :firestore firestore
                                                 :m (merge m-state
                                                           q
                                                           {:lastEditedAt (server-timestamp)
                                                            :lastEditedBy user-id})})))
              :on-toggle-like-quote (fn [^js user quote-id]
                                      ;;  TODO: this is horrible.
                                      (let [quote-k (keyword quote-id)
                                            f (fn [[k fav]]
                                                (if (= quote-id (:quoteId fav))
                                                  (k->str k)
                                                  nil))
                                            fav-id (first (->> @state/favorite-quotes
                                                               (map f)
                                                               (filter some?)))]
                                        (if (favorite? quote-k)
                                          (delete firestore
                                                  (str "favorite_quotes/" fav-id)
                                                  {:on-error log-error
                                                   :on-success #(swap! state/favorite-quotes dissoc (keyword fav-id))})
                                          (db-doc-create! {:collection "favorite_quotes"
                                                           :firestore firestore
                                                           :m {:quoteId quote-id
                                                               :userId user-id}}))))
              :on-share-quote (fn [^js user quote-id]
                                (prn "TODO: share quote" user quote-id))
              :user user}]]))
