(ns spotify-pathom.adapters
  (:require [clojure.string :as str]))

(defn namespace-keys [m ns]
  (into {} (map (fn [[k v]] [(keyword ns (-> k name (str/replace "_" "-"))) v])) m))

(defn update-in-if [m path f & args]
  (if (get-in m path)
    (apply update-in m path f args)
    m))

;;;;;;

(declare track user)

(defn typed [item]
  (case (:type item)
    "user" (user item)
    item))

;;;;;;;

(defn artist [artist]
  (-> artist
      (namespace-keys "spotify.artist")))

(defn album [album]
  (-> album
      (update-in-if [:artists] #(mapv artist %))
      (update-in-if [:tracks :items] #(mapv track %))
      (namespace-keys "spotify.album")))

(defn category [category]
  (-> category
      (namespace-keys "spotify.category")))

(defn playlist [playlist]
  (-> playlist
      (update-in-if [:tracks :items] #(mapv track %))
      (update-in-if [:owner] typed)
      (namespace-keys "spotify.playlist")))

(defn track [track]
  (-> track
      (update-in-if [:album] album)
      (update-in-if [:artists] #(mapv artist %))
      (namespace-keys "spotify.track")))

(defn user [user]
  (-> user
      (namespace-keys "spotify.user")))
