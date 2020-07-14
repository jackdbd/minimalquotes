(ns minimalquotes.components.header
  (:require [minimalquotes.components.buttons :as btn]))

(defn header
  [{:keys [on-login on-logout user]}]
  [:header
   [:div {:class ["flex" "justify-end"]}
    (if user
      [btn/logout {:on-click on-logout
                   :user user}]
      [btn/login {:on-click on-login}])]
   [:img {:alt "minimalquotes logo"
          :class ["m-auto"]
          :src "img/minimalquotes-logo.svg"}]])

(defn header-container
  [ratom]
  (let [m @ratom
        user (:user m)]
    [header {:on-login #(reset! ratom {:user {:photo-url "https://image.flaticon.com/icons/svg/2922/2922506.svg"}})
             :on-logout #(swap! ratom dissoc :user)
             :user user}]))
