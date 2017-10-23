(ns spotify-pathom.controllers.graph.track
  (:require [spotify-pathom.adapters :as adapt]
            [spotify-pathom.logic.api :as api]
            [spotify-pathom.logic.graph :as graph]
            [com.wsscode.pathom.connect :as p.connect]))

(defn audio-features [env {:spotify.track/keys [id]}]
  (-> {::api/endpoint (str "audio-features/" id)}
      (merge env)
      (api/api)
      (adapt/track)))

(graph/add-resolver! `audio-features
  {::p.connect/input #{:spotify.track/id}
   ::p.connect/output [:spotify.track/instrumentalness
                       :spotify.track/track-href
                       :spotify.track/acousticness
                       :spotify.track/energy
                       :spotify.track/time-signature
                       :spotify.track/analysis-url
                       :spotify.track/valence
                       :spotify.track/duration-ms
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
  (-> {::api/endpoint (str "tracks/" id)}
      (merge env)
      (api/api)
      (adapt/track)))

(graph/add-resolver! `track-by-id
  {::p.connect/input #{:spotify.track/id}
   ::p.connect/output [:spotify.track/href
                       :spotify.track/available-markets
                       :spotify.track/popularity
                       :spotify.track/disc-number
                       #:spotify.track{:album [:spotify.album/album-type
                                               #:spotify.album{:external-urls [:spotify]}
                                               #:spotify.album{:images [:height :url :width]}
                                               :spotify.album/available-markets
                                               #:spotify.album{:artists [{:external-urls [:spotify]} :href :id :name :type :uri]}
                                               :spotify.album/name
                                               :spotify.album/uri
                                               :spotify.album/type
                                               :spotify.album/href
                                               :spotify.album/id]}
                       :spotify.track/explicit
                       :spotify.track/name
                       :spotify.track/duration-ms
                       #:spotify.track{:artists [#:spotify.artist{:external-urls [:spotify]}
                                                 :spotify.artist/href
                                                 :spotify.artist/id
                                                 :spotify.artist/name
                                                 :spotify.artist/type
                                                 :spotify.artist/uri]}
                       :spotify.track/uri
                       :spotify.track/type
                       #:spotify.track{:external-ids [:isrc]}
                       #:spotify.track{:external-urls [:spotify]}
                       :spotify.track/preview-url
                       :spotify.track/id
                       :spotify.track/track-number]})
