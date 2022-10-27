(ns ganho-capital.wire.out.discovery
  (:require [common-core.schema :as schema]
            [schema.core :as s]))

;; Please see https://playbooks.nubank.com.br/docs/architecture/tolerant-readers/#summary
;; for an explanation of the wire.out / wire.in distinction.

(def version-schema {:version s/Str})

(def discovery-schema
  (schema/strict-schema {s/Keyword s/Str}))
