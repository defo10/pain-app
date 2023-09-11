(ns pain-app-mobile.views.paramters
  (:require [pain-app-mobile.events :as events]
            [pain-app-mobile.styles :as styles]
            [pain-app-mobile.subs :as subs]
            [pain-app-mobile.views.components :as components :refer [outlined-button]]
            [re-frame.core :as re-frame]
            [spade.core :refer [defclass]]
            ["react-joystick-component" :refer [Joystick]]
            [reagent.core :as reagent]
            [pain-app-mobile.db :as db]))

(defn event->int [event]
  (-> event .-target .-value js/parseInt))

(defn event->number [event]
  (-> event .-target .-value js/parseFloat))

(defn- add-area [{:keys [id position]} event]
  (re-frame/dispatch [:update-areas {:id id
                                     :radius (event->int event)
                                     :position position}]))

(defclass thumbnail-image [bg-image-location]
  {:background-image (str "url(" bg-image-location ")")
   :margin-right "8px"
   :height "28px"
   :width "28px"
   :border-radius "50%"
   :background-size :contain})

(defclass trash-button-container []
  {:margin-left "16px"
   :display :grid
   :place-items :center})

(defclass trash-button []
  {:height "20px"
   :width "20px"
   :background-image "url(./assets/icons/Bereich-Trashcan.png)"
   :background-size :contain
   :background-repeat :no-repeat
   :cursor :pointer})

(defclass download-button-icon-container []
  {:margin-left "16px"
   :display :grid
   :place-items :center})

(defclass download-button-icon []
  {:height "20px"
   :width "20px"
   :background-image "url(./assets/icons/Icon_Download.png)"
   :background-size :contain
   :background-repeat :no-repeat
   :cursor :pointer})

(defn title [icon-location title overlay-texts]
  [:div.row
   [:div.row
    [:div {:class (thumbnail-image icon-location)}]
    [:h2 title]]
   [:button {:class (styles/text-button)
             :style {:font-size "1.3rem"}
             :on-click #(re-frame/dispatch [::events/set-overlay overlay-texts])} "ⓘ"]])

(defn painareas []
  (let [areas @(re-frame/subscribe [::subs/painareas])]
    [:div.box
     (title "./assets/icons/headers/Schmerzbereich.png"
            "Schmerzpunkte"
            ["Legen Sie fest, ob die Schmerzfläche Offen oder geschlossen dargestellt werden soll."
             "Fügen Sie Punkte über das '+' hinzu, verschieben Sie diese an die richtige Stelle und ändern die die Größe über den Schieberegler."
             "Über das Mülltonnensymbol können Sie einen Punkt wieder entfernen."])
     [:div.divider]
     (for [a areas] [:li {:key (:id a)}
                     [:label {:for (:id a)} "Punkt " (:id a)]
                     [:div.row
                      [components/slider 10 100 1 (:radius a) #(add-area a %)]
                      [:div {:class (trash-button-container)}
                       [:div {:class (trash-button) :on-click #(re-frame/dispatch [:remove-area-with-id (:id a)])}]]]])
     (when (< (count areas) 7)
       [:div.center
        [:button {:class (styles/text-button)
                  :style {:font-size "2rem"}
                  :on-click #(re-frame/dispatch [:update-areas {:id (inc (apply max (conj (map :id areas) 0)))
                                                                :position [0 0]
                                                                :radius 20}])}
         "+"]])]))

(defn decorated-slider [id label min max step value on-change disabled]
  [:li {:key id}
   [:label {:for id :style {:color (if disabled "var(--disabled-grey)" "unset")}} label]
   [:div.row
    [components/slider min max step value on-change disabled]]])

(defn simple-parameter-slider [id label min max step parse-fn disabled]
  (let [param @(re-frame/subscribe [::subs/parameter id])]
    (decorated-slider id label min max step param
                      #(re-frame/dispatch [::events/set-param id (parse-fn %)])
                      disabled)))

(def parameters-next-routing
  {:pain-points :painform
   :painform :materialness
   :materialness :paincolor
   :paincolor :painanimation
   :painanimation :export})

(def parameters-back-routing
  {:pain-points :start
   :painform :pain-points
   :materialness :painform
   :paincolor :materialness
   :painanimation :paincolor
   :export :painanimation})

(defn navigation-row []
  (let [page-id @(re-frame/subscribe [::subs/page-id])]
    [:div.row {:style {:padding "16px 16px 0 16px"}}
     [outlined-button ["Zurück"] (if (= page-id :pain-points)
                                   #(js/location.reload)
                                   #(re-frame/dispatch [:set-page-id (page-id parameters-back-routing)]))]
     [:div {:style {:padding "1em" :font-weight "bold"}} (case page-id
                                                           :start "WILLKOMMEN"
                                                           :impressum "Impressum"
                                                           :pain-points "2/6"
                                                           :painform "3/6"
                                                           :materialness "4/6"
                                                           :paincolor "5/6"
                                                           :painanimation "6/6"
                                                           :export "ABSCHLUSS"
                                                           "1/6")]
     (if (= page-id :export)
       [:div {:style {:opacity 0}} "Weiter"]
       [outlined-button ["Weiter"] #(re-frame/dispatch [:set-page-id (page-id parameters-next-routing)]) "black" {}])]))

(defn painform []
  (let [wing-length-enabled (= @(re-frame/subscribe [::subs/parameter :wing-length]) 0)]
    [:div.box
     (title "./assets/icons/headers/Form.png" "Form" ["Wenn Ihr Schmerz eine Form hätte, welche würde das sein?"
                                                      "Stellen Sie als nächstes über die drei Werkzeuge die Form Ihres Schmerzes ein."
                                                      "Über die „Strahlen“ können Sie bestimmen, ob Ihr Schmerz eine geschlossene Form ist, oder spitz ausstrahlt."
                                                      "Über „Rund/Zackig“ können Sie einstellen, ob es sich um stumpfe oder abgerundete Strahlen handelt."
                                                      "Über „Feinheit“ können Sie die Anzahl der Strahlen einstellen."])
     [:div.divider]
     (simple-parameter-slider :wing-length "Strahlen" 0 1 0.01 event->number false)
     (simple-parameter-slider :spikiness "Rund/Zackig" 0 1 0.01 event->number wing-length-enabled)
     (simple-parameter-slider :num-wings "Feinheit" 5 40 1 event->int wing-length-enabled)]))

(defn materialness []
  [:div.box
   (title "./assets/icons/headers/Materialitaet.png" "Materialität" ["Welche Beschaffenheit hat Ihr Schmerz?"
                                                                     "Stellen Sie als nächstes über die zwei Werkzeuge die Materialität Ihres Schmerzes ein."
                                                                     "Über die „Schärfe“ können Sie bestimmen, ob Ihr Schmerz klar abgegrenzt, oder diffus verläuft."
                                                                     "Über die Auflösung können Sie bestimmen, ob der Schmerz ein abgeschlossene Fläche ist, oder aus Einzelpunkten besteht."])
   [:div.divider]
   (simple-parameter-slider :sharpness "Schärfe" 0.05 0.95 0.01 event->number false)
   (simple-parameter-slider :dissolve "Auflösung" 0 1 0.01 event->number false)])

(defclass circular-btn []
  {:height "38px"
   :width "38px"
   :border-radius "50%"
   :cursor :pointer
   :margin "8px 16px 0 0"})

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
  [:div.box
   (title "./assets/icons/headers/Farbe.png" "Farbe" ["Welche Temperatur hat Ihr Schmerz?"
                                                      "Wählen Sie eine Farbe und passen Sie die Helligkeit an."
                                                      "Würden Sie Ihren Schmerz mit einem Verlauf darstellen?"
                                                      "Sie können einen Verlauf zu Gelb, zu Orange oder keinen Verlauf wählen."
                                                      "Mit dem Schieberegler können Sie die Stärke des Verlaufes bestimmen."])
   [:div.divider]
   [:li {:key :color}
    [:label {:for :color} "Farbe"]
    (let [selected @(re-frame/subscribe [::subs/parameter :color])]
      [:div.row {:style {:justify-content :flex-start}}
       (color-radio-button "rgb(250, 27, 27)" (= :red selected) (set-color-fn :red))
       (color-radio-button "rgb(13, 36, 239)" (= :blue selected) (set-color-fn :blue))
       (color-radio-button "rgb(250, 187, 27)" (= :yellow selected) (set-color-fn :yellow))])]
   (simple-parameter-slider :lightness "Helligkeit" 0.3 0.8 0.01 event->number false)
   [:li {:key :outerColor}
    [:label {:for :outerColor} "Verlauf zu"]
    (let [selected @(re-frame/subscribe [::subs/parameter :outerColor])]
      [:div.row {:style {:justify-content :flex-start}}
       (colorshift-radio-button "rgb(250, 187, 27)" (= :yellow selected) (set-colorshift-fn :yellow))
       (colorshift-radio-button "rgb(250, 142, 27)" (= :orange selected) (set-colorshift-fn :orange))
       (colorshift-radio-button "rgb(196, 196, 196)" (= :transparency selected) (set-colorshift-fn :transparency))])]
   (let [selected-transparency (= @(re-frame/subscribe [::subs/parameter :outerColor]) :transparency)]
     (simple-parameter-slider :colorShift "Verlauf zu" 0 1 0.01 event->number selected-transparency))])

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
  (let [id-value @(re-frame/subscribe [::subs/parameter id])
        file-state (if (= id selected) "selected"
                       (if (= id-value 0) "inactive" "clear"))
        on-click-fn (if (= file-state "inactive") #() (set-animation-parameter-fn id))]
    (image-radio-button (str "url(./assets/icons/Animationsparameter/" file-prefix "-" file-state ".png)") on-click-fn)))

(defn animation []
  [:div.box
   (title "./assets/icons/headers/Animation.png" "Animation" ["Ist Ihr Schmerz pulsierend, drückend oder stechend oder still? Hat dieser eine Richtung?"
                                                              "Stellen Sie dieses als nächstes ein!"
                                                              "Wählen Sie erst, ob diese aufbauend, abbauend oder gleichmäßig pulsieren sollen und anschließend, welchen 
                                                               Parameter (Größe, Form, Materialität, Schärfe) Sie animieren wollen."
                                                              "Über die Schieberegler können Sie die Frequenz und das Volumen der Animation einstellen."])
   [:div.divider]
   [:li {:key :animation-behavior}
    [:label {:for :animation-behavior} "Verhalten"]
    (let [selected @(re-frame/subscribe [::subs/parameter :animation-behavior])]
      [:div
       [:div.row {:style {:justify-content :flex-start}}
        (simple-animation-behavior-picker "aus" :off selected)
        (simple-animation-behavior-picker "Aufbauend" :linear-in selected)
        (simple-animation-behavior-picker "Abbauend" :linear-out selected)
        (simple-animation-behavior-picker "Gleichmaessig" :soft selected)]
       [:div.divider {:style {:margin-top "16px"}}]])]
   [:li {:key :animation-parameter}
    [:label {:for :animation-parameter} "Parameter"]
    (let [selected @(re-frame/subscribe [::subs/parameter :animation-parameter])]
      [:div
       [:div.row {:style {:justify-content :flex-start}}
        (simple-animation-parameter-picker "size" :radius selected)
        (simple-animation-parameter-picker "spikes" :wing-length selected)
        (simple-animation-parameter-picker "amount" :dissolve selected)
        (simple-animation-parameter-picker "gradient" :sharpness selected)]
       [:div.divider {:style {:margin-top "16px"}}]])]
   (simple-parameter-slider :animation-frequency-hz "Frequenz" 0.4 5 0.01 event->number false)
   (simple-parameter-slider :animation-amplitude "Volumen" 0.1 1 0.01 event->number false)
   [:div.divier]
   [:li {:key :animation-direction}
    [:label {:for :animation-direction} "Richtung"]
    [:div.center (reagent/create-element Joystick (clj->js {:size 200
                                                            :stickSize 70
                                                            :sticky true
                                                            :baseColor styles/very-light-grey
                                                            :stickColor styles/dark-grey
                                                            :stop #(let [obj (js->clj %)]
                                                                     (re-frame/dispatch [::events/set-param :animation-origin
                                                                                         [(get obj "x") (get obj "y")]]))}))]]])

(defn save-media []
  (re-frame/dispatch [::events/set-exporting true])
  (let [<750px? (< (-> js/window
                       .-screen
                       .-width) 750)
        onMobileSafari (and <750px? (not (.isTypeSupported js/MediaRecorder "video/webm")))]
    (.then (js/Promise.resolve ^js (if onMobileSafari
                                     ^js (.saveAsVideo @db/pain-vis 10)
                                     ^js (.saveAsGif @db/pain-vis)))
           #((let [a (.getElementById js/document "recording")]
               (.setAttribute a "href" %)
               (.setAttribute a "download" (str "schmerzerfassung." (if onMobileSafari "mp4" "gif")))
               (.click a)
               (re-frame/dispatch [::events/set-exporting false]))))))

(defn export []
  (let [exporting @(re-frame/subscribe [::subs/exporting])]
    [:div.box
     [:div.center
      [:p
       {:style {:font-weight :bold :text-align :center}}
       (if exporting
         "Der Download beginnt in wenigen Sekunden. Das Exportieren kann bis zu 30 Sekunden dauern. Bitte verlassen Sie die Seite nicht."
         "Die Schmerzerfassung ist abgeschlossen. Sie können die animierte Darstellung jetzt herunterladen.")]]
     [:div.center
      (if exporting
        [:div.lds-ellipsis [:div] [:div] [:div] [:div]]
        [outlined-button ["Download" [:div {:class (download-button-icon-container)}
                                      [:div {:class (download-button-icon)}]]] save-media styles/green {}])]]))

(comment
  (.click (.getElementById js/document "recording")))