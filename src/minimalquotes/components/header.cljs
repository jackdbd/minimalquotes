(ns minimalquotes.components.header
  (:require
   [minimalquotes.components.buttons :as btn]
   [minimalquotes.components.icons :refer [icon-login]]))

(defn header
  [{:keys [on-login on-logout user]}]
  [:header
   [:div {:class ["flex" "justify-end"]}
    (if user
      [:div {:class ["flex" "items-center"]}
       [btn/button {:color "red"
                    :text "Favorite quotes"
                    :on-click #(js/alert (str "TODO: show favorite quotes for the user " (:uid user)))}]
       [btn/logout {:on-click on-logout
                    :user user}]]
      [btn/button {:icon icon-login
                   :on-click on-login
                   :text "Login"}])]
   [:img {:alt "minimalquotes logo"
          :class ["m-auto"]
          :src "img/minimalquotes-logo.svg"}]])
