(ns pain-app-mobile.events
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [pain-app-mobile.db :as db]
            [pain-app-mobile.specs :as specs]
            [re-frame.core :as re-frame]))

(declare _)

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ [_ _]]
   db/default-db))

(re-frame/reg-event-db
 :set-page-id
 (fn [db [_ new-page-id]]
   {:post [(s/valid? ::specs/db db)]}
   (assoc db :page-id new-page-id)))

(re-frame/reg-event-db
 :set-area-asset
 (fn [db [_ new-area-asset]]
   {:post [(s/valid? ::specs/db db)]}
   (assoc db :area-asset new-area-asset)))

(re-frame/reg-event-db
 ::set-overlay
 (fn [db [_ overlay]]
   {:post [(s/valid? ::specs/db db)]}
   (assoc db :overlay overlay)))

(defn update-areas [db [_ new-area]]
  {:pre [(s/valid? ::specs/painarea new-area)]}
  (assoc-in db [:parameters :areas]
            (let [areas (get-in db [:parameters :areas])]
              (if-let [old  (first (filter #(= (:id new-area) (:id %)) areas))]
                (replace {old new-area} areas)
                (conj areas new-area)))))

(re-frame/reg-event-db
 :update-areas
 update-areas)

(re-frame/reg-event-db
 :remove-area-with-id
 (fn [db [_ id]]
   {:post [(s/valid? ::specs/db db)]}
   (assoc-in db [:parameters :areas]
             (into [] (remove #(= id (:id %)) (get-in db [:parameters :areas]))))))

(re-frame/reg-event-db
 ::set-param
 (fn [db [_ parameter value]]
   {:post [(s/valid? ::specs/db db)]}
   (assoc-in db [:parameters parameter]
             value)))

(re-frame/reg-event-db
 ::set-exporting
 (fn [db [_ val]]
   {:post [(s/valid? ::specs/db db)]}
   (assoc db :exporting val)))

(comment
  (def test-db (gen/generate (s/gen ::specs/db)))
  test-db

  (update-areas test-db [nil {:id 10 :position [0 1] :radius 1}]))
