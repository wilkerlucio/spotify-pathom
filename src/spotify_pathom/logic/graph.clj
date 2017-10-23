(ns spotify-pathom.logic.graph
  (:require [com.wsscode.pathom.connect :as p.connect]))

(defonce indexes (atom {}))

(defn add-resolver! [sym options]
  (swap! indexes p.connect/add sym options))

(comment
  @indexes
  ; reset the index if need to remove resolvers
  (reset! indexes {})

  ; if you just changed some resolvers and need to clean the index, you can run this
  ; line to re-process the items
  (swap! indexes p.connect/reprocess-index))
