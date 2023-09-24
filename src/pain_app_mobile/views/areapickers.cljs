(ns pain-app-mobile.views.areapickers
  (:require [pain-app-mobile.styles :as styles]
            [spade.core :refer [defclass]]
            [pain-app-mobile.views.components :as components]
            [re-frame.core :as re-frame]))

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

(defn page-id-setter [new-page-id]
  (fn []
    (re-frame/dispatch [:set-page-id new-page-id])))

(defn- areapicker-ui [title items prev-page-id]
  [:div.main
   [components/centered-header "1/6"]
   [:div {:class (styles/text-container)}
    [:p title]
    (for [{:keys [label on-click]} items]
      [:button {:key label :on-click on-click :class (areapicker-text-button)} label])
    [:div {:style {:margin-top "2rem"}} [components/outlined-button ["Zurück"] (page-id-setter prev-page-id)]]]])

(defn start-with-asset [asset]
  (fn []
    (re-frame/dispatch [:set-area-asset asset])
    (re-frame/dispatch [:set-page-id :pain-points])))

(defn whole-body []
  [areapicker-ui "Welche Ansicht vom ganzen Körper?" [{:label "Frontal", :on-click (start-with-asset "./assets/whole/front.png")}
                                                      {:label "Von Hinten", :on-click (start-with-asset "./assets/whole/back.png")}
                                                      {:label "Rechte Seite", :on-click (start-with-asset "./assets/whole/left.png")}
                                                      {:label "Linke Seite", :on-click (start-with-asset "./assets/whole/right.png")}]
   :areapicker-general])

(defn part-of-body []
  [areapicker-ui "Welcher Bereich?" [{:label "Kopf und Nacken" :on-click (page-id-setter :areapicker-neck)}
                                     {:label "Arme und Hände" :on-click (page-id-setter :areapicker-arms)}
                                     {:label "Torso" :on-click (page-id-setter :areapicker-torso)}
                                     {:label "Unterkörper" :on-click (page-id-setter :areapicker-subbody)}
                                     {:label "Beine und Füße" :on-click (page-id-setter :areapicker-legs)}]
   :areapicker-general])

(defn neck []
  [areapicker-ui "Wo am Kopf und Nacken?" [{:label "Frontal" :on-click (start-with-asset "./assets/parts/head/head-front.png")}
                                           {:label "Hinterkopf und Nacken" :on-click (start-with-asset "./assets/parts/head/head-back.png")}
                                           {:label "Rechte Seite" :on-click (start-with-asset "./assets/parts/head/head-right.png")}
                                           {:label "Linke Seite" :on-click (start-with-asset "./assets/parts/head/head-left.png")}]
   :areapicker-part-body])

(defn arms []
  [areapicker-ui "Wo an Armen und Händen?" [{:label "Rechter Arm" :on-click (start-with-asset "./assets/parts/arms/arm-right.png")}
                                            {:label "Rechte Hand" :on-click (start-with-asset "./assets/parts/arms/hand-right.png")}
                                            {:label "Linker Arm" :on-click (start-with-asset "./assets/parts/arms/arm-left.png")}
                                            {:label "Linke Hand" :on-click (start-with-asset "./assets/parts/arms/hand-left.png")}]
   :areapicker-part-body])

(defn torso []
  [areapicker-ui "Wo am Torso?" [{:label "Frontal" :on-click (start-with-asset "./assets/parts/upper/front.png")}
                                 {:label "Rücken" :on-click (start-with-asset "./assets/parts/upper/back.png")}
                                 {:label "Rechte Seite" :on-click (start-with-asset "./assets/parts/upper/right.png")}
                                 {:label "Linke Seite" :on-click (start-with-asset "./assets/parts/upper/left.png")}]
   :areapicker-part-body])

(defn subbody []
  [areapicker-ui "Wo am Unterkörper?" [{:label "Frontal" :on-click (start-with-asset "./assets/parts/lower/front.png")}
                                       {:label "Gesäß" :on-click (start-with-asset "./assets/parts/lower/back.png")}]
   :areapicker-part-body])

(defn legs []
  [areapicker-ui "Wo an Beinen und Füßen?" [{:label "Beine Vorne" :on-click (start-with-asset "./assets/parts/legs/legs-front.png")}
                                            {:label "Beine Hinten" :on-click (start-with-asset "./assets/parts/legs/legs-back.png")}
                                            {:label "Rechter Fuß" :on-click (start-with-asset "./assets/parts/legs/foot-right.png")}
                                            {:label "Linker Fuß" :on-click (start-with-asset "./assets/parts/legs/foot-left.png")}]
   :areapicker-part-body])

(defn overview []
  [areapicker-ui "Wo schmerzt es?" [{:label "Ganzer Körper" :on-click (page-id-setter :areapicker-whole-body)}
                                    {:label "Bereich" :on-click (page-id-setter :areapicker-part-body)}]
   :start])