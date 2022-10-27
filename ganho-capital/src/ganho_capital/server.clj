(ns ganho-capital.server
  (:gen-class)
  (:require [common-io.components.container-servlet :as container-servlet]
            [common-io.components.embedded-servlet :as embedded-servlet]
            [ganho-capital.components :as components]))

(def -main           (partial embedded-servlet/main             components/create-and-start-system!))
(def run-dev         (partial embedded-servlet/run-dev          components/create-and-start-system!))
(def servlet-init    (partial container-servlet/servlet-init    components/create-and-start-system!))
(def servlet-destroy (partial container-servlet/servlet-destroy components/stop-system!))
(def servlet-service          container-servlet/servlet-service)
