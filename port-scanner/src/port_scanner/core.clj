(ns port-scanner.core
  (:gen-class)
  (:require [clojure.java.io :as io])
  (:import [java.net Socket InetSocketAddress]))



(defn port-open? [hostname port]
  (try
   (with-open [sock (Socket.)]
     (.connect sock (InetSocketAddress. hostname port) 50)
     (println (format "Port %d is open" port))
   )
   (catch Exception e false)))

(defn scan [ip p] (->> p
                       (map #(future (port-open? ip %)))
                       (map deref)
                       doall
))

(defn parse
  "Parses arguments and returns the IP and Port"
  [ip ports]
  (let [isIP (re-matches #"\d+\.\d+\.\d+\.\d+" ip)
        isPortRange (re-matches #"\d+-\d+" ports)]
    (if (and isIP isPortRange)
      ( (as-> ports p
                 (.split p "-")
                 (map #(Integer/parseInt %) p)
                 (apply range p)
                 (scan ip p))
       nil
       )
      (
       (println "AAAAAA PARSING FAILED WTF")
      )
    )
  )
)

(defn -main
  "A simple port scanner :)"
  [& args]
  (if (not= (count args) 2)
    (println "Incorrect amount of arguments.")
    (apply parse args)
  )
)

; Por favor nao me processe, colégio 24 horas
; (-main "191.252.156.211" "0-99999")
