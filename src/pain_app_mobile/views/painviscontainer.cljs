(ns pain-app-mobile.views.painviscontainer
  (:require [pain-app-mobile.db :as db]
            ["painshapevisualization" :as pain-vis]
            [reagent.core :as reagent]))

(defn pain-vis-container
  "this component manages the lifecycle of the pixi.js pain visualization. Because the render method
   doesn't use props, this component is never re-rendered, thus the canvas remains unique.
   
   This approach loosely follows https://day8.github.io/re-frame/Using-Stateful-JS-Components/"
  []
  (reagent/create-class
   {:reagent-render (fn []
                      [:div#canvasContainer
                       {:style
                        {:position :sticky
                         :top "152px"
                         :background-color "red"
                         :width "100%"
                         :height "50vh"}}])
    :component-did-mount (fn [component]
                           (let [{:keys [asset-location parameters]} (reagent/props component)]
                             (compare-and-set! db/pain-vis nil
                                               (new pain-vis (.getElementById js/document "canvasContainer")))
                             (.then (js/Promise.resolve ^js (.init @db/pain-vis))
                                    #(.start @db/pain-vis asset-location))
                             ^js (.updateModel @db/pain-vis (clj->js parameters))))
    :component-did-update (fn [component]
                            (let [{:keys [parameters]} (reagent/props component)]
                              ^js (.updateModel @db/pain-vis (clj->js parameters))))}))