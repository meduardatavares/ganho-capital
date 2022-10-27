(ns ganho-capital.diplomat.http-client)

(def bookmarks-settings {})

(comment
  (require '[common-core.protocols.http-client :as protocols.http-client :refer [IHttpClient]]
           '[schema.core :as s])

  (def bookmarks-settings
    "Defines schemas that will be utilized for coercing request and response bodies in the communication with bookmarks declared in the ganho-capital_config.json.base."
    {:some-bookmark
     [{:method       :post
       :schema-req   s/Str
       :schema-resps {200 s/Str
                      404 s/Str}}]})

  (s/defn example
    "Demonstrates a typical outgoing HTTP function.

Request and response bodies are coerced according to schemas defined in the bookmarks-settings var, by matching the HTTP verb in question.
Response bodies schemas are determined from the returned status code (i.e. it's possible to define different schemas for different statuses)."
    [http :- IHttpClient]
    (:body (protocols.http-client/req! http {:method  :post
                                             :url     :some-bookmark
                                             :payload "example"}))))
