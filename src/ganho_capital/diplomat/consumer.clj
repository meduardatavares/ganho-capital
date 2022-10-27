(ns ganho-capital.diplomat.consumer
  (:require [common-io.services.auth :as auth]))

(def settings
  {:token-revoked      auth/token-revoked-setting
   :all-tokens-revoked auth/all-tokens-revoked-setting})

(comment
  ;; you'll need to add to this namespace
  (require '[common-core.schema :as schema]
           '[schema.core :as s])
  ;; and the following to the resources/ganho-capital_config.json.base file:
  ;; "new-greeting" : {"topic": "NEW-GREETING", "group": "ganho-capital", "direction": "consumer"},

  (def greeting-schema (schema/loose-schema {:greeting s/Str}))

  (s/defn greeting-handler
    "Demonstrates a typical consumer function."
    [{:keys [message meta components]}]
    (println "The following greeting has been received: " message))

  (def settings
    {:new-greeting {:handler greeting-handler :schema greeting-schema}}))
