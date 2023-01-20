(ns intent.concept.loom-model
  (:require [loom.graph :as gr]
            [loom.alg :as al]
            [loom.alg-generic :as al2]
            [loom.attr :as att]
            [clojure.set :as cljset]))

(defn assert-node [g node-type k]
  (if (gr/has-node? g k)
    g
    (-> (gr/add-nodes g k)
        (att/add-attr k :node-type node-type))))

(defn assert-nodes [g node-type nodes]
  (loop [graph g nds nodes]
    (if-let [n (first nds)]
      (recur (assert-node graph node-type n) (rest nds))
      graph)))

(defn assert-edge [g src-node dest-node]
  (if (gr/has-edge? g src-node dest-node)
    g
    (gr/add-edges g [src-node dest-node])))

(defn assert-edges [g src-nodes dest-edge]
  (loop [graph g nds src-nodes]
    (if-let [n (first nds)]
      (recur (assert-edge graph n dest-edge) (rest nds))
      graph)))

(defn add-type [g type-id]
  (assert-node g :type type-id))

(defn add-transform [g transform-id out-type in-types]
  (-> g
      (assert-node :transform transform-id)
      (assert-node :type out-type)
      (assert-nodes :type in-types)
      (assert-edge transform-id out-type)
      (assert-edges in-types transform-id)))

(defn test-graph []
  (-> (gr/digraph)
      (add-transform :localdate->:foo :Foo [:LocalDate])
      (add-transform :localdate->localdatetime :LocalDateTime [:LocalDate :LocalTime])
      (add-transform :localdatemoo->localdatetime :LocalDateTime [:LocalDate :Moo])
      (add-transform :localdatetime->instant :Instant [:LocalDateTime :ZoneId])
      (add-transform :instant->Date :Date [:Instant])))

(def g (test-graph))

(defn node-type? [g n required-type]
  (= required-type (att/attr g n :node-type)))

(defn into-non-empty-set [s]
  (if (set? s)
    s
    (when (not-empty s)
      (into #{} s))))

(defn successors [g n type-keyword]
  (into-non-empty-set
        (filter #(node-type? g % type-keyword) (gr/successors g n))))

(defn predecessors [g n type-keyword]
  (into-non-empty-set
        (filter #(node-type? g % type-keyword) (gr/predecessors g n))))

(defn successor-with-predecessor [g n s predecessor-type]
  (let [p-nodes (predecessors g n predecessor-type)]
    (cljset/subset? p-nodes s)))

(defn successors-type [g n s]
  (when (= :type (att/attr g n :node-type))
    (when-let [trans-nodes (successors g n :transform)]
      (into-non-empty-set (filter #(successor-with-predecessor g % s :type) trans-nodes)))))

(defn successor-transform [g n]
  (when (= :transform (att/attr g n :node-type))
    (successors g n :type)))

(defn successors-to-types [g n s]
  (if (= :type (att/attr g n :node-type))
    (successors-type g n s)
    (successor-transform g n)))

(defn nil-on-empty [coll]
  (when (not-empty coll)
    coll))

(defn type-node-successors [g n s]
  (nil-on-empty
    (map first
      (map (fn [[t s]] (map #(vector t %) s))
        (map #(vector % (successor-transform g %))
             (successors-type g n s))))))

(defn pconj [target x]
  (printf "Conj %s with %s" (prn-str target) (prn-str x))
  (conj target x))

(defn post-process-paths [paths start end]
  (cond
    (and start end) (filter #(and (= start (first %)) (= end (last %))) paths)
    (some? start) (filter #(= start (first %)) paths)
    (some? end) (filter #(= end (last %)) paths)
    :else paths))

(defn df-all-valid-paths [g start end input-set]
  (loop [node-list [[start (list start) (conj input-set start)]]
         paths []]
    (if-let [[c p es] (first node-list)]
      (if (not= end c)
        (if-let [succs (type-node-successors g c es)]
          (recur (concat [] (rest node-list) (map #(vector (second %) (concat p %) (conj es (second %))) succs)) paths)
          (recur (concat [] (rest node-list)) (conj paths p)))
        (recur (concat [] (rest node-list)) (conj paths p)))
      (post-process-paths paths start end))))
