(ns spotify-pathom.controllers.graph.player
  (:require [spotify-pathom.adapters :as adapt]
            [spotify-pathom.logic.api :as api]
            [spotify-pathom.logic.graph :as graph]
            [com.wsscode.pathom.connect :as p.connect]))

(defn player [env _]
  (-> {::api/endpoint (str "me/player")}
      (merge env)
      (api/api)
      (adapt/player)))

(graph/add-resolver! `player
  {::p.connect/output [:spotify.player/timestamp
                       :spotify.player/progress-ms
                       :spotify.player/is-playing
                       #:spotify.player{:item [:spotify.track/preview-url
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
                       :spotify.player/context
                       #:spotify.player{:device [:spotify.device/id
                                                 :spotify.device/is-active
                                                 :spotify.device/is-restricted
                                                 :spotify.device/name
                                                 :spotify.device/type
                                                 :spotify.device/volume-percent]}
                       :spotify.player/repeat-state
                       :spotify.player/shuffle-state]})

(defn devices [env _]
  (-> {::api/endpoint (str "me/player/devices")}
      (merge env)
      (api/api)
      (update :devices #(mapv adapt/device %))
      :devices
      (->> (hash-map :spotify.me/devices))))

(graph/add-resolver! `devices
  {::p.connect/output [#:spotify.me{:devices [:spotify.device/id
                                              :spotify.device/is-active
                                              :spotify.device/is-restricted
                                              :spotify.device/name
                                              :spotify.device/type
                                              :spotify.device/volume-percent]}]})
