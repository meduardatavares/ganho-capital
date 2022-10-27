(ns ganho-capital.diplomat.producer)

(def settings
  {})

(comment
  (require '[common-core.protocols.producer :as protocols.producer :refer [IProducer]]
           '[schema.core :as s])

  (def greeting-schema {:greeting s/Str})

  (s/defn new-greeting!
    "Demonstrates a typical producer function."
    [greeting-message :- greeting-schema, producer :- IProducer]
    (protocols.producer/produce! producer {:topic   :new-greeting
                                           :message greeting-message}))

  (def settings
    ;; See https://playbooks.nubank.com.br/cloud-infrastructure/kafka/background/content-types/ for more details on Kafka Content Types.
    {:new-greeting {:schema greeting-schema :content-type :edn}}))
