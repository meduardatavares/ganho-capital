(ns integration.version-test
  (:require [integration.aux.init :as init :refer [defflow]]
            [state-flow.api :refer [match?]]
            [state-flow.helpers.component.servlet :as servlet]))

(defflow check-service-version
  (match? {:body   {:version "0"}
           :status 200}
          (servlet/request {:method :get
                            :uri    "/api/version"})))
