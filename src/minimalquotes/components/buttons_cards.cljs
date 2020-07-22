(ns minimalquotes.components.buttons-cards
  (:require
   [devcards.core :as dc :refer [defcard]]
   [minimalquotes.components.buttons :as btn]
   [minimalquotes.components.icons :refer [icon-login icon-share
                                           icon-edit
                                           icon-minus icon-plus
                                           icon-print
                                           icon-shopping-cart icon-signal
                                           icon-trash]]
   [minimalquotes.fakes :as fakes]))

(defcard "# Buttons")

(defcard buttons-with-no-icons
  (dc/reagent [:div
               [btn/button {:text "Cancel"}]
               [btn/button {:text "Confirm"}]]))

(defcard buttons-left-to-right-card
  "By default, buttons show the icon to the right (direction=ltr)."
  (dc/reagent [:div
               [btn/button {:icon icon-trash
                            :text "Delete"}]
               [btn/button {:icon icon-edit
                            :text "Edit"}]
               [btn/button {:icon icon-plus
                            :text "Plus"}]
               [btn/button {:icon icon-minus
                            :text "Minus"}]
               [btn/button {:icon icon-login
                            :text "Login"}]
               [btn/button {:icon icon-share
                            :text "Share"}]
               [btn/button {:icon icon-print
                            :text "Print"}]
               [btn/button {:icon icon-shopping-cart
                            :text "Checkout"}]
               [btn/button {:icon icon-signal
                            :text "Wi-Fi"}]]))

(defcard buttons-right-to-left-card
  "Buttons can show the icon to the left (direction=rtl)."
  (let [direction "rtl"]
    (dc/reagent [:div
                 [btn/button {:icon icon-trash
                              :direction direction
                              :text "Delete"}]
                 [btn/button {:icon icon-edit
                              :direction direction
                              :text "Edit"}]
                 [btn/button {:icon icon-plus
                              :direction direction
                              :text "Plus"}]
                 [btn/button {:icon icon-minus
                              :direction direction
                              :text "Minus"}]
                 [btn/button {:icon icon-login
                              :direction direction
                              :text "Login"}]
                 [btn/button {:icon icon-share
                              :direction direction
                              :text "Share"}]
                 [btn/button {:icon icon-print
                              :direction direction
                              :text "Print"}]
                 [btn/button {:icon icon-shopping-cart
                              :direction direction
                              :text "Checkout"}]
                 [btn/button {:icon icon-signal
                              :direction direction
                              :text "Wi-Fi"}]])))

;; (defcard logout-button-card
;;   (let [props {:logout! (fn [user]
;;                           (js/alert (str "User " (:display-name user) " logs out")))
;;                :user fakes/user}]
;;     (dc/reagent [:div
;;                  [btn/logout props]
;;                  [btn/logout (merge props {:color "red"})]
;;                  [btn/logout (merge props {:color "green"})]])))

(defcard submit-button-card
  (dc/reagent [:div
               [btn/submit]
               [btn/submit {:color "red"}]
               [btn/submit {:color "green"}]]))
