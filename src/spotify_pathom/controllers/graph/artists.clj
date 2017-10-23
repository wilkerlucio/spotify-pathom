(ns spotify-pathom.controllers.graph.artists
  (:require [spotify-pathom.adapters :as adapt]
            [spotify-pathom.logic.api :as api]
            [spotify-pathom.logic.graph :as graph]
            [com.wsscode.pathom.connect :as p.connect]))

(defn artist-by-id [env {:spotify.artist/keys [id]}]
  (->> {::api/endpoint (str "artists/" id)}
       (merge env)
       (api/api)
       (adapt/artist)))

(graph/add-resolver! `artist-by-id
  {::p.connect/input  #{:spotify.artist/id}
   ::p.connect/output [#:spotify.artist{:external-urls [:spotify]}
                       :spotify.artist/popularity
                       #:spotify.artist{:images [:height :url :width]}
                       :spotify.artist/genres
                       :spotify.artist/name
                       :spotify.artist/uri
                       :spotify.artist/type
                       :spotify.artist/id
                       :spotify.artist/href
                       #:spotify.artist{:followers [:href :total]}]})

(defn artist-albums [env {:spotify.artist/keys [id]}]
  (-> {::api/endpoint (str "artists/" id "/albums")}
      (merge env)
      (api/api)
      (update :items #(mapv adapt/album %))
      (api/paged-result :spotify.artist/albums)))

(graph/add-resolver! `artist-albums
  {::p.connect/input  #{:spotify.artist/id}
   ::p.connect/output [#:spotify.artist{:albums [:spotify.album/album-type
                                                 :spotify.album/available-markets
                                                 #:spotify.album{:images [:height :url :width]}
                                                 #:spotify.album{:artists [#:spotify.artist{:external-urls [:spotify]}
                                                                           :spotify.artist/href
                                                                           :spotify.artist/id
                                                                           :spotify.artist/name
                                                                           :spotify.artist/type
                                                                           :spotify.artist/uri]}
                                                 :spotify.album/name
                                                 :spotify.album/uri
                                                 :spotify.album/type
                                                 #:spotify.album{:external-urls [:spotify]}
                                                 :spotify.album/href
                                                 :spotify.album/id]}
                       #:spotify.artist{:albums-page [:href :limit :next :offset :previous :total]}]})

(defn artist-top-tracks [env {:spotify.artist/keys [id]}]
  (-> {::api/endpoint (str "artists/" id "/top-tracks?country=BR")}
      (merge env)
      (api/api)
      (update :tracks #(mapv adapt/track %))
      :tracks
      (->> (hash-map :spotify.artist/top-tracks))))

(graph/add-resolver! `artist-top-tracks
  {::p.connect/input  #{:spotify.artist/id}
   ::p.connect/output [#:spotify.artist{:top-tracks [:spotify.track/href
                                                     :spotify.track/available-markets
                                                     :spotify.track/popularity
                                                     :spotify.track/disc-number
                                                     #:spotify.track{:album [:spotify.album/album-type
                                                                             #:spotify.album{:external-urls [:spotify]}
                                                                             #:spotify.album{:images [:height :url :width]}
                                                                             :spotify.album/available-markets
                                                                             #:spotify.album{:artists [#:spotify.artist{:external-urls [:spotify]}
                                                                                                       :spotify.artist/href
                                                                                                       :spotify.artist/id
                                                                                                       :spotify.artist/name
                                                                                                       :spotify.artist/type
                                                                                                       :spotify.artist/uri]}
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
                                                     :spotify.track/track-number]}]})

(defn artist-related-artists [env {:spotify.artist/keys [id]}]
  (-> {::api/endpoint (str "artists/" id "/related-artists")}
      (merge env)
      (api/api)
      (update :artists #(mapv adapt/artist %))
      :artists
      (->> (hash-map :spotify.artist/related-artists))))

(graph/add-resolver! `artist-related-artists
  {::p.connect/input  #{:spotify.artist/id}
   ::p.connect/output [#:spotify.artist{:related-artists [#:spotify.artist{:external-urls [:spotify]}
                                                          :spotify.artist/popularity
                                                          #:spotify.artist{:images [:height :url :width]}
                                                          :spotify.artist/genres
                                                          :spotify.artist/name
                                                          :spotify.artist/uri
                                                          :spotify.artist/type
                                                          :spotify.artist/id
                                                          :spotify.artist/href
                                                          #:spotify.artist{:followers [:href :total]}]}]})
