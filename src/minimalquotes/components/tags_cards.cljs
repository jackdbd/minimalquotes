(ns minimalquotes.components.tags-cards
  (:require
  ;;  ["@testing-library/react" :refer [cleanup fireEvent render]]
   [cljs.test :include-macros true :refer [is]]
   [devcards.core :as dc :refer [defcard deftest]]
   [minimalquotes.components.tags :refer [tag tags]]
   [minimalquotes.fakes :as fakes]
   [minimalquotes.utils :refer [testing-container]]
   [reagent.core :as r]))

(defcard "# Tag & Tags")

(defcard "## Tag")

(defcard tag-card
  (dc/reagent [tag fakes/tag-love]))

(defcard "## Tags")

(defn on-click-tag
  [tag-name]
  (js/alert (str "click tag: " tag-name)))

(defcard tags-card
  "Tags with default margins and no click handler."
  (let [props {:entries fakes/tags}]
    (dc/reagent [tags props])))

(defcard tags-custom-margins-card
  "Tags with custom margins and a single click handler for all tags (event delegation)."
  (let [props {:entries fakes/tags
               :margin-tailwind-class "m-3"
               :on-click-tag on-click-tag}]
    (dc/reagent [tags props])))

;; (deftest tags-tests-card
;;   (let [counter (atom 0)
;;         props {:entries {:love fakes/tag-love :money fakes/tag-money}
;;                :on-click-tag (fn [_] (swap! counter inc))}
;;         tr (render (r/as-element [tags props]) #js {:container (testing-container)})
;;         tag-love (.queryByText tr "love")
;;         tag-money (.queryByText tr "money")]
;;     (is (= 0 @counter) "Precondition: no clicks")
;;     (.click fireEvent tag-love)
;;     (.click fireEvent tag-money)
;;     (is (= 2 @counter) "After 2 clicks, counter is 2")
;;     (cleanup)))
