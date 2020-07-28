(ns minimalquotes.components.forms-cards
  (:require
    ;;  ["@testing-library/react" :refer [cleanup render]]
    [cljs.test :include-macros true :refer [is]]
    [devcards.core :as dc :refer [defcard deftest]]
    [minimalquotes.components.forms :refer
     [button-add-new-quote-modal button-delete-quote-modal
      button-edit-quote-modal quote-form]]
    [minimalquotes.components.modal :refer [modal-window]]
    [minimalquotes.fakes :as fakes]
    [minimalquotes.utils :refer [testing-container]]
    [reagent.core :as r]))

(defcard "# Quote forms")

(defn- on-click-cancel [] (js/alert "Cancel"))

(defn- on-submitted-values [m] (js/alert (js/JSON.stringify (clj->js m) nil 2)))

(defcard quote-form-card
         "The form can be used to create a new quote or edit an existing one."
         (let [props {:on-click-cancel on-click-cancel
                      :on-submitted-values on-submitted-values
                      :quote-author fakes/author-0
                      :quote-text fakes/text-0
                      :tags fakes/tags}]
           (dc/reagent [quote-form props])))

;; (deftest quote-form-tests-card
;;   (let [props {:on-click-cancel on-click-cancel
;;                :on-submitted-values on-submitted-values
;;                :quote-author fakes/author-0
;;                :quote-text fakes/text-0
;;                :tags fakes/tags}
;;         tr (render (r/as-element [quote-form props]) #js {:container
;;         (testing-container)})]
;;     (is (.getByTestId tr "quote-author") "Should have an input for the
;;     quote's author")
;;     (is (.getByTestId tr "quote-text") "Should have an input for the quote's
;;     text")
;;     (cleanup)))

(defcard
  quote-form-modal-buttons-card
  "These buttons show a modal with the form or a dialog inside."
  (let [props {:on-submitted-values on-submitted-values :tags fakes/tags}]
    (dc/reagent
      [:div [modal-window] [button-add-new-quote-modal props]
       [button-edit-quote-modal
        (merge props {:quote-author fakes/author-0 :quote-text fakes/text-0})]
       [button-delete-quote-modal
        {:on-delete (fn [author]
                      (js/alert (str "Quote by " author " deleted")))
         :quote-author fakes/author-0}]])))
