(ns spotify-pathom.controllers.graph
  (:require [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.profile :as p.profile]
            [com.wsscode.pathom.connect :as p.connect]
            [spotify-pathom.controllers.graph.albums]
            [spotify-pathom.controllers.graph.artists]
            [spotify-pathom.controllers.graph.browse]
            [spotify-pathom.controllers.graph.library]
            [spotify-pathom.logic.graph :as logic.graph]))

(def parser
  (p/parser {::p/plugins [(p/env-wrap-plugin #(assoc % ::p.connect/indexes @logic.graph/indexes))
                          ; ^^^ use env-wrap so the indexes on deref on each request, otherwise it can be a
                          ; pain to reload the indexes during development
                          (p/env-plugin {::p/reader [p/map-reader
                                                     p.connect/all-readers
                                                     (p/placeholder-reader ">")]})
                          p/error-handler-plugin
                          p/request-cache-plugin
                          p.profile/profile-plugin]}))
