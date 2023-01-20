(ns intent.concept.example.date.deriving
  (:require [intent.concept.derive :refer [def-derive-concept direct-derive-routes derive-concept with-cache] :as derive]
            [repl.logging :as logging])
  (:import [java.time LocalDate LocalTime Instant ZoneId LocalDateTime]
           [java.util Date])
  (:refer-clojure :exclude [derive]))

(logging/set-logger-level :debug "intent.concept.derive")

(defn local-date-time->local-datetime [^LocalDate local-date ^LocalTime local-time]
  (when (and local-date local-time)
    (LocalDateTime/of local-date local-time)))

(defn local-date-time->instant [^LocalDateTime local-datetime ^ZoneId zone-id]
  (when (and local-datetime zone-id)
    (.toInstant local-datetime zone-id)))

(defn instant->date [^Instant instant]
  (when instant
    (Date/from instant)))

(defn count-derive [] (count @derive/*derive-cache*))

(defn derive-set [] @derive/*derive-cache*)
;
;(with-cache
;  {}
;  (def-derive-concept ::local-date-time->local-datetime local-date-time->local-datetime :date/local-datetime :date/local-date :date/local-time)
;  (def-derive-concept ::local-date-time->instant local-date-time->instant :date/instant :date/local-date :date/local-time :date/zone-id)
;  (def-derive-concept ::instant->date instant->date :date/date :date/instant)
;  (let [possibles (direct-derive-routes :date/local-datetime [:date/local-time :date/local-date])]
;    (derive-concept (first possibles) [:date/local-time :date/local-date] [(LocalTime/of 10 20) (LocalDate/of 2021 10 5)])))


