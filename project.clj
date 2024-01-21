(defproject brick-one "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [net.sekao/odoyle-rules "1.3.1"]
                 [aleph "0.7.0"]]
  :plugins [[lein-auto "0.1.3"]]
  :main ^:skip-aot brick-one.core
  :repl-options {:init-ns brick-one.core})
