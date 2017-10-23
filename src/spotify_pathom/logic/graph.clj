(ns spotify-pathom.logic.graph
  (:require [com.wsscode.pathom.connect :as p.connect]))

(defonce indexes (atom {}))

(defn add-resolver! [sym options]
  (swap! indexes p.connect/add sym options))

(comment
  @indexes
  (reset! indexes {})
  (swap! indexes p.connect/reprocess-index))
