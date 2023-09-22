(ns pain-app-mobile.views
  (:require [pain-app-mobile.styles :as styles]
            [pain-app-mobile.subs :as subs]
            [pain-app-mobile.views.areapickers :as areapickers]
            [pain-app-mobile.views.components :refer [outlined-button centered-header]]
            [pain-app-mobile.views.painviscontainer :refer [pain-vis-container]]
            [pain-app-mobile.views.paramters :as parameters]
            [re-frame.core :as re-frame]
            [pain-app-mobile.events :as events]
            [spade.core :refer [defclass]]))

(defn intro []
  [:div.main
   [:img {:style {:width "100%" :background-color "white"} :src "./assets/visualpain_header.jpg"}]
   [:div {:class (styles/text-container)}
    [:h1 "Visualpain.app"]
    [:p {:style {:font-weight :bold}} "Sie können über diese Webseite eine persönliche Visualisierung ihrer Schmerzen erstellen und 
                                       auf ihr Gerät herunterladen."]
    [:p {:style {:font-weight :bold}} "Diese Anwendung wurde in einem Forschungsprojekt entwickelt und erhebt keine Daten."]
    [:p "Wenn Sie Hilfe brauchen, drücken Sie auf folgendes Symbol: ⓘ"]
    [:div.center {:style {:margin "1rem 0"}} [outlined-button ["Start"] #(re-frame/dispatch [:set-page-id :areapicker-general]) styles/green {}]]
    [:button {:class (styles/text-button)
              :style {:font-size "1rem" :width :fit-content :text-decoration :underline}
              :on-click #(re-frame/dispatch [:set-page-id :impressum])} "Impressum"]]])

(defclass subtitle []
  {:font-size "0.8rem"
   :color styles/light-grey
   :margin 0
   :margin-bottom "1rem"})

(defn impressum []
  [:div.main
   [centered-header "Impressum"]
   [:div {:class (styles/text-container)}
    [:p "Diese Anwendung ist in dem Forschungsprojekt „Schmerzen Formen“ entwickelt worden, welches in Kooperation zwischen der Fakultät Kunst und Gestaltung der Bauhaus-Universität Weimar und der Klinik für Anästhesiologie und  Intensivmedizin der Universität Jena durchgeführt wurde."]
    [:p "Wir freuen uns über Kritik und Anregungen, schicken Sie uns dazu gerne eine " [:a {:href "mailto:mail@johannesbreuer.de"} "Mail."]]
    [:div.row {:style {:justify-content :center :padding-top "5vh" :padding-bottom "5vh"}}
     [:img {:src "./assets/Logo_Jena.jpg" :style {:height 100}}]
     [:img {:src "./assets/Logo_Weimar.jpg" :style {:height 100}}]]
    [:p {:class (subtitle)} "PhD-Arbeit von Johannes Breuer, M.A., betreut durch: Prof. Dr. Jan Sebastian Willmann und Prof. Dipl. Des. Andreas Mühlenberend 
        (Bauhaus-Universität Weimar). Begleitet von Prof. Dr. med. Winfried Meißner, Dr. Philipp Baumbach und Dr. Christin Arnold 
        (Universitätsklinikum Jena). Die Webseite wurde von Daniel Stachnik, M.A. (Hasso-Plattner Institut der Universität Potsdam) 
        umgesetzt."]
    [:p {:class (subtitle) :style {:font-weight "bold"}} "Angaben gem. § 5 TMG:"]
    [:p {:class (subtitle)} "Johannes Breuer"]
    [:p {:class (subtitle) :style {:margin-bottom 0}} "Bülowstr. 61"]
    [:p {:class (subtitle)} "10783 Berlin"]
    [:p {:class (subtitle) :style {:margin-bottom "2rem"}} "E-Mail: johannes.breuer@uni-weimar.de"]
    [outlined-button ["Zurück"] (fn [] (re-frame/dispatch [:set-page-id :start]))]]])

(defn str->p [i str] [:p {:key i :style {:font-size "1.2rem"}} str])

(defclass overlay-and-backdrop []
  {:width "100%" :height "100%"
   :position :fixed :top 0
   :z-index :3
   :backdrop-filter "blur(25px)"})

(defn overlay-container [overlay]
  [:div.overlay-container {:class (overlay-and-backdrop)}
   [:div.row {:style {:flex-direction :row-reverse :margin-top "3%" :margin-right "5%"}}
    [:button {:class (styles/text-button) :on-click #(re-frame/dispatch [::events/set-overlay nil])} "×"]]
   [:div.center {:style {:padding "10% 10%"}} (map-indexed str->p overlay)]])

(defn main-panel []
  [:div {:style {:display :flex :flex-direction :column :align-items :center}}
   (let [overlay @(re-frame/subscribe [::subs/overlay])]
     (when overlay
       [overlay-container overlay]))
   (let [page-id @(re-frame/subscribe [::subs/page-id])]
     (case page-id
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
       ;; we render the animation canvas here to prevent it from re-mounting the component on page change, thus
       ;; component-did-mount is only called once and the canvas isn't re-rendered repeatedly
       [:div.main
        [parameters/navigation-row]
        (let [asset-location @(re-frame/subscribe [::subs/area-asset])
              parameters @(re-frame/subscribe [::subs/parameters])]
          [pain-vis-container {:asset-location asset-location :parameters parameters}])
        (case page-id
          :pain-points [parameters/painareas]
          :painform [parameters/painform]
          :materialness [parameters/materialness]
          :paincolor [parameters/color]
          :painanimation [parameters/animation]
          :export [parameters/export])]))])