(ns pain-app-mobile.db)

(def default-db
  {:name "re-frame"
   :page-id :start
   :area-asset ""
   :overlay nil
   :parameters {:areas []
                :wing-length 0
                :spikiness 0.2
                :num-wings 15
                :sharpness 0.9
                :dissolve 0
                :color :red
                :lightness 0.5
                :outerColor :transparency
                :colorShift 0.7
                :animation-behavior :off
                :animation-parameter :radius
                :animation-frequency-hz 1
                :animation-amplitude 0.5
                :animation-origin [0 0]}})

(def pain-vis (atom nil))