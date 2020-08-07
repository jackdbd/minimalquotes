(ns minimalquotes.cards
  "This namespace contains devcards and tests, and is the entrypoint for both
  'yarn cards' and 'yarn test'."
  (:require
    [cljsjs.react]
    [cljsjs.react.dom]
    ; devcards needs cljsjs.react and cljsjs.react.dom to be imported
    ; separately for shadow-cljs to add shims.
    [devcards.core :refer [start-devcard-ui!]]
    [minimalquotes.components.actions-cards]
    [minimalquotes.components.buttons-cards]
    [minimalquotes.components.error-boundary-cards]
    [minimalquotes.components.footer-cards]
    [minimalquotes.components.forms-cards]
    [minimalquotes.components.header-cards]
    [minimalquotes.components.modal-cards]
    [minimalquotes.components.quote-cards]
    [minimalquotes.components.quotes-cards]
    [minimalquotes.components.tags-cards]
    ["jsdom-global" :as jsdom-global]))

; Set jsdom to mock a dom environment for node testing.
(jsdom-global)

(defn ^:export main
  "Start the devcards UI."
  []
  ; Add a special class to the body to signal we're in devcards mode.
  ; We want to mostly use the same styles as the app, but might need to make
  ; some exceptions.
  (js/document.body.classList.add "using-devcards")
  (start-devcard-ui!))
