(ns pain-app-mobile.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::page-id
 (fn [db]
   (:page-id db)))

(re-frame/reg-sub
 ::painareas
 (fn [db]
   (get-in db [:parameters :areas])))

(re-frame/reg-sub
 ::parameter
 (fn [db query-v]
   (get-in db (into [:parameters] (rest query-v)))))

(re-frame/reg-sub
 ::area-asset
 (fn [db]
   (:area-asset db)))

(re-frame/reg-sub
 ::parameters
 (fn [db]
   (:parameters db)))

(re-frame/reg-sub
 ::overlay
 (fn [db]
   (:overlay db)))