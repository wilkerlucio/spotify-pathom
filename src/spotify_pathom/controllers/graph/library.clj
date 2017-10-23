(ns spotify-pathom.controllers.graph.library
  (:require [spotify-pathom.adapters :as adapt]
            [spotify-pathom.logic.api :as api]
            [spotify-pathom.logic.graph :as graph]
            [com.wsscode.pathom.connect :as p.connect]))

(defn albums [env _]
  (-> {::api/endpoint (str "me/albums")}
      (merge env)
      (api/api)
      (update :items #(mapv (fn [x] (update x :album adapt/album)) %))
      (->> (hash-map :spotify.me/albums))))

(graph/add-resolver! `albums
  {::p.connect/output [#:spotify.me{:albums [:href
                                             {:items [:added-at
                                                      {:album [:spotify.album/release-date-precision
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
                                                               :spotify.album/id]}]}
                                             :limit
                                             :next
                                             :offset
                                             :previous
                                             :total]}]})

(defn tracks [env _]
  (-> {::api/endpoint (str "me/tracks")}
      (merge env)
      (api/api)
      (update :items #(mapv (fn [x] (update x :track adapt/track)) %))
      (->> (hash-map :spotify.me/tracks))))

(graph/add-resolver! `tracks
  {::p.connect/output [#:spotify.me{:tracks [:href
                                             {:items [:added_at
                                                      {:track [:spotify.track/preview-url
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
                                                               :spotify.track/id]}]}
                                             :limit
                                             :next
                                             :offset
                                             :previous
                                             :total]}]})
