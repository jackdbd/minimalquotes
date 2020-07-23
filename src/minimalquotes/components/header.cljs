(ns minimalquotes.components.header
  (:require [minimalquotes.components.buttons :as btn]
            [minimalquotes.components.icons :refer [icon-login]]))

;; TODO: reuse layout from tags component (maybe create cluster component?)
(defn header
  [{:keys [about-href home-href login-href margin-tailwind-class on-logout
           tags-href user],
    :or {margin-tailwind-class "m-1"}}]
  [:header
   [:div {:class ["flex" "justify-between"]}
    [:div {:class ["overflow-hidden" "p-2"]}
     [:ul {:class ["flex" "flex-wrap" (str "-" margin-tailwind-class)]}
      [:li {:class [margin-tailwind-class]}
       [:a {:href home-href} [btn/button {:text "Home"}]]]
      [:li {:class [margin-tailwind-class]}
       [:a {:href tags-href} [btn/button {:text "Tags"}]]]
      [:li {:class [margin-tailwind-class]}
       [:a {:href about-href} [btn/button {:text "About"}]]]
      [:li {:class [margin-tailwind-class]}
       (if user
         [btn/button {:on-click #(on-logout), :text "Logout"}]
         [:a {:href login-href}
          [btn/button {:icon icon-login, :text "Sign in"}]])]]]
    [:div
     (when user
       [btn/button
        {:color "red",
         :on-click #(js/alert (str "TODO: show favorite quotes for the user "
                                   (:uid user))),
         :text "Favorite quotes"}])]]])
