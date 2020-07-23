(ns minimalquotes.components.actions
  (:require [minimalquotes.components.buttons :as btn]
            [minimalquotes.components.icons :refer [icon-like icon-share]]
            [minimalquotes.components.quote-forms :refer
             [button-delete-quote-modal button-edit-quote-modal]]))

; (def debug-css "bg-green-300")
(def debug-css "")

(defn action
  [{:keys [color description name]}]
  [btn/button
   {:color color,
    :data-attributes {:data-tag name, :data-tooltip description},
    :text name}])

(defn- make-on-click
  [{:keys [on-like on-share user]}]
  ;   (prn "make-on-click" on-like on-share)
  (if (or on-like on-share)
    (fn on-click [^js e]
      (let [quote-id (.. e -target -dataset -id)
            op (.. e -target -dataset -operation)]
        (case op
          "like" (when on-like (on-like user quote-id))
          "share" (when on-share (on-share user quote-id))
          nil)))
    nil))

(defn actions
  [{:keys [id margin-tailwind-class on-delete on-edit on-like on-share
           quote-author quote-text tags user],
    :or {margin-tailwind-class "m-2"}}]
  (let [on-click (make-on-click
                  {:on-like on-like, :on-share on-share, :user user})]
    [:div {:class ["overflow-hidden" "p-2" debug-css]}
     [:ul
      {:class ["flex" "flex-wrap" (str "-" margin-tailwind-class)],
       :on-click on-click}
      [:<>
       (when user
         [:<>
          [:li {:class [margin-tailwind-class]}
           [button-edit-quote-modal
            {:on-submitted-values on-edit,
             :quote-author quote-author,
             :quote-text quote-text,
             :tags tags}]]
          [:li {:class [margin-tailwind-class]}
           [button-delete-quote-modal
            {:on-delete on-delete, :quote-author quote-author}]]
          [:li
           {:class [margin-tailwind-class "hint--left"],
            :aria-label "Like this quote"}
           [btn/button
            {:data-attributes {:data-id id,
                               :data-operation "like",
                               ; :data-tooltip "Like this quote"
                               },
             :icon icon-like,
             :text "Like"}]]])
       [:li {:class [margin-tailwind-class "hint--right"], :aria-label "Share"}
        [btn/button
         {:data-attributes {:data-id id,
                            :data-operation "share",
                            ;  :data-tooltip "Share this quote"
                            },
          ; :direction "rtl" the direction is a user's preference,
          ; so it should come from the user's document
          :icon icon-share,
          :text "Share"}]]]]]))
