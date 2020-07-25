(ns minimalquotes.pages.core
  "Page components and translation from routes to pages."
  (:require [minimalquotes.firebase.firestore :refer
             [db-doc-create! db-path-upsert! now server-timestamp]]
            [minimalquotes.components.buttons :as btn]
            [minimalquotes.components.footer :refer [footer]]
            [minimalquotes.components.header :refer [header]]
            [minimalquotes.components.icons :refer [icon-signal icon-trash]]
            [minimalquotes.components.modal :refer [modal-window]]
            [minimalquotes.components.quote-forms :refer
             [button-add-new-quote-modal button-add-new-tag-modal]]
            [minimalquotes.components.quotes :refer [quotes-container]]
            [minimalquotes.components.tags :refer [tags]]
            [minimalquotes.fakes :as fakes]
            [minimalquotes.firebase.auth :as auth]
            [minimalquotes.routes :refer [path-for]]
            [minimalquotes.state :as state]
            [minimalquotes.utils :refer [k->str]]
            [reagent.core :as r]
            [reagent.session :as session]))

(defn f-quote->li
  [[id m]]
  ;; (prn "id" id "m" m)
  ^{:key id} [:li (str (:text m) " -- " (:author m))])

(defn ul-debug-quotes [] [:ul (map f-quote->li @state/quotes)])

(defn about-page-content [] (fn [] [:div "About page"]))

(def label-css-classes ["block" "text-gray-700" "text-sm" "font-bold" "mb-2"])

(defn user->li
  [[k m]]
  ^{:key k}
  [:li {:class ["flex" "justify-between" "border" "items-center"]}
   [:span (:displayName m)]
   [:div
    [btn/button
     {:icon icon-signal,
      :data-attributes {:data-user-id (k->str k)},
      ; TODO: move in parent and use event delegation
      :on-click (fn [e]
                  (prn "dataset user id" (.. e -target -dataset -userId))
                  (let [user-id (k->str k)
                        user (k @state/users)]
                    (db-path-upsert!
                     {:doc-path (str "users/" user-id),
                      :firestore @state/db,
                      :m (merge user
                                {:isAdmin true,
                                 :lastEditedAt (server-timestamp),
                                 :lastEditedBy (:uid @state/user)})}))),
      :text "Make Admin"}] [btn/button {:icon icon-trash, :text "Delete"}]]])

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
          tags @state/tags
          on-submit-tag-form
          (fn [m]
            (let [firestore @state/db
                  user @state/user
                  user-id (:uid user)]
              (db-doc-create! {:collection "tags",
                               :firestore firestore,
                               :m (merge m
                                         {:createdAt (server-timestamp),
                                          :createdBy user-id})})))]
      [:div
       [button-add-new-quote-modal
        {:on-submitted-values on-submit-quote-form, :tags tags}]
       [button-add-new-tag-modal {:on-submitted-values on-submit-tag-form}]
       [:label {:class label-css-classes, :for "users"} "Users:"]
       [:ol {:title "users"} (map user->li @state/users)]])))

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

(defn tags-page-content [] (fn [] [:div [tags {:entries fakes/tags}]]))

(defn current-page
  []
  (fn []
    (let [user @state/user
          page (:current-page (session/get :route))]
      [:<> [modal-window]
       [:div {:class ["container"]}
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
    :index #'home-page-content
    :quotes #'home-page-content
    :sign-in #'sign-in-page-content
    :tags #'tags-page-content))
