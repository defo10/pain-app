(ns pain-app-mobile.utils
  (:require [re-frame.core :as re-frame]))

(defn goto-fn [page-id]
  #(re-frame/dispatch [:set-page-id page-id]))