(ns ganho-capital.config-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [common-core.components.config :as components.config]
            [ganho-capital.config :as config]))

(def system-map
  (component/system-map :config
                        (components.config/new-config "ganho-capital")))

(deftest configuration-file

  (let [system (component/start system-map)]

    (testing "reads a version"
      (is (string? (config/version (:config system)))))

    (testing "reads a service uri"
      (is (string? (config/services (:config system) :ganho-capital))))))
