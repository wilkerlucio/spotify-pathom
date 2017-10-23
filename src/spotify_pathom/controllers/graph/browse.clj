(ns spotify-pathom.controllers.graph.browse
  (:require [spotify-pathom.adapters :as adapt]
            [spotify-pathom.logic.api :as api]
            [spotify-pathom.logic.graph :as graph]
            [com.wsscode.pathom.connect :as p.connect]
            [clojure.set :as set]))

(defn featured-playlists [env _]
  (-> {::api/endpoint (str "browse/featured-playlists")}
      (merge env)
      (api/api)
      :playlists
      (update :items #(mapv adapt/playlist %))
      (api/paged-result :spotify.browse/featured-playlists)))

(graph/add-resolver! `featured-playlists
  {::p.connect/output [#:spotify.browse{:featured-playlists [:spotify.playlist/public
                                                             :spotify.playlist/type
                                                             #:spotify.playlist{:images [:height :url :width]}
                                                             #:spotify.playlist{:owner [:spotify.user/display-name
                                                                                        #:spotify.user{:external-urls [:spotify]}
                                                                                        :spotify.user/href
                                                                                        :spotify.user/id
                                                                                        :spotify.user/type
                                                                                        :spotify.user/uri]}
                                                             :spotify.playlist/name
                                                             :spotify.playlist/snapshot-id
                                                             #:spotify.playlist{:external-urls [:spotify]}
                                                             :spotify.playlist/collaborative
                                                             #:spotify.playlist{:tracks [:href :total]}
                                                             :spotify.playlist/id
                                                             :spotify.playlist/href
                                                             :spotify.playlist/uri]}
                       #:spotify.browse{:featured-playlists-page [:href :limit :next :offset :previous :total]}]})

(defn new-releases [env _]
  (-> {::api/endpoint (str "browse/new-releases")}
      (merge env)
      (api/api)
      :albums
      (update :items #(mapv adapt/album %))
      (api/paged-result :spotify.browse/new-releases)))

(graph/add-resolver! `new-releases
  {::p.connect/output [#:spotify.browse{:new-releases [:spotify.album/album-type
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
                       #:spotify.browse{:new-releases-page [:href :limit :next :offset :previous :total]}]})

(defn categories [env _]
  (-> {::api/endpoint (str "browse/categories")}
      (merge env)
      (api/api)
      :categories
      (update :items #(mapv adapt/category %))
      (api/paged-result :spotify.browse/categories)))

(graph/add-resolver! `categories
  {::p.connect/output [#:spotify.browse{:categories [:spotify.category/href
                                                     #:spotify.category{:icons [:height :url :width]}
                                                     :spotify.category/id
                                                     :spotify.category/name]}
                       #:spotify.browse{:categories-page [:href :limit :next :offset :previous :total]}]})

(defn category-by-id [env {:spotify.category/keys [id]}]
  (-> {::api/endpoint (str "browse/categories/" id)}
      (merge env)
      (api/api)
      (adapt/category)))

(graph/add-resolver! `category-by-id
  {::p.connect/input  #{:spotify.category/id}
   ::p.connect/output [:spotify.category/href
                       #:spotify.category{:icons [:height :url :width]}
                       :spotify.category/id
                       :spotify.category/name]})

(defn category-playlists [env {:spotify.category/keys [id]}]
  (-> {::api/endpoint (str "browse/categories/" id "/playlists")}
      (merge env)
      (api/api)
      :playlists
      (update :items #(mapv (comp adapt/playlist (fn [x] (set/rename-keys x {:tracks :tracks-ref}))) %))
      (api/paged-result :spotify.category/playlists)))

(graph/add-resolver! `category-playlists
  {::p.connect/input  #{:spotify.category/id}
   ::p.connect/output [#:spotify.category{:playlists [:spotify.playlist/public
                                                      :spotify.playlist/type
                                                      #:spotify.playlist{:images [:height :url :width]}
                                                      #:spotify.playlist{:owner [:spotify.user/display-name
                                                                                 #:spotify.user{:external-urls [:spotify]}
                                                                                 :spotify.user/href
                                                                                 :spotify.user/id
                                                                                 :spotify.user/type
                                                                                 :spotify.user/uri]}
                                                      :spotify.playlist/name
                                                      #:spotify.playlist{:tracks-ref [:href :total]}
                                                      :spotify.playlist/snapshot-id
                                                      #:spotify.playlist{:external-urls [:spotify]}
                                                      :spotify.playlist/collaborative
                                                      :spotify.playlist/id
                                                      :spotify.playlist/href
                                                      :spotify.playlist/uri]}
                       #:spotify.category{:playlists-page [:href :limit :next :offset :previous :total]}]})
