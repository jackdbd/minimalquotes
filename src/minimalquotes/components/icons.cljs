(ns minimalquotes.components.icons
  "SVG icons.
   https://github.com/adamwathan/entypo-optimized/tree/master/dist/icons")

(def default-css-classes ["fill-current" "w-4" "h-4"])
(def view-box "0 0 20 20")
(def xmlns "http://www.w3.org/2000/svg")

(defn icon-cross
  []
  [:svg {:class default-css-classes
         :viewBox view-box
         :xmlns xmlns}
   [:path {:d "M14.348,14.849c-0.469,0.469-1.229,0.469-1.697,0L10,11.819l-2.651,
               3.029c-0.469,0.469-1.229,0.469-1.697,0 c-0.469-0.469-0.469-1.229,
               0-1.697l2.758-3.15L5.651,6.849c-0.469-0.469-0.469-1.228,
               0-1.697c0.469-0.469,1.228-0.469,1.697,0 L10,
               8.183l2.651-3.031c0.469-0.469,1.228-0.469,1.697,0c0.469,0.469,
               0.469,1.229,0,1.697l-2.758,3.152l2.758,3.15 C14.817,13.62,14.817,
               14.38,14.348,14.849z"}]])

(defn icon-edit
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/edit.svg"
  [{:keys [css-classes data-attributes]}]
  (let [svg-props {:class css-classes
                   :viewBox view-box
                   :xmlns xmlns}
        path-props {:d "M17.561 2.439c-1.442-1.443-2.525-1.227-2.525-1.227L8.984
                        7.264 2.21 14.037 1.2 18.799l4.763-1.01 6.774-6.771
                        6.052-6.052c-.001 0 .216-1.083-1.228-2.527zM5.68
                        17.217l-1.624.35a3.71 3.71 0 0 0-.69-.932 3.742 3.742 0
                        0 0-.932-.691l.35-1.623.47-.469s.883.018 1.881
                        1.016c.997.996 1.016 1.881 1.016 1.881l-.471.468z"}]
    [:svg (merge svg-props data-attributes)
     [:path (merge path-props data-attributes)]]))

(defn icon-like
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/heart.svg"
  [{:keys [css-classes data-attributes]}]
  (let [svg-props {:class css-classes
                   :viewBox view-box
                   :xmlns xmlns}
        path-props {:d "M17.19 4.155c-1.672-1.534-4.383-1.534-6.055 0L10 5.197
                        8.864 4.155c-1.672-1.534-4.382-1.534-6.054 0-1.881
                        1.727-1.881 4.52 0 6.246L10 17l7.19-6.599c1.88-1.726
                        1.88-4.52 0-6.246z"}]
    [:svg (merge svg-props data-attributes)
     [:path (merge path-props data-attributes)]]))

(defn icon-login
  [{:keys [css-classes data-attributes]}]
  (let [svg-props {:class css-classes
                   :viewBox view-box
                   :xmlns xmlns}
        path-props {:d "M14 10L8 5v3H1v4h7v3l6-5zm3 7H9v2h8c1.1 0 2-.9
                        2-2V3c0-1.1-.9-2-2-2H9v2h8v14z"}]
    [:svg (merge svg-props data-attributes)
     [:path (merge path-props data-attributes)]]))

(defn icon-minus
  [{:keys [css-classes data-attributes]}]
  (let [svg-props {:class css-classes
                   :viewBox view-box
                   :xmlns xmlns}
        path-props {:d "M16 10c0 .553-.048 1-.601 1H4.601C4.049 11 4 10.553 4
                        10c0-.553.049-1 .601-1H15.4c.552 0 .6.447.6 1z"}]
    (prn "props" data-attributes css-classes)
    [:svg (merge svg-props data-attributes)
     [:path (merge path-props data-attributes)]]))

(defn icon-plus
  [{:keys [css-classes data-attributes]}]
  (let [svg-props {:class css-classes
                   :viewBox view-box
                   :xmlns xmlns}
        path-props {:d "M16,10c0,0.553-0.048,1-0.601,1H11v4.399C11,15.951,
                        10.553,16,10,16c-0.553,0-1-0.049-1-0.601V11H4.601 
                        C4.049,11,4,10.553,4,10c0-0.553,0.049-1,
                        0.601-1H9V4.601C9,4.048,9.447,4,10,4c0.553,0,1,0.048,1,
                        0.601V9h4.399 C15.952,9,16,9.447,16,10z"}]
    [:svg (merge svg-props data-attributes)
     [:path (merge path-props data-attributes)]]))

(defn icon-share
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/share.svg"
  [{:keys [css-classes data-attributes]}]
  (let [svg-props {:class css-classes
                   :viewBox view-box
                   :xmlns xmlns}
        path-props {:d "M15 13.442c-.633 0-1.204.246-1.637.642l-5.938-3.463c.046-.188.075-.384.075-.584s-.029-.395-.075-.583L13.3
                        6.025A2.48 2.48 0 0 0 15 6.7c1.379 0 2.5-1.121 2.5-2.5S16.379
                        1.7 15 1.7s-2.5 1.121-2.5 2.5c0 .2.029.396.075.583L6.7 8.212A2.485
                        2.485 0 0 0 5 7.537c-1.379 0-2.5 1.121-2.5 2.5s1.121
                        2.5 2.5 2.5a2.48 2.48 0 0 0 1.7-.675l5.938 3.463a2.339
                        2.339 0 0 0-.067.546A2.428 2.428 0 1 0 15 13.442z"}]
    [:svg (merge svg-props data-attributes)
     [:path (merge path-props data-attributes)]]))

(defn icon-shopping-cart
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/shopping-cart.svg"
  [{:keys [css-classes]}]
  [:svg {:class css-classes
         :viewBox view-box
         :xmlns xmlns}
   [:path {:d "M13 17a2 2 0 1 0 3.999.001A2 2 0 0 0 13 17zM3 17a2 2 0 1 0 4 0 2 2 0 0 0-4 0zm3.547-4.828L17.615 9.01A.564.564 0 0 0 18 8.5V3H4V1.4c0-.22-.181-.4-.399-.4H.399A.401.401 0 0 0 0 1.4V3h2l1.91 8.957.09.943v1.649c0 .219.18.4.4.4h13.2c.22 0 .4-.182.4-.4V13H6.752c-1.15 0-1.174-.551-.205-.828z"}]])

(defn icon-trash []
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/trash.svg"
  [:svg {:class default-css-classes
         :viewBox view-box
         :xmlns xmlns}
   [:path {:d "M3.389,7.113L4.49,18.021C4.551,18.482,6.777,19.998,10,
               20c3.225-0.002,5.451-1.518,5.511-1.979l1.102-10.908 C14.929,
               8.055,12.412,8.5,10,8.5C7.59,8.5,5.072,8.055,3.389,
               7.113z M13.168,1.51l-0.859-0.951C11.977,0.086,11.617,0,10.916,
               0 H9.085c-0.7,0-1.061,0.086-1.392,0.559L6.834,1.51C4.264,1.959,
               2.4,3.15,2.4,4.029v0.17C2.4,5.746,5.803,7,10,7 c4.198,0,
               7.601-1.254,7.601-2.801v-0.17C17.601,3.15,15.738,1.959,13.168,
               1.51z M12.07,4.34L11,3H9L7.932,4.34h-1.7 c0,0,1.862-2.221,
               2.111-2.522C8.533,1.588,8.727,1.5,8.979,1.5h2.043c0.253,0,0.447,
               0.088,0.637,0.318 c0.248,0.301,2.111,2.522,2.111,2.522H12.07z"}]])
