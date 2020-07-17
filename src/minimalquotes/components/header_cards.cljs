(ns minimalquotes.components.header-cards
  (:require
   ["@testing-library/react" :refer [cleanup render]]
   [cljs.test :include-macros true :refer [is]]
   [devcards.core :as dc :refer [defcard deftest]]
   [minimalquotes.components.header :refer [header]]
   [minimalquotes.fakes :as fakes]
   [minimalquotes.utils :refer [testing-container]]
   [reagent.core :as r]))

(defcard "# Header component")

(defcard header-unauthenticated-card
  "Header for an unauthenticated user."
  (let [props {:on-login (fn [_] (js/alert "Login"))}]
    (dc/reagent [header props])))

(deftest header-unauthenticated-tests-card
  (let [props {:on-login (fn [_])}
        tr (render (r/as-element [header props]) #js {:container (testing-container)})]
    (is (.queryByText tr "Login") "Should contain a 'Login'")
    (is (nil? (.queryByAltText tr "user avatar")) "Should not contain a user avatar")
    (cleanup)))

(defcard header-authenticated-card
  "Header for an authenticated user."
  (let [props {:on-logout (fn [_] (js/alert "Logout"))
               :user fakes/user}]
    (dc/reagent [header props])))

(deftest header-authenticated-tests-card
  (let [props {:on-logout (fn [_])
               :user fakes/user}
        tr (render (r/as-element [header props]) #js {:container (testing-container)})]
    (is (.queryByAltText tr "user avatar") "Should contain a user avatar")
    (is (nil? (.queryByText tr "Login")) "Should not contain a 'Login'")
    (cleanup)))

(defn header-container
  [ratom]
  (let [m @ratom
        user (:user m)]
    [header {:on-login #(reset! ratom {:user fakes/user})
             :on-logout #(swap! ratom dissoc :user)
             :user user}]))

(defcard header-container-card
  (dc/reagent header-container)
  (r/atom {:user nil})
  {:history true :inspect-data true})
