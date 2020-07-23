(ns minimalquotes.components.modal-cards
  (:require [devcards.core :as dc :refer [defcard]]
            [minimalquotes.components.buttons :as btn]
            [minimalquotes.components.modal :refer [modal! modal-window]]))

(defcard "# Modal window (stateful)")

(defn app-example
  []
  (let [on-cancel (fn [_] (modal! nil))]
    [:<> [modal-window]
     [:div [:h3 "Click different buttons to show a different modal"]
      [:div {:class ["flex" "justify-between"]}
       [:div
        [btn/button
         {:text "modal 0",
          :on-click #(modal! [:div [:p "This is inside modal 0"]
                              [btn/button
                               {:text "Close modal 0", :on-click on-cancel}]])}]
        [btn/button
         {:text "modal 1",
          :on-click #(modal!
                       [:div [:p "This is inside modal 1"]
                        [btn/button
                         {:text "Close modal 1", :on-click on-cancel}]])}]]]]]))

(defcard app-modal-card (dc/reagent [app-example]))
