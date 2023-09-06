(ns pain-app-mobile.views.components
  (:require [spade.core :refer [defclass]]
            [pain-app-mobile.styles :as styles]))

(declare outlined-button-style)
(defclass outlined-button-style []
  {:background-color "unset"
   :border "solid 2px"
   :border-radius "8px"
   :width "fit-content"
   :padding "0.6em"
   :font-weight "bold"
   :font-size "medium"
   :cursor "pointer"
   :display "flex"})


(defn outlined-button
  ([content on-click]
   (outlined-button content on-click styles/light-grey {}))
  ([content on-click color attr-override]
   (into [:button (merge {:class (outlined-button-style) :on-click on-click :style {:color color}} attr-override)] content)))


(defn slider
  ([min max step value on-change]
   (slider min max step value on-change false))
  ([min max step value on-change disabled]
   [:input {:type :range :min min, :max max, :step step, :value value :on-change on-change :disabled disabled}]))