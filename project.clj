(defproject oneup "0.0.1-SNAPSHOT"
  :jvm-opts ^:replace ["-Xms512m" "-Xmx512m" "-server"]
  :dependencies [[org.clojure/clojure "1.9.0-alpha10"]
                 [org.clojure/clojurescript "1.9.93"]
                 [clj-di "0.5.0"]]
  :profiles {:dev {:plugins [[lein-cljsbuild "1.1.3"]]
                   :dependencies [[figwheel-sidecar "0.5.4-7"]]}}
  :clean-targets [:target-path "out"]
  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src"]
                        :figwheel     true
                        :compiler     {:main       "oneup.core"
                                       :asset-path "js/out"
                                       :output-dir "resources/public/js/out"
                                       :output-to  "resources/public/js/main.js"
                                       :source-map true}}
                        {:id           "prod"
                         :source-paths ["src"]
                         :compiler     {:optimizations :advanced
                                        :pretty-print  false
                                        :main       "oneup.core"
                                        :asset-path "out"
                                        :output-dir "target/out"
                                        :output-to  "target/main.min.js"
                                        :source-map "target/main.min.js.map"}}]})