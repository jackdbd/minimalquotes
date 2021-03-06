(ns minimalquotes.components.actions
  (:require
    [minimalquotes.components.buttons :as btn]
    [minimalquotes.components.forms :refer
     [button-delete-quote-modal button-edit-quote-modal]]
    [minimalquotes.components.icons :refer
     [icon-like icon-share icon-unlike]]
    [minimalquotes.utils :refer [encode]]))

; (def debug-css "bg-green-300")
(def debug-css "")

(defn action
  [{:keys [color description name]}]
  [btn/button
   {:color color
    :data-attributes {:data-tag name :data-tooltip description}
    :text name}])

(defn- make-on-click
  [{:keys [on-share on-toggle-like user]}]
  ;   (prn "make-on-click" on-like on-share)
  (if (or on-share on-toggle-like)
    (fn on-click [^js e]
      (let [quote-id (.. e -target -dataset -id)
            op (.. e -target -dataset -operation)]
        (case op
          "share" (when on-share (on-share user quote-id))
          "toggle-like" (when on-toggle-like (on-toggle-like user quote-id))
          nil)))
    nil))

(defn actions
  [{:keys [id is-liked like-button-text margin-tailwind-class on-delete
           on-edit on-share on-toggle-like quote-author quote-text tags
           unlike-button-text user]
    :or {is-liked false
         like-button-text "Like"
         margin-tailwind-class "m-2"
         unlike-button-text "Unlike"}}]
  (let [on-click (make-on-click {:on-share on-share
                                 :on-toggle-like on-toggle-like
                                 :user user})
        encoded-uri (encode (str quote-text " ― " quote-author))]
    [:div {:class ["actions" "overflow-hidden" "p-2" debug-css]}
     [:ul
      {:class ["flex" "flex-wrap" (str "-" margin-tailwind-class)]
       :on-click on-click}
      [:<>
       (when user
         [:<>
          [:li {:class [margin-tailwind-class]}
           [button-edit-quote-modal {:on-submitted-values on-edit
                                     :quote-author quote-author
                                     :quote-text quote-text
                                     :tags tags}]]
          [:li {:class [margin-tailwind-class]}
           [button-delete-quote-modal {:on-delete on-delete :quote-author quote-author}]]
          (if is-liked
            [:li
             {:class [margin-tailwind-class "hint--left"]
              :aria-label "Unlike this quote"}
             [btn/button
              {:data-attributes {:data-id id :data-operation "toggle-like"}
               :icon icon-unlike
               :text unlike-button-text}]]
            [:li
             {:class [margin-tailwind-class "hint--left"]
              :aria-label "Like this quote"}
             [btn/button
              {:data-attributes {:data-id id :data-operation "toggle-like"}
               :icon icon-like
               :text like-button-text}]])])
       [:li {:class [margin-tailwind-class "hint--left"]}
        [btn/share-on-twitter {:encoded-uri encoded-uri}]]
       [:li {:class [margin-tailwind-class "hint--left"]}
        [btn/share-on-whatsapp {:encoded-uri encoded-uri}]]
       [:li {:class [margin-tailwind-class "hint--left"]}
        [btn/share-on-telegram {:encoded-uri encoded-uri}]]
       [:li {:class [margin-tailwind-class "hint--left"]}
        [btn/share-on-reddit {:encoded-uri encoded-uri}]]
       [:li {:class [margin-tailwind-class "hint--right"] :aria-label "Share"}
        [btn/button
         {:data-attributes {:data-id id :data-operation "share"}
          :icon icon-share
          :text "Share"}]]]]]))
