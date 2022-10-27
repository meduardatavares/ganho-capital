(defproject ganho-capital "0.1.0-SNAPSHOT"
  :description "ganho-capital"
  :url "https://github.com/nubank/ganho-capital"
  :license {:name "Proprietary"}

  :plugins [[lein-cloverage "1.2.3"]
            [lein-vanity "0.2.0"]
            [lein-ancient "0.7.0-SNAPSHOT"]]

  :repositories ^:replace [["nu-codeartifact" {:url "https://maven.cicd.nubank.world"}]]

  :exclusions [log4j]

  :dependencies [[org.clojure/clojure "1.11.0"]

                 [common-core/common-core               "16.14.1"]
                 [common-crypto/common-crypto           "10.37.1"]
                 [common-db/common-db                   "25.9.0"]
                 [common-finagle/common-finagle         "11.15.0"]
                 [common-http-client/common-http-client "18.2.0"]
                 [common-io/common-io                   "52.12.1"]
                 [common-kafka/common-kafka             "14.15.0"]
                 [common-metrics/common-metrics         "10.13.0"]
                 [common-repl/common-repl               "0.5.1"]]

  :profiles {:uberjar {:aot :all}

             :integration {:test-paths ^:replace ["test/integration/"]}

             :unit {:test-paths ^:replace ["test/unit/"]}


             :dev {:source-paths   ["dev"]
                   :resource-paths ["test/resources/"]
                   :plugins        [[com.github.clojure-lsp/lein-clojure-lsp "1.3.14"]]
                   :dependencies   [[common-test/common-test "18.10.0"]
                                    [codestyle/codestyle "0.10.0"]
                                    [state-flow-helpers/state-flow-helpers "12.3.0"]
                                    [nubank/matcher-combinators "3.5.1"]
                                    [nubank/mockfn "0.7.0"]
                                    [org.clojure/tools.namespace "1.1.0"]]
                   :repl-options   {:init-ns user}}

             :repl-start {:injections   [(require '[ganho-capital.server :as s])
                                         (s/run-dev)]
                          :repl-options {:prompt  #(str "[ganho-capital] " % "=> ")
                                         :timeout 300000
                                         :init-ns user}
                          :test-paths   ^:replace []}}

  :min-lein-version "2.4.2"

  :resource-paths ["resources"]

  :aliases {"run-dev"         ["with-profile" "+repl-start" "trampoline" "repl" ":headless"]
            "run-dev-notramp" ["with-profile" "+repl-start" "repl" ":headless"]
            "diagnostics"     ["clojure-lsp" "diagnostics"]
            "format"          ["clojure-lsp" "format" "--dry"]
            "format-fix"      ["clojure-lsp" "format"]
            "clean-ns"        ["clojure-lsp" "clean-ns" "--dry"]
            "clean-ns-fix"    ["clojure-lsp" "clean-ns"]
            "lint"            ["do" ["diagnostics"]  ["format"] ["clean-ns"]]
            "lint-fix"        ["do" ["format-fix"] ["clean-ns-fix"]]
            "unit"            ["with-profile" "+unit" "test"]
            "integration"     ["with-profile" "+integration" "test"]}

  :main ^{:skip-aot false} ganho-capital.server

  :test-paths ["test/unit" "test/integration"])
