(ns pain-app-mobile.views.paramters
  (:require [pain-app-mobile.events :as events]
            [pain-app-mobile.styles :as styles]
            [pain-app-mobile.subs :as subs]
            [pain-app-mobile.utils :as utils]
            [pain-app-mobile.views.components :as components :refer [outlined-button]]
            [re-frame.core :as re-frame]
            [spade.core :refer [defclass]]
            [pain-app-mobile.views.painviscontainer :refer [pain-vis-container]]))

(defn event->int [event]
  (-> event .-target .-value js/parseInt))

(defn event->number [event]
  (-> event .-target .-value js/parseFloat))

(defn- add-area [{:keys [id position]} event]
  (re-frame/dispatch [:update-areas {:id id
                                     :radius (event->int event)
                                     :position position}]))

(declare thumbnail-image)
(defclass thumbnail-image [bg-image-location]
  {:background-image (str "url(" bg-image-location ")")
   :margin-right "8px"
   :height "20px"
   :width "20px"
   :border-radius "50%"
   :background-size :contain})

(declare trash-button-container)
(defclass trash-button-container []
  {:margin-left "16px"
   :display :grid
   :place-items :center})

(declare trash-button)
(defclass trash-button []
  {:height "20px"
   :width "20px"
   :background-image "url(./assets/icons/Bereich-Trashcan.png)"
   :background-size :contain
   :background-repeat :no-repeat
   :cursor :pointer})

(defn- abstract-parameters [back-fn next-fn content]
  [:div.content
   [:div.row {:style {:padding "16px" :position :sticky :top "80px" :height "40px"}}
    [outlined-button ["Zurück"] back-fn] [outlined-button ["Weiter"] next-fn]]
   (let [asset-location @(re-frame/subscribe [::subs/area-asset])
         parameters @(re-frame/subscribe [::subs/parameters])]
     [pain-vis-container {:asset-location asset-location :parameters parameters}])
   content])

(defn- title [icon-location title]
  [:div.row
   [:div.row
    [:div {:class (thumbnail-image icon-location)}]
    [:h2 title]]
   [:button {:class (styles/text-button) :style {:font-size "1rem"}} "ⓘ"]])

(defn painareas []
  (abstract-parameters
   (utils/goto-fn :areapicker-general)
   (utils/goto-fn :painform)
   (let [areas @(re-frame/subscribe [::subs/painareas])]
     [:div.box
      (title "./assets/icons/headers/Schmerzbereich.png" "Schmerzpunkte")
      [:div.divider]
      [:ul
       (for [a areas] [:li {:key (:id a)}
                       [:label {:for (:id a)} "Punkt " (:id a)]
                       [:div.row
                        [components/slider 10 100 1 (:radius a) #(add-area a %)]
                        [:div {:class (trash-button-container)}
                         [:div {:class (trash-button) :on-click #(re-frame/dispatch [:remove-area-with-id (:id a)])}]]]])]
      (when (< (count areas) 7)
        [:div.center
         [:button {:class (styles/text-button)
                   :style {:font-size "2rem"}
                   :on-click #(re-frame/dispatch [:update-areas {:id (inc (apply max (conj (map :id areas) 0)))
                                                                 :position [0 0]
                                                                 :radius 20}])}
          "+"]])])))

(defn decorated-slider [id label min max step value on-change]
  [:li {:key id}
   [:label {:for id} label]
   [:div.row
    [components/slider min max step value on-change]]])

(defn simple-parameter-slider [id label min max step parse-fn]
  (let [param @(re-frame/subscribe [::subs/parameter id])]
    (decorated-slider id label min max step param
                      #(re-frame/dispatch [::events/set-param id (parse-fn %)]))))

(defn painform []
  (abstract-parameters
   (utils/goto-fn :pain-points)
   (utils/goto-fn :materialness)
   [:div.box
    (title "./assets/icons/headers/Form.png" "Form")
    [:div.divider]
    (simple-parameter-slider :wing-length "Strahlen" 0 1 0.01 event->number)
    (simple-parameter-slider :spikiness "Rund/Zackig" 0 1 0.01 event->number)
    (simple-parameter-slider :num-wings "Feinheit" 5 40 1 event->int)]))

(defn materialness []
  (abstract-parameters
   (utils/goto-fn :painform)
   (utils/goto-fn :paincolor)
   [:div.box
    (title "./assets/icons/headers/Materialitaet.png" "Materialität")
    [:div.divider]
    (simple-parameter-slider :sharpness "Schärfe" 0.05 0.95 0.01 event->number)
    (simple-parameter-slider :dissolve "Auflösung" 0 1 0.01 event->number)]))

(defclass circular-btn []
  {:height "32px"
   :width "32px"
   :border-radius "50%"
   :cursor :pointer})

(defn color-radio-button [css-color selected? on-click]
  [:button {:class (circular-btn)
            :style {:background-color css-color
                    :border "2px solid white"
                    :outline (if selected? "2px solid black" "2px solid white")}
            :on-click on-click}])

(defn colorshift-radio-button [css-color selected? on-click]
  [:button {:class (circular-btn)
            :style {:background-color "white"
                    :border (str "5px solid " css-color)
                    :outline (if selected? "2px solid black" "2px solid white")}
            :on-click on-click}])

(defn- set-color-fn [new-color]
  #(re-frame/dispatch [::events/set-param :color new-color]))

(defn- set-colorshift-fn [new-color]
  #(re-frame/dispatch [::events/set-param :outerColor new-color]))

(defn color []
  (abstract-parameters
   (utils/goto-fn :painform)
   (utils/goto-fn :painanimation)
   [:div.box
    (title "./assets/icons/headers/Farbe.png" "Farbe")
    [:div.divider]
    [:li {:key :color}
     [:label {:for :color} "Farbe"]
     (let [selected @(re-frame/subscribe [::subs/parameter :color])]
       [:div.row
        (color-radio-button "rgb(250, 27, 27)" (= :red selected) (set-color-fn :red))
        (color-radio-button "rgb(13, 36, 239)" (= :blue selected) (set-color-fn :blue))
        (color-radio-button "rgb(250, 187, 27)" (= :yellow selected) (set-color-fn :yellow))])]
    (simple-parameter-slider :lightness "Helligkeit" 0.3 0.8 0.01 event->number)
    [:li {:key :outerColor}
     [:label {:for :outerColor} "Verlauf zu"]
     (let [selected @(re-frame/subscribe [::subs/parameter :outerColor])]
       [:div.row
        (colorshift-radio-button "rgb(250, 187, 27)" (= :yellow selected) (set-colorshift-fn :yellow))
        (colorshift-radio-button "rgb(250, 142, 27)" (= :orange selected) (set-colorshift-fn :orange))
        (colorshift-radio-button "rgb(196, 196, 196)" (= :transparency selected) (set-colorshift-fn :transparency))])]
    (simple-parameter-slider :colorShift "Verlauf zu" 0 1 0.01 event->number)]))

(defn image-radio-button [css-bg-image on-click]
  [:button {:class (circular-btn)
            :style {:background-image css-bg-image
                    :background-size :contain
                    :border :none}
            :on-click on-click}])

(defn- set-animation-behavior-fn [new-behavior]
  #(re-frame/dispatch [::events/set-param :animation-behavior new-behavior]))

(defn- set-animation-parameter-fn [new-param]
  #(re-frame/dispatch [::events/set-param :animation-parameter new-param]))

(defn simple-animation-behavior-picker [file-prefix id selected]
  (image-radio-button (str "url(./assets/icons/" file-prefix "_" (if (= id selected) "selected" "clear")  ".png)")
                      (set-animation-behavior-fn id)))

(defn simple-animation-parameter-picker [file-prefix id selected]
  (image-radio-button (str "url(./assets/icons/Animationsparameter/" file-prefix "-" (if (= id selected) "selected" "clear")  ".png)")
                      (set-animation-parameter-fn id)))
(defn animation []
  (abstract-parameters
   (utils/goto-fn :paincolor)
   (utils/goto-fn :export)
   [:div.box
    (title "./assets/icons/headers/Animation.png" "Animation")
    [:div.divider]
    [:li {:key :animation-behavior}
     [:label {:for :animation-behavior} "Verhalten"]
     (let [selected @(re-frame/subscribe [::subs/parameter :animation-behavior])]
       [:div.row
        (simple-animation-behavior-picker "aus" :off selected)
        (simple-animation-behavior-picker "Aufbauend" :linear-in selected)
        (simple-animation-behavior-picker "Abbauend" :linear-out selected)
        (simple-animation-behavior-picker "Gleichmaessig" :soft selected)])]
    [:div.divider]
    [:li {:key :animation-parameter}
     [:label {:for :animation-parameter} "Parameter"]
     (let [selected @(re-frame/subscribe [::subs/parameter :animation-parameter])]
       [:div.row
        (simple-animation-parameter-picker "size" :radius selected)
        (simple-animation-parameter-picker "spikes" :wing-length selected)
        (simple-animation-parameter-picker "amount" :dissolve selected)
        (simple-animation-parameter-picker "gradient" :sharpness selected)])]
    [:div.divider]
    (simple-parameter-slider :animation-frequency-hz "Frequenz" 0.4 5 0.01 event->number)
    (simple-parameter-slider :animation-amplitude "Volumen" 0.1 1 0.01 event->number)]))

(comment)