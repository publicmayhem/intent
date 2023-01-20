(ns intent.concept.derive
  (:require [clojure.set :as clj-set]
            [clojure.tools.logging :as log]))

(def ^:dynamic *derive-cache* (atom {}))

(defmacro with-cache [new-cache-value & body]
  `(with-bindings {#'*derive-cache* (atom ~new-cache-value)} ~@body))

(defn def-derive-concept [unique-key fn output-concept-key & input-concept-keys]
  (log/debug :def-derive unique-key output-concept-key :-> input-concept-keys)
  (let [paths (get *derive-cache* output-concept-key [])]
    (swap! *derive-cache* assoc output-concept-key
           (conj paths {:id unique-key :fn fn :out-key output-concept-key
                        :in-keys input-concept-keys :in-keys-set (set input-concept-keys)
                        :in-keys-count (count input-concept-keys)}))))

(defn direct-route-filter [input-keys]
  (let [input-keys-set (if (set? input-keys) input-keys (set input-keys))]
    (fn [{:keys [in-keys-set in-keys-count]}]
      (= (count (clj-set/intersection in-keys-set input-keys-set)) in-keys-count))))

; in -> [:a :b :c]
; z -> [fn-1{:a :d} fn-2{:a :e}]
; d -> [fn-3{:f}]
; f -> [fn-4{:c}]
; [:a :b :c] -> [:a :b :c :f] -> [:a :b :c :f :d] -> [:a :b :c :f :d :z] -> :z
(defn indirect-derive-routes [output-concept-key input-concept-keys max-depth]
  (loop [in-keys (set input-concept-keys)
         route []
         remaining-depth max-depth]
    (if (= 0 remaining-depth)
      nil)))

(defn direct-derive-routes [output-concept-key input-concept-keys]
  (if-let [by-out-key (get @*derive-cache* output-concept-key)]
    (let [_ (log/debug :possible-derive-routes :out-key-matched)
          filtered (filter (direct-route-filter input-concept-keys) by-out-key)]
      (log/debug :possible-derive-routes :count (count filtered))
      (map vector filtered))
    (log/debug :possible-derive-routes :no-match :out-key-matched)))

(defn reorder-inputs [required-order input-map]
  (for [k required-order]
    (get input-map k)))

(defn derive-concept-fn [{:keys [id fn out-key in-keys]} input-map]
  (log/debug :deriving id in-keys :-> out-key)
  (let [reordered-inputs (reorder-inputs in-keys input-map)]
    (apply fn reordered-inputs)))

(defn derive-concept
  ([route input-concept-keys input-concept-vals]
   (derive-concept route (zipmap input-concept-keys input-concept-vals)))
  ([route input-map]
   (derive-concept-fn (first route) input-map)))

