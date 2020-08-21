(ns minimalquotes.components.icons
  "SVG icons.
   https://github.com/adamwathan/entypo-optimized/tree/master/dist/icons")

(def default-css-classes ["fill-current" "w-4" "h-4"])
(def view-box "0 0 20 20")
(def xmlns "http://www.w3.org/2000/svg")

(defn icon
  [{:keys [css-classes d data-attributes]
    :or {css-classes default-css-classes}}]
  (let [svg-props {:class css-classes :viewBox view-box :xmlns xmlns}
        path-props {:d d}]
    [:svg (merge svg-props data-attributes)
     [:path (merge path-props data-attributes)]]))

(defn icon-backward
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/controller-fast-backward.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M17.959 4.571L10.756 9.52s-.279.201-.279.481.279.479.279.479l7.203 4.951c.572.38 1.041.099 1.041-.626V5.196c0-.727-.469-1.008-1.041-.625zm-9.076 0L1.68 9.52s-.279.201-.279.481.279.479.279.479l7.203 4.951c.572.381 1.041.1 1.041-.625v-9.61c0-.727-.469-1.008-1.041-.625z"}])

(defn icon-cross
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/cross.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M14.348,14.849c-0.469,0.469-1.229,0.469-1.697,0L10,11.819l-2.651,3.029c-0.469,0.469-1.229,0.469-1.697,0 c-0.469-0.469-0.469-1.229,0-1.697l2.758-3.15L5.651,6.849c-0.469-0.469-0.469-1.228,0-1.697c0.469-0.469,1.228-0.469,1.697,0 L10,8.183l2.651-3.031c0.469-0.469,1.228-0.469,1.697,0c0.469,0.469,0.469,1.229,0,1.697l-2.758,3.152l2.758,3.15 C14.817,13.62,14.817,14.38,14.348,14.849z"}])

(defn icon-edit
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/edit.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M17.561 2.439c-1.442-1.443-2.525-1.227-2.525-1.227L8.984 7.264 2.21 14.037 1.2 18.799l4.763-1.01 6.774-6.771 6.052-6.052c-.001 0 .216-1.083-1.228-2.527zM5.68 17.217l-1.624.35a3.71 3.71 0 0 0-.69-.932 3.742 3.742 0 0 0-.932-.691l.35-1.623.47-.469s.883.018 1.881 1.016c.997.996 1.016 1.881 1.016 1.881l-.471.468z"}])

(defn icon-forward
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/controller-fast-forward.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M9.244 9.52L2.041 4.571C1.469 4.188 1 4.469 1 5.196v9.609c0 .725.469 1.006 1.041.625l7.203-4.951s.279-.199.279-.478c0-.28-.279-.481-.279-.481zm9.356.481c0 .279-.279.478-.279.478l-7.203 4.951c-.572.381-1.041.1-1.041-.625V5.196c0-.727.469-1.008 1.041-.625L18.32 9.52s.28.201.28.481z"}])

(defn icon-like
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/heart-outlined.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M17.19 4.156c-1.672-1.535-4.383-1.535-6.055 0L10 5.197 8.864 4.156c-1.672-1.535-4.382-1.535-6.054 0-1.881 1.726-1.881 4.519 0 6.245L10 17l7.19-6.599c1.88-1.726 1.88-4.52 0-6.245zm-1.066 5.219L10 15.09 3.875 9.375c-.617-.567-.856-1.307-.856-2.094s.138-1.433.756-1.999c.545-.501 1.278-.777 2.063-.777.784 0 1.517.476 2.062.978L10 7.308l2.099-1.826c.546-.502 1.278-.978 2.063-.978s1.518.276 2.063.777c.618.566.755 1.212.755 1.999s-.238 1.528-.856 2.095z"}])

(defn icon-login
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/login.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M14 10L8 5v3H1v4h7v3l6-5zm3 7H9v2h8c1.1 0 2-.9 2-2V3c0-1.1-.9-2-2-2H9v2h8v14z"}])

(defn icon-minus
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/minus.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M16 10c0 .553-.048 1-.601 1H4.601C4.049 11 4 10.553 4 10c0-.553.049-1 .601-1H15.4c.552 0 .6.447.6 1z"}])

(defn icon-paper-plane
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/paper-plane.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M18.64 2.634L.984 8.856c-.284.1-.347.345-.01.479l3.796 1.521 2.25.901 10.984-8.066c.148-.108.318.095.211.211l-7.871 8.513v.002l-.452.503.599.322 4.982 2.682c.291.156.668.027.752-.334l2.906-12.525c.079-.343-.148-.552-.491-.431zM7 17.162c0 .246.139.315.331.141.251-.229 2.85-2.561 2.85-2.561L7 13.098v4.064z"}])

(defn icon-phone
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/phone.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M11.229 11.229c-1.583 1.582-3.417 3.096-4.142 2.371-1.037-1.037-1.677-1.941-3.965-.102-2.287 1.838-.53 3.064.475 4.068 1.16 1.16 5.484.062 9.758-4.211 4.273-4.274 5.368-8.598 4.207-9.758-1.005-1.006-2.225-2.762-4.063-.475-1.839 2.287-.936 2.927.103 3.965.722.725-.791 2.559-2.373 4.142z"}])

(defn icon-plus
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/plus.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M16,10c0,0.553-0.048,1-0.601,1H11v4.399C11,15.951,10.553,16,10,16c-0.553,0-1-0.049-1-0.601V11H4.601 C4.049,11,4,10.553,4,10c0-0.553,0.049-1,0.601-1H9V4.601C9,4.048,9.447,4,10,4c0.553,0,1,0.048,1,0.601V9h4.399 C15.952,9,16,9.447,16,10z"}])

(defn icon-price-tag
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/price-tag.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M19.388.405a.605.605 0 0 0-1.141.399c.929 2.67-.915 4.664-2.321 5.732l-.568-.814c-.191-.273-.618-.5-.95-.504l-3.188.014a2.162 2.162 0 0 0-1.097.338L.729 12.157a1.01 1.01 0 0 0-.247 1.404l4.269 6.108c.32.455.831.4 1.287.082l9.394-6.588c.27-.191.582-.603.692-.918l.998-3.145c.11-.314.043-.793-.148-1.066l-.346-.496c1.888-1.447 3.848-4.004 2.76-7.133zm-4.371 9.358a1.608 1.608 0 0 1-2.24-.396 1.614 1.614 0 0 1 .395-2.246 1.607 1.607 0 0 1 1.868.017c-.272.164-.459.26-.494.275a.606.606 0 0 0 .259 1.153c.086 0 .174-.02.257-.059.194-.092.402-.201.619-.33a1.615 1.615 0 0 1-.664 1.586z"}])

(defn icon-print
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/print.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M1.501 6h17c.57 0 .477-.608.193-.707C18.409 5.194 15.251 4 14.7 4H14V1H6v3h-.699c-.55 0-3.709 1.194-3.993 1.293-.284.099-.377.707.193.707zM19 7H1c-.55 0-1 .45-1 1v5c0 .551.45 1 1 1h2.283l-.882 5H17.6l-.883-5H19c.551 0 1-.449 1-1V8c0-.55-.449-1-1-1zM4.603 17l1.198-7.003H14.2L15.399 17H4.603z"}])

(defn icon-share
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/share.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M15 13.442c-.633 0-1.204.246-1.637.642l-5.938-3.463c.046-.188.075-.384.075-.584s-.029-.395-.075-.583L13.3 6.025A2.48 2.48 0 0 0 15 6.7c1.379 0 2.5-1.121 2.5-2.5S16.379 1.7 15 1.7s-2.5 1.121-2.5 2.5c0 .2.029.396.075.583L6.7 8.212A2.485 2.485 0 0 0 5 7.537c-1.379 0-2.5 1.121-2.5 2.5s1.121 2.5 2.5 2.5a2.48 2.48 0 0 0 1.7-.675l5.938 3.463a2.339 2.339 0 0 0-.067.546A2.428 2.428 0 1 0 15 13.442z"}])

(defn icon-shopping-cart
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/shopping-cart.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M13 17a2 2 0 1 0 3.999.001A2 2 0 0 0 13 17zM3 17a2 2 0 1 0 4 0 2 2 0 0 0-4 0zm3.547-4.828L17.615 9.01A.564.564 0 0 0 18 8.5V3H4V1.4c0-.22-.181-.4-.399-.4H.399A.401.401 0 0 0 0 1.4V3h2l1.91 8.957.09.943v1.649c0 .219.18.4.4.4h13.2c.22 0 .4-.182.4-.4V13H6.752c-1.15 0-1.174-.551-.205-.828z"}])

(defn icon-signal
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/signal.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M10 14a1.99 1.99 0 0 0-1.981 2c0 1.104.887 2 1.981 2a1.99 1.99 0 0 0 1.98-2c0-1.105-.886-2-1.98-2zm-4.2-2.242l1.4 1.414a3.933 3.933 0 0 1 5.601 0l1.399-1.414a5.898 5.898 0 0 0-8.4 0zM3 8.928l1.4 1.414a7.864 7.864 0 0 1 11.199 0L17 8.928a9.831 9.831 0 0 0-14 0zM.199 6.1l1.4 1.414a11.797 11.797 0 0 1 16.801 0L19.8 6.1a13.763 13.763 0 0 0-19.601 0z"}])

(defn icon-spinner
  "https://tailwind-animation-playground.vercel.app/"
  [{:keys [data-attributes size text-color] :or {size 32 text-color "text-indigo-400"}}]
  (let [svg-props {:class ["animate-spin" (str "w-" size) (str "h-" size) text-color]
                   :fill "none"
                   :viewBox "0 0 24 24"
                   :xmlns xmlns}
        circle-props {:class ["opacity-25"]
                      :cx 12 :cy 12 :r 10
                      :stroke "currentColor"
                      :stroke-width 4}
        path-props {:class ["opacity-75"]
                    :fill "currentColor"
                    :d "M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"}]
    [:svg (merge svg-props data-attributes)
     [:circle (merge circle-props data-attributes)]
     [:path (merge path-props data-attributes)]]))

(defn icon-tag
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/tag.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M18.662 5.521L5.237 19l.707-4.967-4.945.709L14.424 1.263c.391-.392 1.133-.308 1.412 0l2.826 2.839c.5.473.391 1.026 0 1.419z"}])

(defn icon-to-top
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/arrow-long-up.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d "M10 .75L15.5 6H12v13H8V6H4.5L10 .75z"}])

(defn icon-trash
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/trash.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M3.389,7.113L4.49,18.021C4.551,18.482,6.777,19.998,10,20c3.225-0.002,5.451-1.518,5.511-1.979l1.102-10.908 C14.929,8.055,12.412,8.5,10,8.5C7.59,8.5,5.072,8.055,3.389,7.113z M13.168,1.51l-0.859-0.951C11.977,0.086,11.617,0,10.916,0 H9.085c-0.7,0-1.061,0.086-1.392,0.559L6.834,1.51C4.264,1.959,2.4,3.15,2.4,4.029v0.17C2.4,5.746,5.803,7,10,7 c4.198,0,7.601-1.254,7.601-2.801v-0.17C17.601,3.15,15.738,1.959,13.168,1.51z M12.07,4.34L11,3H9L7.932,4.34h-1.7 c0,0,1.862-2.221,2.111-2.522C8.533,1.588,8.727,1.5,8.979,1.5h2.043c0.253,0,0.447,0.088,0.637,0.318 c0.248,0.301,2.111,2.522,2.111,2.522H12.07z"}])

(defn icon-twitter
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/twitter.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M17.316 6.246c.008.162.011.326.011.488 0 4.99-3.797 10.742-10.74 10.742-2.133 0-4.116-.625-5.787-1.697a7.577 7.577 0 0 0 5.588-1.562 3.779 3.779 0 0 1-3.526-2.621 3.858 3.858 0 0 0 1.705-.065 3.779 3.779 0 0 1-3.028-3.703v-.047a3.766 3.766 0 0 0 1.71.473 3.775 3.775 0 0 1-1.168-5.041 10.716 10.716 0 0 0 7.781 3.945 3.813 3.813 0 0 1-.097-.861 3.773 3.773 0 0 1 3.774-3.773 3.77 3.77 0 0 1 2.756 1.191 7.602 7.602 0 0 0 2.397-.916 3.789 3.789 0 0 1-1.66 2.088 7.55 7.55 0 0 0 2.168-.594 7.623 7.623 0 0 1-1.884 1.953z"}])

(defn icon-unlike
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/heart.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M17.19 4.155c-1.672-1.534-4.383-1.534-6.055 0L10 5.197 8.864 4.155c-1.672-1.534-4.382-1.534-6.054 0-1.881 1.727-1.881 4.52 0 6.246L10 17l7.19-6.599c1.88-1.726 1.88-4.52 0-6.246z"}])

(defn icon-upload-to-cloud
  "https://github.com/adamwathan/entypo-optimized/blob/master/dist/icons/upload-to-cloud.svg"
  [{:keys [css-classes data-attributes] :or {css-classes default-css-classes}}]
  [icon
   {:css-classes css-classes
    :data-attributes data-attributes
    :d
    "M15.213 6.639c-.276 0-.546.025-.809.068C13.748 4.562 11.716 3 9.309 3c-2.939 0-5.32 2.328-5.32 5.199 0 .256.02.508.057.756a3.567 3.567 0 0 0-.429-.027C1.619 8.928 0 10.51 0 12.463S1.619 16 3.617 16H8v-4H5.5L10 7l4.5 5H12v4h3.213C17.856 16 20 13.904 20 11.32c0-2.586-2.144-4.681-4.787-4.681z"}])
