(ns minimalquotes.components.buttons-cards
  (:require
   [devcards.core :as dc :refer [defcard]]
   [minimalquotes.components.buttons :as btn]
   [minimalquotes.fakes :as fakes]))

(defcard "# Buttons")

(defcard cancel-button-card
  (let [props {:on-click (fn [_] (js/alert "Cancel"))}]
    (dc/reagent [btn/cancel props])))

(defcard login-button-card
  (let [props {:on-click (fn [_] (js/alert "Login"))}]
    (dc/reagent [btn/login props])))

(defcard logout-button-card
  (let [props {:on-click (fn [_] (js/alert "Logout"))
               :user fakes/user}]
    (dc/reagent [btn/logout props])))

(defcard submit-button-card
  (dc/reagent [btn/submit]))
