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
                        {:width "100%"
                         :height "60vh"
                         :opacity "0"}}])
    :component-did-mount (fn [component]
                           (let [{:keys [asset-location parameters]} (reagent/props component)]
                             (when (compare-and-set! db/pain-vis nil
                                                     (new pain-vis (.getElementById js/document "canvasContainer")))
                               (.then (js/Promise.resolve ^js (.start @db/pain-vis asset-location))
                                      (fn [] ^js (.updateModel @db/pain-vis (clj->js parameters)))))))
    :component-did-update (fn [component]
                            (let [{:keys [parameters]} (reagent/props component)]
                              ^js (.updateModel @db/pain-vis (clj->js parameters))))
    :component-will-unmount (fn [_]
                              (.destroy @db/pain-vis)
                              (reset! db/pain-vis nil))}))