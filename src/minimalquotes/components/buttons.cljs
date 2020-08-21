(ns minimalquotes.components.buttons
  (:require
    [minimalquotes.components.icons :refer
     [icon-paper-plane icon-phone icon-share icon-twitter]]))

(defn button
  "Generic button.
  Tailwind CSS gotcha: avoid using string concatenation to create class names.
  https://tailwindcss.com/docs/controlling-file-size/#writing-purgeable-html"
  [{:keys [border-color data-attributes direction hover-bg-color icon on-click
           text text-color]
    :or {border-color "border-color-500" data-attributes {} direction "ltr"
         hover-bg-color "hover:bg-blue-700" text-color "text-blue-500"}}]
  (let [margin-tailwind-class (if (= "ltr" direction) "ml-1" "mr-1")
        button-props {:class ["font-bold" "rounded" "bg-transparent" "px-2"
                              "py-2" text-color
                              (when (:data-tooltip data-attributes) "tooltip")
                              "border" border-color hover-bg-color "hover:text-white"
                              "hover:border-transparent"]
                      :on-click on-click
                      :style {:direction direction}
                      :type "button"}
        icon-props {:css-classes ["w-4" "h-4" "fill-current"
                                  margin-tailwind-class]}]
    [:button (merge button-props data-attributes)
     [:div (merge {:class ["inline-flex" "items-center"]} data-attributes)
      [:span data-attributes text]
      (when icon
        [icon (merge icon-props {:data-attributes data-attributes})])]]))

(defn submit
  [{:keys [bg-color disabled hover-bg-color text]
    :or {bg-color "bg-blue-500" disabled false
         hover-bg-color "hover:bg-blue-700" text "Submit"}}]
  [:button
   {:class ["font-bold" "rounded" "text-white" "px-2" "py-2"
            bg-color hover-bg-color
            "focus:outline-none" "focus:shadow-outline"]
    :disabled disabled
    :type "submit"} text])

(defn share-on-twitter
  [{:keys [encoded-uri]}]
  [:a {:href (str "https://twitter.com/intent/tweet"
                  "?text=" encoded-uri
                  "&url=" (.. js/window -location -href))
       :rel "noopener" :target "_blank"}
   [button {:icon icon-twitter :text "Tweet"}]])

(defn share-on-whatsapp
  [{:keys [encoded-uri]}]
  [:a {:data-action "share/whatsapp/share"
       :href (str "https://web.whatsapp.com/send"
                  ; "?phone=XX1234567890"
                  "?text=" encoded-uri)
       :rel "noopener" :target "_blank"}
   [button {:icon icon-phone :text "WhatsApp"}]])

(defn share-on-telegram
  [{:keys [encoded-uri]}]
  [:a {:href (str "https://t.me/share/url"
                  "?text=" encoded-uri
                  "&url=" (.. js/window -location -href))
       :rel "noopener" :target "_blank"}
   [button {:icon icon-paper-plane :text "Telegram"}]])

(defn share-on-reddit
  [{:keys [encoded-uri]}]
  [:a {:href (str "https://www.reddit.com/submit"
                  "?url=" encoded-uri)
       :rel "noopener" :target "_blank"}
   [button {:icon icon-share :text "Reddit"}]])
