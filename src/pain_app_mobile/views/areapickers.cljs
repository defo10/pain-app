(ns pain-app-mobile.views.areapickers
  (:require [clojure.spec.alpha :as s]
            [pain-app-mobile.styles :as styles]
            [spade.core :refer [defclass]]
            [pain-app-mobile.views.components :as components]
            [re-frame.core :as re-frame]))

(def area-tree {:title "Wo schmerzt es?",
                :displayName "",
                :children
                [{:title "Welche Ansicht vom ganzen Körper?",
                  :displayName "Ganzer Körper",
                  :children
                  [{:displayName "Frontal", :assetLocation "wholeFront"}
                   {:displayName "Von Hinten", :assetLocation "wholeBack"}
                   {:displayName "Rechte Seite", :assetLocation "wholeRight"}
                   {:displayName "Linke Seite", :assetLocation "wholeLeft"}]}
                 {:title "Welcher Bereich?",
                  :displayName "Bereich",
                  :children
                  [{:title "Wo am Kopf und Nacken?",
                    :displayName "Kopf und Nacken",
                    :children
                    [{:displayName "Frontal", :assetLocation "partsHeadFront"}
                     {:displayName "Hinterkopf und Nacken", :assetLocation "partsHeadBack"}
                     {:displayName "Rechte Seite", :assetLocation "partsHeadRight"}
                     {:displayName "Linke Seite", :assetLocation "partsHeadLeft"}]}
                   {:title "Wo an Armen und Händen?",
                    :displayName "Arme und Hände",
                    :children
                    [{:displayName "Rechter Arm", :assetLocation "partsArmsRight"}
                     {:displayName "Rechte Hand", :assetLocation "partsArmsHandRight"}
                     {:displayName "Linker Arm", :assetLocation "partsArmsLeft"}
                     {:displayName "Linke Hand", :assetLocation "partsArmsHandLeft"}]}
                   {:title "Wo am Torso?",
                    :displayName "Torso",
                    :children
                    [{:displayName "Frontal", :assetLocation "partsUpperFront"}
                     {:displayName "Rücken", :assetLocation "partsUpperBack"}
                     {:displayName "Rechte Seite", :assetLocation "partsUpperRight"}
                     {:displayName "Linke Seite", :assetLocation "partsUpperLeft"}]}
                   {:title "Wo am Unterkörper?",
                    :displayName "Unterkörper",
                    :children
                    [{:displayName "Frontal", :assetLocation "partsLowerFront"}
                     {:displayName "Gesäß", :assetLocation "partsLowerBack"}]}
                   {:title "Wo an Beinen und Füßen?",
                    :displayName "Beine und Füße",
                    :children
                    [{:displayName "Beine Vorne", :assetLocation "partsLegsFront"}
                     {:displayName "Beine Hinten", :assetLocation "partsLegsBack"}
                     {:displayName "Rechter Fuß", :assetLocation "partsLegsFootRight"}
                     {:displayName "Linker Fuß", :assetLocation "partsLegsFootLeft"}]}]}]})

(declare areapicker-text-button)
(defclass areapicker-text-button []
  {:background-color "unset"
   :border "none"
   :border-bottom "0.5px solid black"
   :padding "0.5em 0"
   :text-align "left"
   :font-size "1.3em"
   :font-weight 600
   :color styles/brown})

(s/def ::on-click (s/fspec))
(s/def ::label string?)
(s/def ::item (s/keys :req [::label ::on-click]))

(s/fdef areapicker-ui :args (s/cat :title string? :items (s/coll-of ::item)))
(defn- areapicker-ui [title items]
  [:div {:class (styles/text-container)}
   [:p title]
   (for [{:keys [label on-click]} items]
     [:button {:key label :on-click on-click :class (areapicker-text-button)} label])
   [components/outlined-button ["Zurück"] #()]])

(defn start-with-asset [asset]
  (fn []
    (re-frame/dispatch [:set-area-asset asset])
    (re-frame/dispatch [:set-page-id :pain-points])))

(defn page-id-setter [new-page-id]
  (fn []
    (re-frame/dispatch [:set-page-id new-page-id])))

(defn whole-body []
  [areapicker-ui "Welche Ansicht vom ganzen Körper?" [{:label "Frontal", :on-click (start-with-asset "wholeFront")}
                                                      {:label "Von Hinten", :on-click (start-with-asset "wholeBack")}
                                                      {:label "Rechte Seite", :on-click (start-with-asset "wholeRight")}
                                                      {:label "Linke Seite", :on-click (start-with-asset "wholeLeft")}]])

(defn part-of-body []
  [areapicker-ui "Welcher Bereich?" [{:label "Kopf und Nacken" :on-click (page-id-setter :areapicker-neck)}
                                     {:label "Arme und Hände" :on-click (page-id-setter :areapicker-arms)}
                                     {:label "Torso" :on-click (page-id-setter :areapicker-torso)}
                                     {:label "Unterkörper" :on-click (page-id-setter :areapicker-subbody)}
                                     {:label "Beine und Füße" :on-click (page-id-setter :areapicker-legs)}]])

(defn neck []
  [areapicker-ui "Wo am Kopf und Nacken?" [{:label "Frontal" :on-click (start-with-asset "partsHeadFront")}
                                           {:label "Hinterkopf und Nacken" :on-click (start-with-asset "partsHeadBack")}
                                           {:label "Rechte Seite" :on-click (start-with-asset "partsHeadRight")}
                                           {:label "Linke Seite" :on-click (start-with-asset "partsHeadLeft")}]])

(defn arms []
  [areapicker-ui "Wo an Armen und Händen?" [{:label "Rechter Arm" :on-click (start-with-asset "parsArmsRight")}
                                            {:label "Rechte Hand" :on-click (start-with-asset "partsArmsHandRight")}
                                            {:label "Linker Arm" :on-click (start-with-asset "partsArmsLeft")}
                                            {:label "Linke Hand" :on-click (start-with-asset "partsArmsHandLeft")}]])

(defn torso []
  [areapicker-ui "Wo am Torso?" [{:label "Frontal" :on-click (start-with-asset "partsUpperFront")}
                                 {:label "Rücken" :on-click (start-with-asset "partsUpperBack")}
                                 {:label "Rechte Seite" :on-click (start-with-asset "partsUpperRight")}
                                 {:label "Linke Seite" :on-click (start-with-asset "partsUpperLeft")}]])

(defn subbody []
  [areapicker-ui "Wo am Unterkörper?" [{:label "Frontal" :on-click (start-with-asset "partsLowerFront")}
                                       {:label "Gesäß" :on-click (start-with-asset "partsLowerBack")}]])

(defn legs []
  [areapicker-ui "Wo an Beinen und Füßen?" [{:label "Beine Vorne" :on-click (start-with-asset "partsLegsFront")}
                                            {:label "Beine Hinten" :on-click (start-with-asset "partsLegsBack")}
                                            {:label "Rechter Fuß" :on-click (start-with-asset "partsLegsFootRight")}
                                            {:label "Linker Fuß" :on-click (start-with-asset "partsLegsFootLeft")}]])

(defn overview []
  [areapicker-ui "Wo schmerzt es?" [{:label "Ganzer Körper" :on-click (page-id-setter :areapicker-whole-body)}
                                    {:label "Bereich" :on-click (page-id-setter :areapicker-part-body)}]])