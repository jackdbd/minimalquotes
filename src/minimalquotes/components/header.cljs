(ns minimalquotes.components.header
  (:require [minimalquotes.components.buttons :as btn]
            [minimalquotes.components.icons :refer [icon-login]]
            [minimalquotes.routes :refer [path-for]]))

(defn make-link->li
  [margin-tailwind-class]
  (fn link->li [{:keys [href label]}]
    ^{:key label}
    [:li {:class [margin-tailwind-class]}
     [:a {:href href} [btn/button {:text label}]]]))

;; TODO: reuse layout from tags component (maybe create cluster component?)
(defn header
  [{:keys [links login-href margin-tailwind-class on-logout user],
    :or {margin-tailwind-class "m-1"}}]
  (let [link->li (make-link->li margin-tailwind-class)]
    [:header
     [:div {:class ["flex" "justify-between"]}
      [:div {:class ["overflow-hidden" "p-2"]}
       [:ul {:class ["flex" "flex-wrap" (str "-" margin-tailwind-class)]}
        (map link->li links)
        (when (:isAdmin user)
          [:li {:class [margin-tailwind-class]}
           [:a {:href (path-for :admin)} [btn/button {:text "Admin"}]]])
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
           :text "Favorite quotes"}])]]]))
