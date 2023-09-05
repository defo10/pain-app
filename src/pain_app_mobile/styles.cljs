(ns pain-app-mobile.styles
  (:require
   [spade.core   :refer [defglobal defclass]]
   [garden.selectors]))

(declare defaults)
(declare level1)
(declare text-container)
(declare areapicker-text-button)

(def primary :#FD7F81)
(def secondary :#FFE9E9)
(def brown :#D1C2BA)
(def light-grey :#B1B1B1)
(def dark-grey :#5C5C5C)

(defglobal defaults
  [:body
   {:background-color    :#FFFFFF
    :margin 0
    :font-family "Helvetica, Verdana, Arial, sans-serif"}]
  [:h1
   {:color primary
    :margin "0 0 0.6em 0"}]
  [:h2
   {:color primary
    :font-size "1.3rem"
    :margin 0}]
  [:p
   {:color :black}]
  [:.row
   {:display :flex
    :justify-content :space-between
    :align-items :center
    :padding-bottom "0.5rem"}]
  [:.center
   {:display :grid
    :justify-content :center
    :align-items :center}]
  [:label
   {:white-space :nowrap
    :margin-right "16px"}]
  [:ul
   {:display :block
    :margin-block-start 0
    :margin-block-end 0
    :margin-inline-start 0
    :margin-inline-end 0
    :padding-inline-start 0}]
  [:li
   {:list-style-type :none}]
  [:.divider
   {:margin "12px 0"
    :width "100%"
    :border-top "1px solid #bbb"}]
  [:.box
   {:box-shadow "0.4px 0.4px 5px grey"
    :clip-path "inset(-15px 0px 0px 0px)"
    :display :flex
    :flex-direction :column
    :gap "0.5rem"
    :padding "32px"
    :background-color :white}])
; note: range inputs are directly styled within the index.html because I could not figure out how to 
; apply input[type="range"]::... selectors in garden/spade

(defclass text-container []
  {:display "flex" :flex-direction "column" :padding "2em"})

(declare text-button)
(defclass text-button []
  {:background-color :unset
   :border :none
   :color :black
   :cursor :pointer
   :font-size "1.5rem"})

(defclass level1
  []
  {:color :green})
