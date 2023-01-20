(ns intent.concept.loom
  (:require [loom.graph :as gr]
            [loom.alg :as al]
            [loom.attr :as att]))

(def g (-> (gr/digraph)
           (gr/add-path 1 2)
           (gr/add-path 2 3)
           (gr/add-path 2 4)
           (gr/add-path 2 5)
           (gr/add-path 4 6)
           (gr/add-path 4 7)
           (gr/add-path 3 5)
           (gr/add-path 7 9)
           (gr/add-path 5 9)
           (gr/add-path 10 12)))

(al/bf-path g 4 9) ; (4 7 9)
(al/bf-path g 4 5) ; nil no directed graph between them

; LDT [LD LT]
; INST [LDT ZID]
; D [INST]

; -> D [LD LT ZID]

(defn assert-node [g k]
  (if (gr/has-node? g k)
    g
    (gr/add-nodes g k)))

(defn assert-node-attr [g k atk atv]
  (if-let [actual-value (att/attr g k atk)]
    (if (= actual-value atv)
      g
      (throw (ex-info "Unexpected node attribute value" {:node k :attr atk :expected-value atv :actual-value actual-value})))
    (att/add-attr-to-nodes g atk atv [k])))

(defn node [g k t]
  (-> g
      (assert-node k)
      (assert-node-attr k :type t)))

(defn derive-node [g k] (node g k :derive))
(defn type-node [g k] (node g k :type))

(defn assert-edge [g src-node dest-node]
  (if (gr/has-edge? g src-node dest-node)
    g
    (gr/add-edges g [src-node dest-node])))

(defn assert-edge-attr [g src-node dest-node atk atv]
  (if-let [actual-value (att/attr g src-node dest-node atk)]
    (if (= actual-value atv)
      g
      (throw (ex-info "Unexpected edge attribute value" {:src src-node :dest dest-node :attr atk :expected-value atv :actual-value actual-value})))
    (att/add-attr-to-edges g atk atv [[src-node dest-node]])))

(defn assert-derive-relationship [g dk tk]
  (-> g
      (type-node tk)
      (derive-node dk)
      (assert-edge dk tk)
      (assert-edge-attr dk tk :type :derives)))

(defn assert-argument-relationship [g dk arg-k idx]
  (-> g
      (derive-node dk)
      (node idx :argument)
      (assert-edge dk idx)
      (assert-edge-attr dk idx :type :argument)
      (assert-edge-attr dk idx :index idx)
      (assert-edge-attr dk idx :arg-type arg-k)))

(defn add-derive [g dk tk args]
  (loop [graph (assert-derive-relationship g dk tk)
         childs args
         idx 0]
    (if-let [f (first childs)]
      (recur (assert-argument-relationship graph dk f idx) (rest childs) (inc idx))
      graph)))

; LDT [LD LT]
; INST [LDT ZID]
; D [INST]

; -> D [LD LT ZID]

(def g1 (-> (gr/digraph)
            (add-derive :d-t->ldt :ldt [:ld :lt])
            (add-derive :ldt->ins :ins [:ldt :zi])
            (add-derive :ins->date :date [:ins])))




