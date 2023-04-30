(defproject com.urbandictionary/pug4clj "0.0.5"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0",
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :main ^:skip-aot pug.demo
  :dependencies [[org.clojure/clojure "1.11.1"] [de.neuland-bfi/pug4j "2.0.6"]]
  :repl-options {:init-ns pug.core}
  :deploy-repositories [["clojars"
                         {:password :env/clojars_password,
                          :sign-releases false,
                          :url "https://repo.clojars.org",
                          :username :env/clojars_username}]]
  :profiles {:uberjar {:aot :all,
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})