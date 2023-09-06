(ns pain-app-mobile.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::overlay (s/or :nil nil? :string (s/coll-of string?)))

(def non-negative-num? (s/and number? #(>= % 0)))
(s/def ::id (s/and int? #(>= % 0)))
(s/def ::position (s/tuple non-negative-num? non-negative-num?))
(s/def ::radius (s/and number? #(<= 10 % 100)))
(s/def ::painarea (s/keys :req-un [::id ::position ::radius]))

(s/def ::areas (s/coll-of ::painarea :max-count 7))
; form parameter
(s/def ::wing-length (s/and number? #(<= 0 % 1)))
(s/def ::spikiness (s/and number? #(<= 0 % 1)))
(s/def ::num-wings (s/and int? #(<= 5 % 40)))
; materialität parameter
(s/def ::sharpness (s/and number? #(<= 0.05 % 1.0)))
(s/def ::dissolve (s/and number? #(<= 0 % 1)))
; farbe paramter
(s/def ::color #{:red :blue :yellow})
(s/def ::lightness (s/and number? #(<= 0.1 % 1)))
(s/def ::outerColor #{:yellow :orange :transparency})
(s/def ::colorShift (s/and number? #(<= 0 % 1)))
; animation parameter
(s/def ::animation-behavior #{:off :linear-in :linear-out :soft})
(s/def ::animation-parameter #{:radius :wing-length :dissolve :sharpness})
(s/def ::animation-frequency-hz (s/and number? #(<= 0.4 % 5)))
(s/def ::animation-amplitude (s/and number? #(<= 0.1 % 1)))
(def unit? (s/and number? #(<= -1 % 1)))
(s/def ::animation-origin (s/tuple unit? unit?))

(s/def ::parameters (s/keys :req-un [::areas

                                     ::wing-length
                                     ::spikiness
                                     ::num-wings

                                     ::sharpness
                                     ::dissolve

                                     ::color
                                     ::lightness
                                     ::outerColor
                                     ::colorShift

                                     ::animation-behavior
                                     ::animation-parameter
                                     ::animation-frequency-hz
                                     ::animation-amplitude
                                     ::animation-origin]))

(s/def ::area-asset string?)
(s/def ::name string?)
(s/def ::page-id keyword?)

(s/def ::db (s/keys :req-un [::name
                             ::overlay
                             ::area-asset
                             ::page-id
                             ::parameters]))

(comment
  (gen/generate (s/gen ::painarea))
  (def t (gen/generate (s/gen ::parameters)))
  (clj->js t)
  (js/console.log (clj->js t))
  (gen/generate (s/gen ::db)))