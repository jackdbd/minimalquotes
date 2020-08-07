(ns minimalquotes.components.tags
  "Tags are an appropriate use-case for the cluster layout.
   https://absolutely.every-layout.dev/layouts/cluster/"
  (:require [minimalquotes.components.buttons :as btn]
            [minimalquotes.components.icons :refer [icon-tag]]
            [minimalquotes.routes :refer [path-for]]
            [minimalquotes.state :as state]
            [minimalquotes.utils :refer [k->str]]))

; (def debug-css "bg-green-300")
(def debug-css "")

(defn tag
  [{:keys [color description name]}]
  [btn/button
   {:color color
    :data-attributes {:data-tag name :data-tooltip description}
    :icon icon-tag
    :text name}])

(defn make-m->li
  [margin-tailwind-class]
  (fn m->li [[k-id props]]
    (let [doc-id (k->str k-id)
          tag-name (:name props)]
      ^{:key doc-id}
      [:li {:class [margin-tailwind-class]}
       [:a {:href (path-for :minimalquotes.routes/quotes {:tag tag-name})}
        [tag props]]])))

(defn make-on-click
  [on-click-tag]
  (if on-click-tag
    (fn [^js e]
      (let [tag-name (.. e -target -dataset -tag)]
        (when tag-name (on-click-tag tag-name))))
    nil))

; TODO: is on-click-tag used anywhere?
(defn tags
  [{:keys [entries margin-tailwind-class on-click-tag]
    :or {margin-tailwind-class "m-1"}}]
  (let [m->li (make-m->li margin-tailwind-class)
        on-click (make-on-click on-click-tag)]
    [:div {:class ["tags" "overflow-hidden" "p-2" debug-css]}
     [:ul
      {:class ["flex" "flex-wrap" (str "-" margin-tailwind-class)]
       :on-click on-click} (map m->li entries)]]))

(defn tags-container [] (let [entries @state/tags] [tags {:entries entries}]))
