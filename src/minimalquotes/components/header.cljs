(ns minimalquotes.components.header
  (:require
   [minimalquotes.components.buttons :as btn]
   [minimalquotes.components.icons :refer [icon-login]]
   [minimalquotes.routes :refer [path-for]]))

;; TODO: reuse layout from tags component (maybe create cluster component?)
(defn header
  [{:keys [margin-tailwind-class on-logout user]
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
       (if user
         [btn/button {:on-click #(on-logout) :text "Logout"}]
         [btn/button {:icon icon-login :text [:a {:href (path-for :sign-in)} "Sign in"]}])]]]
    [:div
     (when user
       [btn/button {:color "red"
                    :on-click #(js/alert (str "TODO: show favorite quotes for the user " (:uid user)))
                    :text "Favorite quotes"}])]]
   [:img {:alt "minimalquotes logo"
          :class ["m-auto"]
          :src "img/minimalquotes-logo.svg"}]])
