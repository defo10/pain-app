(ns pain-app-mobile.views
  (:require [pain-app-mobile.styles :as styles]
            [pain-app-mobile.subs :as subs]
            [pain-app-mobile.views.areapickers :as areapickers]
            [pain-app-mobile.views.components :refer [outlined-button]]
            [pain-app-mobile.views.paramters :as parameters]
            [re-frame.core :as re-frame]))

(defn header []
  (let [page-id @(re-frame/subscribe [::subs/page-id])]
    [:div {:style
           {:height "80px"
            :position :sticky
            :top 0
            :width "100%"
            :background-color styles/secondary
            :display "flex"
            :justify-content :space-between
            :align-items :center
            :font-weight 600}}
     [:div]
     [:div {:style {:padding "1em"}} (case page-id
                                       :start "WILLKOMMEN"
                                       :impressum "Impressum"
                                       :pain-points "2/6"
                                       :painform "3/6"
                                       :materialness "4/6"
                                       :paincolor "5/6"
                                       :painanimation "6/6"
                                       "1/6")]
     [:div]]))

(defn intro []
  [:div.main
   [:div.img {:style {:width "100%" :background-color "blue" :height "24em"}}]
   [:div {:class (styles/text-container)}
    [:h1 "Visualpain.app"]
    [:p {:style {:font-weight :bold}} "Sie können über diese Webseite eine persönliche Visualisierung ihrer Schmerzen erstellen und auf ihr Gerät herunterladen."]
    [:p {:style {:font-weight :bold}} "Diese Anwendung wurde in einem Forschungsprojekt entwickelt und erhebt keine Daten."]
    [:p "Wenn Sie Hilfe brauchen, drücken Sie auf folgendes Symbol: ⓘ"]
    [outlined-button ["Start"] #(re-frame/dispatch [:set-page-id :areapicker-general])]
    [outlined-button ["Impressum"] #(re-frame/dispatch [:set-page-id :impressum])]]])

(defn impressum []
  [:div {:class (styles/text-container)}
   [:p "Diese Anwendung ist in dem Forschungsprojekt 'Schmerzen Formen' entwickelt worden, welches in Kooperation zwischen der Fakultät Kunst und Gestaltung der Bauhaus-Universität Weimar und der Klinik für Anästhesiologie und Intensivmedizin der Universiät Jena durchgeführt wurde."]
   [:p "Wir freuen uns über Kritk und Anregungen, schicken Sie uns dazu gern eine"]
   [:a {:href "mailto:todo@mail.com"} "Mail."]
   [outlined-button ["Zurück"] (fn [] (re-frame/dispatch [:set-page-id :start]))]])

(comment
  (defn main-panel []
    (let [page-title @(re-frame/subscribe [::subs/page])]
      [:div
       [header]
       [intro]
       [:h1
        {:class (styles/level1)}
        "Hello from " @name]])))

(defn main-panel []
  [:div
   [header]
   (case @(re-frame/subscribe [::subs/page-id])
     :start [intro]
     :impressum [impressum]
     :areapicker-general [areapickers/overview]
     :areapicker-whole-body [areapickers/whole-body]
     :areapicker-part-body [areapickers/part-of-body]
     :areapicker-neck [areapickers/neck]
     :areapicker-arms [areapickers/arms]
     :areapicker-torso [areapickers/torso]
     :areapicker-subbody [areapickers/subbody]
     :areapicker-legs [areapickers/legs]
     :pain-points [parameters/painareas]
     :painform [parameters/painform]
     :materialness [parameters/materialness]
     :paincolor [parameters/color]
     :painanimation [parameters/animation]
     [:h1 "Wrong page id"])])