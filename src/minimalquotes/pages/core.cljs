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
    [minimalquotes.firebase.firestore :refer [db-doc-create! now server-timestamp]]
    [minimalquotes.routes :refer [path-for]]
    [minimalquotes.state :as state]
    [reagent.core :as r]
    ;;  ["@windmill/react-ui" :as windmill-react-ui]
    [reagent.session :as session]))

;; (def windmill-button (r/adapt-react-class windmill-react-ui/Button))

(defn f-quote->li
  [[id m]]
  ^{:key id} [:li (str (:text m) " -- " (:author m))])

(defn about-page-content
  []
  (fn []
    [:div
     ;;  [windmill-button {:size "larger" :children "Hi there"}]
     [:p "About page"]]))

(defn tags-reducer
  [acc cv]
  (assoc acc cv true))

(defn admin-page-content
  []
  (fn []
    (let [user (.-currentUser (js/firebase.auth))
          ;; TODO: pass user as props?
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
       [admin/users {:user-id user-id}]
       [admin/tags {:firestore firestore :tags tags :user-id user-id}]])))

(defn quotes-page-content
  []
  (fn []
    [quotes-container]))

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
                 {:href (path-for :minimalquotes.routes/quotes {:tag "wisdom"})
                  :label "Wisdom quotes"}
                 {:href (path-for :minimalquotes.routes/quotes
                                  {:author "Zen" :tag "friendship"})
                  :label "Zen Friendship quotes"}
                 {:href (path-for :minimalquotes.routes/quotes {:author "Buddha"})
                  :label "Buddha quotes"}
                 {:href (path-for :minimalquotes.routes/quotes {:author "Buddha" :tag "wisdom"}) :label "Buddha wisdom quotes"}
                 {:href (path-for :minimalquotes.routes/tags) :label "Tags"}
                 {:href (path-for :minimalquotes.routes/about) :label "About"}]]
      [:<> [modal-window]
       [:div {:class ["container"]}
        (when user
          [:div (str "You are " (.-displayName user))])
        [header
         {:links (if @state/is-admin-signed-in?
                   (conj links {:href (path-for :minimalquotes.routes/admin)
                                :label "Admin"})
                   links)
          :login-href (path-for :minimalquotes.routes/sign-in)
          :on-logout #(auth/sign-out)
          :user user}]]
       [page]
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
