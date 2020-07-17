(ns minimalquotes.components.header
  (:require
   [minimalquotes.components.buttons :as btn]
   [minimalquotes.components.icons :refer [icon-login]]
   [minimalquotes.routes :refer [path-for]]))

;; TODO: reuse layout from tags component (maybe create cluster component?)
(defn header
  [{:keys [margin-tailwind-class on-login on-logout user]
    :or {margin-tailwind-class "m-1"}}]
  [:header
   [:div {:class ["flex" "justify-between"]}
    [:div {:class ["overflow-hidden" "p-2"]}
     [:ul {:class ["flex" "flex-wrap" (str "-" margin-tailwind-class)]}
      [:li {:class [margin-tailwind-class]}
       [btn/button {:text [:a {:href (path-for :index)} "Home"]}]]
      [:li {:class [margin-tailwind-class]}
       [btn/button {:text [:a {:href (path-for :about)} "About"]}]]
      [:li {:class [margin-tailwind-class]}
       [btn/button {:text [:a {:href (path-for :sign-in)} "Sign in"]}]]]]
    [:div
     (if user
       [:div {:class ["flex" "items-center"]}
        [btn/button {:color "red"
                     :text "Favorite quotes"
                     :on-click #(js/alert (str "TODO: show favorite quotes for the user " (:uid user)))}]
        [btn/logout {:on-click on-logout
                     :user user}]]
       [btn/button {:icon icon-login
                    :on-click on-login
                    :text "Login"}])]]
   [:img {:alt "minimalquotes logo"
          :class ["m-auto"]
          :src "img/minimalquotes-logo.svg"}]])
