(ns intent.concept.graph-idea-better
  (:require [loom.graph :as gr]
            [loom.alg :as al]
            [loom.alg-generic :as al2]
            [loom.attr :as att]))

(defn assert-node [g k]
  (if (gr/has-node? g k)
    g
    (gr/add-nodes g k)))

(defn assert-node-attr [g n attr v]
  (if-let [actual-value (att/attr g n attr)]
    (if (= actual-value v)
      g
      (throw (ex-info "Unexpected node attribute value" {:node n :attr attr :expected-value v :actual-value actual-value})))
    (att/add-attr-to-nodes g attr v [n])))

(defn assert-edge [g src-node dest-node]
  (if (gr/has-edge? g src-node dest-node)
    g
    (gr/add-edges g [src-node dest-node])))

(defn assert-edge-attr [g src-node dest-node attr attv]
  (if-let [actual-value (att/attr g src-node dest-node attr)]
    (if (= actual-value attv)
      g
      (throw (ex-info "Unexpected edge attribute value" {:src src-node :dest dest-node :attr attr :expected-value attv :actual-value actual-value})))
    (att/add-attr-to-edges g attr attv [[src-node dest-node]])))

(defn add-transpose-args-to-edge [g node-1-type node-2-type transpose-args]
  (loop [gr g
         args (map-indexed (fn [idx v] [idx v]) transpose-args)]
    (apply println [args])
    (if-let [[idx arg] (first args)]
      (recur (assert-edge-attr gr node-1-type node-2-type idx arg) (rest args))
      gr)))

(defn add-edge [g node-1-type node-2-type transpose-fn transpose-args]
  (-> g
      (assert-node node-1-type)
      (assert-node node-2-type)
      (assert-edge node-1-type node-2-type)
      (assert-edge-attr node-1-type node-2-type :transpose-fn transpose-fn)
      (assert-edge-attr node-1-type node-2-type :args transpose-args)
      ;(add-transpose-args-to-edge node-1-type node-2-type transpose-args)
      ))

;# given
(def g1 (-> (gr/digraph)
            (add-edge :LocalDate :LocalDateTime :localdate-2-localdatetime [:LocalDate :LocalTime])
            (add-edge :LocalTime :LocalDateTime :localdate-2-localdatetime [:LocalDate :LocalTime])
            (add-edge :LocalDateTime :Instant :localdatetime-2-instant [:LocalDateTime :ZoneId])
            (add-edge :ZoneId :Instant :localdatetime-2-instant [:LocalDateTime :ZoneId])
            (add-edge :Instant :Date :instant-2-instant [:Instant])
            ;(add-edge :LocalDate :Date :invalid-fn [:LocalDate :Foo])
            ))

;# when
; Given list of types #{ZoneId, LocalDate, LocalTime}
; And Target type DateInstant

(def in-types [:ZoneId, :LocalDate, :LocalTime])
(def target-type :Date)

;# then
; We have a list of paths between Target -> Each-Element-of{GivenList}
(def all-paths (conj []
                       (al/bf-path g1 (nth in-types 0) target-type)
                       (al/bf-path g1 (nth in-types 1) target-type)
                       (al/bf-path g1 (nth in-types 2) target-type)))

(def all-paths2
  (al2/bf-paths-bi (gr/successors g1) (gr/predecessors g1) :LocalDate :Date))

(def all-paths-span
  (al2/bf-span (gr/successors g1) :LocalDate))
