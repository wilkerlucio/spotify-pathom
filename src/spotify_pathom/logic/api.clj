(ns spotify-pathom.logic.api
  (:require [clj-http.client :as http]))

(defn api [{::keys [endpoint method token]
            :or    {method :get}}]
  (-> (http/request {:method  method
                     :headers {"Authorization" (str "Bearer " token)}
                     :as      :json
                     :url     (str "https://api.spotify.com/v1/" endpoint)})
      :body))

(defn url->endpoint [url]
  (->> url (re-find #"v1/(.+)") second))

(defn paged-result [res out]
  {out                                (:items res)
   (keyword (namespace out) (str (name out) "-page")) (dissoc res :items)})
