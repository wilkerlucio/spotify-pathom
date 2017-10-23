(ns spotify-pathom.controllers.graph.albums
  (:require [spotify-pathom.adapters :as adapt]
            [spotify-pathom.logic.api :as api]
            [spotify-pathom.logic.graph :as graph]
            [com.wsscode.pathom.connect :as p.connect]))

(defn album-by-id [env {:spotify.album/keys [id]}]
  (-> {::api/endpoint (str "albums/" id)}
      (merge env)
      (api/api)
      (adapt/album)))

(graph/add-resolver! `album-by-id
  {::p.connect/input  #{:spotify.album/id}
   ::p.connect/output [:spotify.album/release-date-precision
                       #:spotify.album{:tracks [:href
                                                {:items [:spotify.track/href
                                                         :spotify.track/available-markets
                                                         :spotify.track/disc-number
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
                                                         #:spotify.track{:external-urls [:spotify]}
                                                         :spotify.track/preview-url
                                                         :spotify.track/id
                                                         :spotify.track/track-number]}
                                                :limit
                                                :next
                                                :offset
                                                :previous
                                                :total]}
                       :spotify.album/album-type
                       #:spotify.album{:external-ids [:upc]}
                       #:spotify.album{:external-urls [:spotify]}
                       #:spotify.album{:copyrights [:text :type]}
                       :spotify.album/popularity
                       #:spotify.album{:images [:height :url :width]}
                       :spotify.album/release-date
                       :spotify.album/label
                       :spotify.album/genres
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
                       :spotify.album/id]})

(defn album-tracks [env {:spotify.album/keys [id]}]
  (-> {::api/endpoint (str "albums/" id "/tracks")}
      (merge env)
      (api/api)
      (update :items #(mapv adapt/track %))
      (->> (hash-map :spotify.album/tracks))))

(graph/add-resolver! `album-tracks
  {::p.connect/input  #{:spotify.album/id}
   ::p.connect/output [#:spotify.album{:tracks [:href
                                                {:items [:spotify.track/href
                                                         :spotify.track/available-markets
                                                         :spotify.track/disc-number
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
                                                         #:spotify.track{:external-urls [:spotify]}
                                                         :spotify.track/preview-url
                                                         :spotify.track/id
                                                         :spotify.track/track-number]}
                                                :limit
                                                :next
                                                :offset
                                                :previous
                                                :total]}]})
