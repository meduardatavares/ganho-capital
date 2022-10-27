(ns ganho-capital.components
  (:refer-clojure :exclude [test])
  (:require [com.stuartsierra.component :as component]
            [common-core.components.config :as config]
            [common-core.components.log-stacker :as log-stacker]
            [common-core.fault-tolerance.circuit-breaker-strategy-rate :as circuit-breaker]
            [common-core.system :as system]
            [common-core.time]
            [common-core.visibility :as vis]
            [common-crypto.component :as crypto]
            [common-crypto.components.mock-crypto :as mock-crypto]
            [common-db.components.mock-s3-store :as mock-s3-store]
            [common-db.components.s3-store :as s3-store]
            [common-finagle.components.announcer :as announcer]
            [common-finagle.components.http-client :as finagle]
            [common-finagle.components.null-announcer :as null-announcer]
            [common-http-client.components.auth :as auth]
            [common-http-client.components.http :as http]
            [common-http-client.components.http.config :as http.config]
            [common-http-client.components.mock-http :as mock-http]
            [common-http-client.components.routes :as routes]
            [common-io.components.container-servlet :as container-servlet]
            [common-io.components.debug-logger :as debug-logger]
            [common-io.components.embedded-servlet :as embedded-servlet]
            [common-io.components.health :as health]
            [common-io.components.mock-servlet :as mock-servlet]
            [common-io.components.mock-token-verifier :as mock-token-verifier]
            [common-io.components.operations-routes :as operations-routes]
            [common-io.components.service :as service]
            [common-io.components.token-verifier :as token-verifier]
            [common-io.components.webapp :as webapp]
            [common-kafka.components.consumer :as consumer]
            [common-kafka.components.impls.null-producer :as kafka.null-producer]
            [common-kafka.components.impls.producer :as kafka.simple-producer]
            [common-kafka.components.mock-consumer :as mock-consumer]
            [common-kafka.components.mock-producer :as mock-producer]
            [common-kafka.components.producer :as producer]
            [common-metrics.components.filters :as filters]
            [common-metrics.components.metrics :as metrics]
            [common-metrics.components.mock-prometheus :as mock-prometheus]
            [common-metrics.components.prometheus :as prometheus]
            [common-repl.components.repl :as repl]
            [ganho-capital.config]
            [ganho-capital.diplomat.consumer :as consumer.settings]
            [ganho-capital.diplomat.http-client :as http-client]
            [ganho-capital.diplomat.http-server]
            [ganho-capital.diplomat.producer :as producer.settings]))

(def http-defaults (assoc http.config/transit-transit-defaults :user-agent ganho-capital.config/user-agent))

(def healthcheckable-components
  [:metrics :consumer :http])

(def webapp-deps
  [:auth :config :crypto :http :metrics :routes :token-verifier :producer :prometheus])

(defn base [{circuit-breaker-channel :circuit-breaker-channel :as initial-state}]
  (component/system-map
    :config                  (config/new-config ganho-capital.config/service-name)
    :circuit-breaker-factory (component/using (circuit-breaker/new-channel-circuit-breaker circuit-breaker-channel) [:metrics])
    :crypto                  (component/using (crypto/new-crypto) [:config :metrics])
    :filters                 (filters/new-filters)
    :auth                    (component/using (auth/new-auth) [:config :crypto])
    :announcer               (component/using (announcer/new-announcer) [:config])
    :health                  (component/using (health/new-health) healthcheckable-components)
    :http-impl               (component/using (finagle/new-http-client) [:metrics :config :crypto])
    :http                    (component/using (http/new-http http-defaults) [:auth :config :crypto :routes :http-impl :metrics])
    :producer-impl           (component/using (kafka.simple-producer/new-producer) [:config :metrics])
    :consumer                (component/using (consumer/new-consumer consumer.settings/settings) [:config :crypto :producer :webapp :metrics :circuit-breaker-factory])
    :producer                (component/using (producer/new-producer producer.settings/settings) [:config :crypto :producer-impl :metrics])
    :metrics                 (component/using (metrics/new-metrics) [:prometheus])
    :operations-routes       (component/using (operations-routes/new-operations-routes "ops") [:config :health :consumer :token-verifier :repl :routes :prometheus])
    :prometheus              (component/using (prometheus/new-prometheus) [:config])
    :repl                    (component/using (repl/new-repl) [:config])
    :routes                  (component/using (routes/new-routes #'ganho-capital.diplomat.http-server/routes {:bookmarks-settings http-client/bookmarks-settings}) [:config])
    :service                 (component/using (service/new-service) [:config :routes :operations-routes :webapp :announcer :metrics])
    :servlet                 (component/using (container-servlet/new-servlet initial-state) [:service])
    :s3-auth                 (component/using (s3-store/new-s3 :auth-bucket) [:config])
    :token-verifier          (component/using (token-verifier/new-token-verifier) [:crypto :s3-auth])
    :webapp                  (component/using (webapp/new-webapp) webapp-deps)))

(defn local [initial-state]
  (merge (base initial-state)
         (component/system-map :servlet (component/using (embedded-servlet/new-servlet) [:service]))))

(defn test [initial-state]
  (merge (dissoc (base initial-state) :http-impl)
         (component/system-map
           :crypto         (component/using (mock-crypto/new-mock-crypto) [:config])
           :token-verifier (component/using (mock-token-verifier/new-mock-token-verifier) [:crypto :s3-auth])
           :s3-auth        (component/using (mock-s3-store/new-mock-s3 {} :auth-bucket) [:config])
           :producer-impl  (component/using (kafka.null-producer/null-producer) [:config])
           :producer       (component/using (mock-producer/new-mock-producer producer.settings/settings) [:config :crypto :producer-impl :metrics :log-stacker])
           :consumer       (component/using (mock-consumer/new-mock-consumer consumer.settings/settings) [:config :crypto :producer :webapp :metrics :log-stacker])
           :servlet        (component/using (mock-servlet/new-servlet) [:service :log-stacker])
           :debug-logger   (debug-logger/new-debug-logger)
           :http           (component/using (mock-http/new-mock-http http-defaults http/raise-400+!) [:auth :config :crypto :routes :log-stacker])
           :announcer      (null-announcer/null-announcer)
           :webapp         (component/using (webapp/new-webapp) (conj webapp-deps :debug-logger))
           :log-stacker    (log-stacker/new-log-stacker))))

(defn staging [initial-state]
  (base initial-state))

(defn sachem
  "This system contains all the components that are needed to run sachem.
  Segregating this avoids starting unused dependencies, just like DynamoDB."
  [_initial-state]
  (component/system-map
    :config              (config/new-config ganho-capital.config/service-name)
    :crypto              (component/using (mock-crypto/new-mock-crypto) [:config])
    :announcer           (null-announcer/null-announcer)
    :producer-impl       (component/using (kafka.null-producer/null-producer) [:config])
    :producer            (component/using (mock-producer/new-mock-producer producer.settings/settings) [:config :crypto :producer-impl])
    :consumer            (component/using (mock-consumer/new-mock-consumer consumer.settings/settings) [:config :crypto :producer])
    :http                (component/using (mock-http/new-mock-http http-defaults) [:config :crypto :routes])
    :routes              (component/using (routes/new-routes #'ganho-capital.diplomat.http-server/routes {:bookmarks-settings http-client/bookmarks-settings}) [:config])
    :operations-routes   (component/using (operations-routes/new-operations-routes "ops") [:config])
    :prometheus          (component/using (mock-prometheus/new-mock {}) [])
    :metrics             (component/using (metrics/new-metrics) [:prometheus])
    :service             (component/using (service/new-service) [:config :routes :operations-routes :webapp :announcer :metrics])
    :webapp              (component/using (webapp/new-webapp) [])))

(def systems-map {:local   local
                  :dev     local
                  :test    test
                  :staging staging
                  :base    base})

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn start-sachem-system! []
  (system/bootstrap! ganho-capital.config/service-name {:test sachem} {}))

(defn stop-system! []
  (vis/info :log :service-shutdown)
  (system/stop-components!))

(defn register-shutdown-hook! []
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. stop-system!)))

(defn- deployed? [system]
  (-> system
      :config
      ganho-capital.config/environment
      #{"staging" "prod"}))

(defn create-and-start-system!
  ([] (create-and-start-system! {}))
  ([initial-state]
   (let [system (system/bootstrap! ganho-capital.config/service-name systems-map initial-state)]
     ;; leiningen issue #1854
     (when (deployed? system) (register-shutdown-hook!))
     system)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn ensure-system-up! []
  (or (deref system/system)
      (create-and-start-system!)))
