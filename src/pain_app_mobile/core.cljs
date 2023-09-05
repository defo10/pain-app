(ns pain-app-mobile.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [pain-app-mobile.events :as events]
   [pain-app-mobile.views :as views]
   [pain-app-mobile.config :as config]
   [pain-app-mobile.specs]
   [clojure.test.check.generators]))

(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
