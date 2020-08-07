(ns minimalquotes.pages.core
  "Page components and translation from routes to pages."
  (:require
    [minimalquotes.components.admin :as admin]
    [minimalquotes.components.footer :refer [footer]]
    [minimalquotes.components.header :refer [header]]
    [minimalquotes.components.forms :refer [button-add-new-quote-modal]]
    [minimalquotes.components.modal :refer [modal-window]]
    [minimalquotes.components.quotes :refer [quotes-container]]
    [minimalquotes.components.tags :refer [tags-container]]
    [minimalquotes.firebase.auth :as auth]
    [minimalquotes.firebase.firestore :refer
     [db-doc-create! now query server-timestamp update-state-from-firestore!]]
    [minimalquotes.routes :refer [path-for]]
    [minimalquotes.state :as state]
    [reagent.core :as r]
    [reagent.session :as session]
    ["@windmill/react-ui" :as wui]))

(defn f-quote->li
  [[id m]]
  ^{:key id} [:li (str (:text m) " -- " (:author m))])

(defn about-page-content
  []
  (fn []
    [:div
     [:p "About page"]]))

(defn tags-reducer
  [acc cv]
  (assoc acc cv true))

(defn admin-page-content
  []
  (fn []
    (let [user (.-currentUser (js/firebase.auth))
          user-id (if user (.-uid user) nil)
          firestore @state/db
          on-submit-quote-form (fn [m]
                                 ;; TODO: the quote author, text or tags should
                                 ;; be validated by a cloud function (e.g. one
                                 ;; could type a swear word for a tag). This
                                 ;; form submit could write to a `pendingQuotes`
                                 ;; collection, then a cloud function would take
                                 ;; a quote and its tags, validate everything,
                                 ;; then create documents in tags and quotes
                                 ;; collections, and finally deleting the quote
                                 ;; from the `pendingQuotes` collection. All of
                                 ;; these operations should be executed in a
                                 ;; transaction.
                                 (prn "TODO: on-submit-quote-form extract tags" (:tags m))
                                 (db-doc-create! {:collection "quotes"
                                                  :firestore firestore
                                                  :m (merge m
                                                            {:tags (reduce tags-reducer {} (:tags m))}
                                                            {:createdAt (server-timestamp)
                                                             :createdBy user-id})}))
          tags @state/tags]
      [:div
       [button-add-new-quote-modal
        {:on-submitted-values on-submit-quote-form :tags tags}]
       [admin/tags {:firestore firestore :tags tags :user-id user-id}]])))

(defn on-each-snapshot
  "Callback to invoke for each QueryDocumentSnapshot.
   https://firebase.google.com/docs/reference/js/firebase.firestore.QueryDocumentSnapshot"
  [^js query-document-snapshot]
  ; (prn "each id" (.-id query-document-snapshot))
  (update-state-from-firestore! {:ratom state/quotes} query-document-snapshot))

(defn on-first-snapshot
  "Callback to invoke for the first QueryDocumentSnapshot."
  [^js query-document-snapshot]
  ; (prn "first id" (.-id query-document-snapshot))
  (reset! state/first-quote query-document-snapshot))

(defn on-last-snapshot
  "Callback to invoke for the last QueryDocumentSnapshot."
  [^js query-document-snapshot]
  (comment
    (prn "last id" (.-id query-document-snapshot)
      "data" (.data query-document-snapshot)
      "exists" (.-exists query-document-snapshot)
      "fromCache" (.. query-document-snapshot -metadata -fromCache)
      "hasPendingWrites" (.. query-document-snapshot -metadata -hasPendingWrites)))
  (reset! state/last-quote query-document-snapshot))

(defn quotes-page-content
  []
  (let [results-per-page 10
        m {:on-each-snapshot on-each-snapshot
           :on-first-snapshot on-first-snapshot
           :on-last-snapshot on-last-snapshot}
        on-prev (fn []
                  (reset! state/quotes {})
                  (query @state/db "quotes" m {:limit-to-last results-per-page
                                               :order-by [["createdAt"]]
                                               :end-before @state/first-quote}))
        on-next (fn []
                  (reset! state/quotes {})
                  (query @state/db "quotes" m {:limit results-per-page
                                               :order-by [["createdAt"]]
                                               :start-after @state/last-quote}))]
    (query @state/db "quotes" m {:limit results-per-page
                                 :order-by [["createdAt"]]})
    (fn []
      (reset! state/quotes {})
      [:div
       [quotes-container]
       [:> wui/Button {:onClick on-prev} "Previous"]
       [:> wui/Button {:onClick on-next} "Next"]])))

(defn sign-in-page-content
  []
  (fn []
    (let [user (.-currentUser (js/firebase.auth))
          ui (get @state/state :firebase-ui)
          ui-config (get @state/state :firebase-ui-config)
          container-id "firebaseui-auth-container"
          did-mount (fn [_] (.start ui (str "#" container-id) ui-config))
          reagent-render (fn [] [:div {:class ["shadow-lg" "bg-blue-200"]}
                                 [:div {:id container-id}]])]
      (if user
        [:p "already signed in"]
        (r/create-class {:display-name "modal-window"
                         :component-did-mount did-mount
                         :reagent-render reagent-render})))))

(defn tags-page-content [] (fn [] [:div [tags-container]]))

(defn current-page
  []
  (fn []
    (let [user (.-currentUser (js/firebase.auth))
          page (:current-page (session/get :route))
          links [{:href (path-for :minimalquotes.routes/index) :label "Home"}
                 {:href (path-for :minimalquotes.routes/tags) :label "Tags"}
                 {:href (path-for :minimalquotes.routes/about) :label "About"}]]
      [:<>
       [modal-window]
       [header {:links (if @state/is-admin-signed-in?
                         (conj links {:href (path-for :minimalquotes.routes/admin)
                                      :label "Admin"})
                         links)
                :login-href (path-for :minimalquotes.routes/sign-in)
                :on-logout #(auth/sign-out)
                :user user}]
       [:main {:class ["bg-green-200" "flex-1"]}
        [page]]
       [footer]]
      )))

; TODO: a non-admin user could access HOST/admin by typing in the address bar.
; How to avoid it? With a check in this page-for? By redirecting him to home?
; What do I have to do with accountant and the browser history?
(defn page-for
  "Translate routes -> page components."
  [route-name]
  ; (prn "route-name" route-name)
  (case route-name
    :minimalquotes.routes/about #'about-page-content
    :minimalquotes.routes/admin #'admin-page-content
    :minimalquotes.routes/index #'quotes-page-content
    :minimalquotes.routes/quotes #'quotes-page-content
    :minimalquotes.routes/sign-in #'sign-in-page-content
    :minimalquotes.routes/tags #'tags-page-content))
