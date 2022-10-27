(ns ganho-capital.diplomat.http-server
  (:require [clojure.set :as set]
            [common-finagle.interceptors.context :as finagle]
            [common-io.endpoint-specifications :refer [add-base-uri]]
            [common-io.interceptors.adapt :as adapt]
            [common-io.interceptors.auth :as auth]
            [common-io.interceptors.doc :as doc]
            [common-io.interceptors.errors :as errors]
            [common-io.interceptors.identity :as identity]
            [common-io.interceptors.instrument :as instrument]
            [common-io.interceptors.logging :as logging]
            [common-io.interceptors.pedestal-exporter :as pedestal-exporter]
            [common-io.interceptors.visibility :as visibility]
            [common-io.interceptors.wire :as wire]
            [io.pedestal.http.route :refer [expand-routes]]
            [ganho-capital.config :as config]
            [ganho-capital.wire.out.discovery :as wire.out.discovery]))

(def discovery-endpoints  {})

(def admin-discovery-endpoints (merge discovery-endpoints {}))

(defn current-version
  [{{config :config} :components}]
  {:status 200 :body {:version (config/version config)}})

(defn discovery
  [{{config :config} :components}]
  {:status 200 :body (add-base-uri discovery-endpoints (config/base-uri config))})

(defn admin-discovery
  [{{config :config} :components}]
  {:status 200 :body (add-base-uri admin-discovery-endpoints (config/base-uri config))})

(def common-interceptors
  [(instrument/instrument)
   (visibility/cid)
   (errors/catch-externalize)
   wire/to-wire
   (errors/catch)
   (pedestal-exporter/pedestal-exporter)
   wire/from-wire
   (identity/add-identity)
   (finagle/context)
   (logging/log)])

(def default-routes
  #{["/api/version"
     :get (conj common-interceptors
                (doc/desc "Current version")
                (auth/public)
                (adapt/externalize! {200 wire.out.discovery/version-schema})
                current-version)
     :route-name :version]

    ["/api/discovery"
     :get (conj common-interceptors
                (doc/desc "Hypermedia entrypoint")
                (auth/public)
                (adapt/externalize! {200 wire.out.discovery/discovery-schema})
                discovery)
     :route-name :discovery]

    ["/api/admin/discovery"
     :get (conj common-interceptors
                (auth/allow? #{"auth/admin" "auth/trusted"})
                (doc/desc "Admin hypermedia entrypoint")
                (adapt/externalize! {200 wire.out.discovery/discovery-schema})
                admin-discovery)
     :route-name :admin-discovery]})

(def routes
  (expand-routes
   (set/union
     default-routes)))
