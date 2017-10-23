(ns spotify-pathom.controllers.graph.playlist
  (:require [spotify-pathom.adapters :as adapt]
            [spotify-pathom.logic.api :as api]
            [spotify-pathom.logic.graph :as graph]
            [com.wsscode.pathom.connect :as p.connect]))

(defn playlist-tracks [env {:spotify.playlist/keys [tracks-ref]}]
  (-> {::api/endpoint (api/url->endpoint (:href tracks-ref))}
      (merge env)
      (api/api)
      (update :items #(mapv (fn [x] (update x :track adapt/track)) %))
      (api/paged-result :spotify.playlist/tracks)
      (update :spotify.playlist/tracks #(mapv :track %))))

(graph/add-resolver! `playlist-tracks
  {::p.connect/input #{:spotify.playlist/tracks-ref}
   ::p.connect/output [#:spotify.playlist{:tracks [:spotify.track/preview-url
                                                   :spotify.track/available-markets
                                                   :spotify.track/href
                                                   :spotify.track/duration-ms
                                                   :spotify.track/popularity
                                                   :spotify.track/track-number
                                                   #:spotify.track{:album [:spotify.album/album-type
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
                                                   :spotify.track/explicit
                                                   :spotify.track/name
                                                   #:spotify.track{:artists [#:spotify.artist{:external-urls [:spotify]}
                                                                             :spotify.artist/href
                                                                             :spotify.artist/id
                                                                             :spotify.artist/name
                                                                             :spotify.artist/type
                                                                             :spotify.artist/uri]}
                                                   :spotify.track/uri
                                                   :spotify.track/type
                                                   :spotify.track/disc-number
                                                   #:spotify.track{:external-ids [:isrc]}
                                                   #:spotify.track{:external-urls [:spotify]}
                                                   :spotify.track/id]}
                       #:spotify.playlist{:tracks-page [:href :limit :next :offset :previous :total]}]})
