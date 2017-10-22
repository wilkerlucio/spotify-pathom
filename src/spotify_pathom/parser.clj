(ns spotify-pathom.parser
  (:require [clj-http.client :as http]
            [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as p.connect]
            [com.wsscode.pathom.profile :as p.profile]))

(def token "")

(def indexes* (atom {}))

(defn add-resolver! [sym options]
  (swap! indexes* p.connect/add sym options))

(defn namespace-keys [m ns]
  (into {} (map (fn [[k v]] [(keyword ns (name k)) v])) m))

(defn api [{::keys [endpoint method token]
            :or    {method :get}}]
  (-> (http/request {:method  method
                     :headers {"Authorization" (str "Bearer " token)}
                     :as      :json
                     :url     (str "https://api.spotify.com/v1/" endpoint)})
      :body))

(defn adapt-album [album]
  (namespace-keys album "spotify.album"))

(defn adapt-artist [artist]
  (namespace-keys artist "spotify.artist"))

(defn adapt-track [track]
  (-> track
      (update :album adapt-album)
      (update :artists #(mapv adapt-artist %))
      (namespace-keys "spotify.track")))

(defn artist-by-id [env {:spotify.artist/keys [id]}]
  (->> {::endpoint (str "artists/" id)}
       (merge env)
       (api)
       (adapt-artist)))

(add-resolver! `artist-by-id
  {::p.connect/input  #{:spotify.artist/id}
   ::p.connect/output [#:spotify.artist{:external_urls [:spotify]}
                       :spotify.artist/popularity
                       #:spotify.artist{:images [:height :url :width]}
                       :spotify.artist/genres
                       :spotify.artist/name
                       :spotify.artist/uri
                       :spotify.artist/type
                       :spotify.artist/id
                       :spotify.artist/href
                       #:spotify.artist{:followers [:href :total]}]})

(defn artist-top-tracks [env {:spotify.artist/keys [id]}]
  (->> {::endpoint (str "artists/" id "/top-tracks?country=BR")}
       (merge env)
       (api)
       :tracks
       (mapv adapt-track)
       (hash-map :spotify.artist/top-tracks)))

(add-resolver! `artist-top-tracks
  {::p.connect/input  #{:spotify.artist/id}
   ::p.connect/output [#:spotify.artist{:top-tracks [:spotify.track/href
                                                     :spotify.track/available_markets
                                                     :spotify.track/popularity
                                                     :spotify.track/disc_number
                                                     #:spotify.track{:album [:spotify.album/album_type
                                                                             #:spotify.album{:external_urls [:spotify]}
                                                                             #:spotify.album{:images [:height :url :width]}
                                                                             :spotify.album/available_markets
                                                                             #:spotify.album{:artists [{:external_urls [:spotify]}
                                                                                                       :href
                                                                                                       :id
                                                                                                       :name
                                                                                                       :type
                                                                                                       :uri]}
                                                                             :spotify.album/name
                                                                             :spotify.album/uri
                                                                             :spotify.album/type
                                                                             :spotify.album/href
                                                                             :spotify.album/id]}
                                                     :spotify.track/explicit
                                                     :spotify.track/name
                                                     :spotify.track/duration_ms
                                                     #:spotify.track{:artists [#:spotify.artist{:external_urls [:spotify]}
                                                                               :spotify.artist/href
                                                                               :spotify.artist/id
                                                                               :spotify.artist/name
                                                                               :spotify.artist/type
                                                                               :spotify.artist/uri]}
                                                     :spotify.track/uri
                                                     :spotify.track/type
                                                     #:spotify.track{:external_ids [:isrc]}
                                                     #:spotify.track{:external_urls [:spotify]}
                                                     :spotify.track/preview_url
                                                     :spotify.track/id
                                                     :spotify.track/track_number]}]})

(defn track-audio-features [env {:spotify.track/keys [id]}]
  (-> {::endpoint (str "audio-features/" id)}
      (merge env)
      (api)
      (namespace-keys "spotify.track")))

(add-resolver! `track-audio-features
  {::p.connect/input #{:spotify.track/id}
   ::p.connect/output [:spotify.track/instrumentalness
                       :spotify.track/track_href
                       :spotify.track/acousticness
                       :spotify.track/energy
                       :spotify.track/time_signature
                       :spotify.track/analysis_url
                       :spotify.track/valence
                       :spotify.track/duration_ms
                       :spotify.track/uri
                       :spotify.track/type
                       :spotify.track/key
                       :spotify.track/tempo
                       :spotify.track/mode
                       :spotify.track/liveness
                       :spotify.track/loudness
                       :spotify.track/speechiness
                       :spotify.track/danceability
                       :spotify.track/id]})

(defn track-by-id [env {:spotify.track/keys [id]}]
  (-> {::endpoint (str "tracks/" id)}
      (merge env)
      (api)
      (adapt-track)))

(add-resolver! `track-by-id
  {::p.connect/input #{:spotify.track/id}
   ::p.connect/output [:spotify.track/href
                       :spotify.track/available_markets
                       :spotify.track/popularity
                       :spotify.track/disc_number
                       #:spotify.track{:album [:spotify.album/album_type
                                               #:spotify.album{:external_urls [:spotify]}
                                               #:spotify.album{:images [:height :url :width]}
                                               :spotify.album/available_markets
                                               #:spotify.album{:artists [{:external_urls [:spotify]} :href :id :name :type :uri]}
                                               :spotify.album/name
                                               :spotify.album/uri
                                               :spotify.album/type
                                               :spotify.album/href
                                               :spotify.album/id]}
                       :spotify.track/explicit
                       :spotify.track/name
                       :spotify.track/duration_ms
                       #:spotify.track{:artists [#:spotify.artist{:external_urls [:spotify]}
                                                 :spotify.artist/href
                                                 :spotify.artist/id
                                                 :spotify.artist/name
                                                 :spotify.artist/type
                                                 :spotify.artist/uri]}
                       :spotify.track/uri
                       :spotify.track/type
                       #:spotify.track{:external_ids [:isrc]}
                       #:spotify.track{:external_urls [:spotify]}
                       :spotify.track/preview_url
                       :spotify.track/id
                       :spotify.track/track_number]})

(def parser
  (p/parser {::p/plugins [(p/env-plugin {::p/reader          [p/map-reader
                                                              p.connect/all-readers
                                                              (p/placeholder-reader ">")]
                                         ::p.connect/indexes @indexes*
                                         ::token             token})
                          p.profile/profile-plugin
                          p/request-cache-plugin
                          p/error-handler-plugin]}))

(comment
  (def CONTEXT {:spotify.artist/id "3WrFJ7ztbogyGnTHbHJFl2"})

  (parser {}
    [{[:spotify.artist/id "3WrFJ7ztbogyGnTHbHJFl2"]
      [:spotify.artist/top-tracks]}])

  (parser {}
    [{[:spotify.artist/id "3WrFJ7ztbogyGnTHbHJFl2"]
      [:spotify.artist/name :spotify.artist/genres]}])

  ; example artist id: "3WrFJ7ztbogyGnTHbHJFl2"

  (p.connect/data->shape (artist-by-id {::token token}
                                       {:spotify.artist/id "3WrFJ7ztbogyGnTHbHJFl2"}))
  )
