(ns minimalquotes.components.quotes
  (:require [minimalquotes.state :as state]))

(defn m->li
  [[id {:keys [author text]}]]
  ^{:key id} [:li {:class ["quote"]} (str text " ― " author)])

(defn quotes
  [m-quotes]
  [:ul {:class ["quotes"]} (map m->li m-quotes)])

(defn quotes-container
  []
  [quotes @state/quotes])
