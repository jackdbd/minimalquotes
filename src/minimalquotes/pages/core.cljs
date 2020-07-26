(ns minimalquotes.pages.core
  "Page components and translation from routes to pages."
  (:require [minimalquotes.components.admin :as admin]
            [minimalquotes.components.footer :refer [footer]]
            [minimalquotes.components.header :refer [header]]
            [minimalquotes.components.forms :refer [button-add-new-quote-modal]]
            [minimalquotes.components.modal :refer [modal-window]]
            [minimalquotes.components.quotes :refer [quotes-container]]
            [minimalquotes.components.tags :refer [tags-container]]
            [minimalquotes.firebase.auth :as auth]
            [minimalquotes.firebase.firestore :refer
             [db-doc-create! now server-timestamp]]
            [minimalquotes.routes :refer [path-for]]
            [minimalquotes.state :as state]
            [reagent.core :as r]
            [reagent.session :as session]))

(defn f-quote->li
  [[id m]]
  ;; (prn "id" id "m" m)
  ^{:key id} [:li (str (:text m) " -- " (:author m))])

(defn ul-debug-quotes [] [:ul (map f-quote->li @state/quotes)])

(defn about-page-content [] (fn [] [:div "About page"]))

(defn admin-page-content
  []
  (fn []
    (let [on-submit-quote-form
          (fn [m]
            (let [firestore @state/db
                  user @state/user
                  user-id (:uid user)
                  q (assoc m :tags "TODO-m-tag")]
              (db-doc-create! {:collection "quotes",
                               :firestore firestore,
                               :m (merge q
                                         {:createdAt (server-timestamp),
                                          :createdBy user-id})})))
          tags @state/tags]
      [:div
       [button-add-new-quote-modal
        {:on-submitted-values on-submit-quote-form, :tags tags}] [admin/users]
       [admin/tags]])))

(defn favorite-quotes-page-content
  []
  (fn [] [quotes-container {:show-only-favorites true}]))

(defn home-page-content
  []
  (fn []
    (let [tag (get-in (session/get :route) [:query-params :tag])]
      (prn (str "=== TODO: show only quotes with tag " tag " ==="))
      [quotes-container])))

(defn sign-in-page-content
  []
  (fn []
    (let [user @state/user
          ui (get @state/state :firebase-ui)
          ui-config (get @state/state :firebase-ui-config)
          container-id "firebaseui-auth-container"
          did-mount (fn [_] (.start ui (str "#" container-id) ui-config))
          reagent-render (fn [] [:div {:class ["shadow-lg" "bg-blue-200"]}
                                 [:div {:id container-id}]])]
      (if user
        [:p "already signed in"]
        (r/create-class {:display-name "modal-window",
                         :component-did-mount did-mount,
                         :reagent-render reagent-render})))))

(defn tags-page-content [] (fn [] [:div [tags-container]]))

(defn current-page
  []
  (fn []
    (let [user @state/user
          page (:current-page (session/get :route))]
      [:<> [modal-window]
       [:div {:class ["container"]}
        [:div (str "You are " (:displayName @state/user))]
        [header
         {:links [{:href (path-for :index), :label "Home"}
                  {:href (path-for :tags), :label "Tags"}
                  {:href (path-for :about), :label "About"}],
          :login-href (path-for :sign-in),
          :on-logout #(auth/sign-out),
          :user user}]] (comment [ul-debug-quotes]) [page] [footer]])))

; TODO: a non-admin user could access HOST/admin by typing in the address bar.
; How to avoid it? With a check in this page-for? By redirecting him to home?
; What do I have to do with accountant and the browser history?
(defn page-for
  "Translate routes -> page components."
  [route-name]
  ; (prn "route-name" route-name)
  (case route-name
    :about #'about-page-content
    :admin #'admin-page-content
    ; :admin (if @state/user #'admin-page-content #'sign-in-page-content)
    :favorite-quotes #'favorite-quotes-page-content
    :index #'home-page-content
    :quotes #'home-page-content
    :sign-in #'sign-in-page-content
    :tags #'tags-page-content))
