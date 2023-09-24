(ns pain-app-mobile.effects
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-fx
 :calc-area-asset-aspect-ratio
 (fn [img-src]
   (let [img (new js/Image)]
     (set! (.-onload img) #(re-frame/dispatch [:set-area-asset-aspect-ratio (/ (.-width img) (.-height img))]))
     (set! (.-src img) img-src))))
