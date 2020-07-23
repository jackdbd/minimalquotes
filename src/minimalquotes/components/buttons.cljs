(ns minimalquotes.components.buttons)

; (def debug-css "border border-red-500 border-dashed")
(def debug-css "")

(defn button
  [{:keys [color data-attributes direction icon on-click text],
    :or {color "blue", data-attributes {}, direction "ltr"}}]
  (let [margin-tailwind-class (if (= "ltr" direction) "ml-1" "mr-1")
        button-props {:class ["font-bold" "rounded" "bg-transparent" "px-2"
                              "py-2" (str "text-" color "-500")
                              (when (:data-tooltip data-attributes) "tooltip")
                              "border" (str "border-" color "-500")
                              (str "hover:bg-" color "-700") "hover:text-white"
                              "hover:border-transparent"],
                      :on-click on-click,
                      :style {:direction direction},
                      :type "button"}
        icon-props {:css-classes ["w-4" "h-4" "fill-current"
                                  margin-tailwind-class]}]
    [:button (merge button-props data-attributes)
     [:div {:class ["inline-flex" "items-center" debug-css]}
      [:span data-attributes text]
      (when icon
        [icon (merge icon-props {:data-attributes data-attributes})])]]))

;; (defn logout
;;   [{:keys [color logout! user]
;;     :or {color "blue"}}]
;;   [:button {:class ["font-bold" "rounded" "bg-transparent"
;;                     "px-2" "py-2"
;;                     (str "text-" color "-500")
;;                     "items-center"
;;                     "tooltip"
;;                     "border" (str "border-" color "-500")
;;                     (str "hover:bg-" color "-700")
;;                     "hover:text-white" "hover:border-transparent"]
;;             :data-tooltip "Logout"
;;             :on-click #(logout!)
;;             :type "button"}
;;    [:figure
;;     [:img {:alt "user avatar"
;;            :class ["w-8 h-auto"]
;;            :src (:photo-url user)}]]])

(defn submit
  [{:keys [color disabled text],
    :or {color "blue", disabled false, text "Submit"}}]
  [:button
   {:class ["font-bold" "rounded" "text-white" "px-2" "py-2"
            (str "bg-" color "-500") (str "hover:bg-" color "-700")
            "focus:outline-none" "focus:shadow-outline"],
    :disabled disabled,
    :type "submit"} text])
