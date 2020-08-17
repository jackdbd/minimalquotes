(ns minimalquotes.components.header-cards
  (:require
    ;  ["@testing-library/react" :refer [cleanup render]]
    ;  [cljs.test :include-macros true :refer [is]]
    [devcards.core :as dc :refer [defcard]]
    ;  [devcards.core :as dc :refer [defcard deftest]]
    [minimalquotes.components.header :refer [header]]
    [minimalquotes.fakes :as fakes]
    ;  [minimalquotes.utils :refer [testing-container]]
    ;  [reagent.core :as r]
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    ))

;; TODO: shadow-cljs fails to load react testing library. It was working fine
;; some days ago. I think there is something wrong with the tests running in a
;; real browser, or with the devcards config, because the tests run without an
;; issue in the jsdom (i.e. in a Node.js environment).

(defcard "# Header component")

(defcard header-unauthenticated-card
         "Header for an unauthenticated user."
         (let [props {}] (dc/reagent [header props])))

; (deftest header-unauthenticated-tests-card
;   (let [props {:on-login (fn [_])}
;         tr (render (r/as-element [header props])
;                    #js {:container (testing-container)})]
;     (is (.queryByText tr "Sign in") "Should show 'Sign in'")
;     (cleanup)))

(defcard header-authenticated-card
         "Header for an authenticated user."
         (let [props {:on-logout (fn [_] (js/alert "Logout"))
                      :user fakes/user}]
           (dc/reagent [header props])))

; (deftest header-authenticated-tests-card
;   (let [props {:on-logout (fn [_]) :user fakes/user}
;         tr (render (r/as-element [header props])
;                    #js {:container (testing-container)})]
;     (is (.queryByText tr "Logout") "Should show 'Logout'")
;     (is (nil? (.queryByText tr "Sign in")) "Should not show 'Sign in'")
;     (cleanup)))
