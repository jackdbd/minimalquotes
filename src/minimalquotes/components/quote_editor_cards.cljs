(ns minimalquotes.components.quote-editor-cards
  (:require
   ["@testing-library/react" :refer [cleanup render]]
   [cljs.test :include-macros true :refer [is]]
   [devcards.core :as dc :refer [defcard deftest]]
   [minimalquotes.components.quote-editor :refer [button-add-new-quote-modal
                                                  form-field-values
                                                  quote-editor-form]]
   [minimalquotes.components.modal :refer [modal-window]]
   [minimalquotes.fakes :as fakes]
   [minimalquotes.utils :refer [testing-container]]
   [reagent.core :as r]))

(defcard "Quote editor form.")

(defcard quote-editor-form-card
  (let [props {:author fakes/author-0
               :id "quote-editor-form-card"
               :on-cancel (fn [_]
                            (js/alert "Cancel"))
               :on-submit (fn [e]
                            (.preventDefault e)
                            (let [values (form-field-values "quote-editor-form-card")]
                              (js/alert values)))
               :text fakes/text-0}]
    (dc/reagent [quote-editor-form props])))

(deftest quote-editor-form-tests-card
  (let [props {:author fakes/author-0
               :id "quote-editor-form-tests-card"
               :on-cancel (fn [_]
                            (js/alert "Cancel"))
               :on-submit (fn [e]
                            (.preventDefault e)
                            (js/alert "Submitted (prevented default form submission)"))
               :text fakes/text-0}
        tr (render (r/as-element [quote-editor-form props]) #js {:container (testing-container)})]
    (is (.queryByTestId tr "author-form-field") "Should have a form field for the author")
    (cleanup)))

(defcard quote-editor-modal-card
  (let [props {:on-confirm (fn [m]
                             (js/alert (js/JSON.stringify (clj->js m))))}]
    (dc/reagent [:div
                 [modal-window]
                 [button-add-new-quote-modal props]])))
