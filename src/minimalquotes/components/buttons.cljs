(ns minimalquotes.components.buttons
  (:require [minimalquotes.components.icons :refer [icon-login]]))

(defn button
  [{:keys [color data-attributes icon on-click text]
    :or {color "blue"
         data-attributes {}}}]
  (let [button-props {:class ["font-bold" "rounded" "bg-transparent"
                              (str "text-" color "-500")
                              "px-4" "py-2"
                              (when (:data-tooltip data-attributes) "tooltip")
                              "border" (str "border-" color "-500")
                              (str "hover:bg-" color "-700")
                              "hover:text-white" "hover:border-transparent"]
                      :on-click on-click
                      :type "button"}
        icon-props {:css-classes ["w-4" "h-4" "fill-current" "ml-2"]}]
    [:button (merge button-props data-attributes)
     [:span data-attributes text]
     (when icon
       [icon (merge icon-props {:data-attributes data-attributes})])]))

(defn cancel
  [{:keys [on-click]}]
  [:button {:class ["font-bold" "rounded" "bg-transparent" "text-blue-500"
                    "px-4" "py-2"
                    "border" "border-blue-500"
                    "hover:bg-blue-700" "hover:text-white" "hover:border-transparent"]
            :on-click on-click
            :type "button"}
   [:span "Cancel"]])

(defn login
  [{:keys [on-click]}]
  [:button {:class ["font-bold" "rounded"
                    "bg-transparent" "text-blue-500"
                    "px-4" "py-2"
                    "border" "border-blue-500"
                    "tooltip"
                    "inline-flex"
                    "items-center"
                    "hover:bg-blue-700" "hover:text-white" "hover:border-transparent"]
            :data-tooltip "Login"
            :on-click on-click
            :type "button"}
   [:span "Login"]
   [icon-login {:css-classes ["w-4" "h-4" "fill-current" "ml-2"]}]])

(defn logout
  [{:keys [on-click user]}]
  [:button {:class ["font-bold" "rounded" "items-center"
                    "bg-transparent" "text-blue-500"
                    "px-2" "py-1"
                    "border" "border-blue-500"
                    "tooltip"
                    "hover:bg-blue-700" "hover:text-white" "hover:border-transparent"]
            :data-tooltip "Logout"
            :on-click on-click
            :type "button"}
   [:figure
    [:img {:alt "user avatar"
           :class ["w-8 h-auto"]
           :src (:photo-url user)}]]])

(defn submit
  []
  [:button {:class ["font-bold" "rounded" "bg-blue-500" "text-white"
                    "px-4" "py-2"
                    "hover:bg-blue-700"
                    "focus:outline-none" "focus:shadow-outline"]
            :type "input"} "Submit"])
