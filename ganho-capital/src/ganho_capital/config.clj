(ns ganho-capital.config
  (:require [clojure.string :as string]
            [common-core.protocols.config :as protocols.config]))

(def service-name "ganho-capital")

(def user-agent (format "Nubank/%s" service-name))

(defn version [config] (protocols.config/get! config [:version]))

(defn environment [config] (protocols.config/get! config [:environment]))

(defn services [config svc]
  (string/replace (protocols.config/get! config [:services svc]) #"/api\z" ""))

(defn base-uri [config] (services config :ganho-capital))
