(ns minimalquotes.pages.core
  "Page components and translation from routes to pages."
  (:require
   [minimalquotes.components.footer :refer [footer]]
   [minimalquotes.components.header :refer [header]]
   [minimalquotes.components.modal :refer [modal-window]]
   [minimalquotes.components.quotes :refer [quotes-container]]
   [minimalquotes.firebase.auth :as auth]
   [minimalquotes.state :as state]
   [reagent.session :as session]))

(defn f-quote->li
  [[id m]]
  ;; (prn "id" id "m" m)
  ^{:key id} [:li (str (:text m) " -- " (:author m))])

(defn ul-debug-quotes
  []
  [:ul (map f-quote->li @state/quotes)])

(defn about-page-content []
  (fn []
    [:div "About page"]))

(defn home-page-content []
  (fn []
    [quotes-container]))

(defn sign-in-page-content []
  (fn []
    [:div "Sign-in page"]))

(defn current-page []
  (fn []
    (let [user @state/user
          page (:current-page (session/get :route))]
      ;; (prn "PAGE" page)
      [:<>
       [modal-window]
       [:div {:class ["container"]}
        [header {:on-login #(auth/sign-in-with-google)
                 :on-logout #(auth/sign-out)
                 :user user}]]
       (comment
         [ul-debug-quotes])
       [page]
       [footer]])))

(defn page-for
  "Translate routes -> page components."
  [route-name]
  (case route-name
    :about #'about-page-content
    :index #'home-page-content
    :sign-in #'sign-in-page-content))
