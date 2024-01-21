(ns brick-one.ws-channel
  (:require [aleph.http :as http]
            [manifold.stream :as s]
            [manifold.deferred :as d]))

;; UI

(defn handle-message [msg]
  (let [edn (read-string msg)]
    (println msg)
    (println edn)
    (str "I heard \"" msg "\""))
  )

(defn dialog-handler
  [req]
  (println req)
  (d/let-flow [socket (http/websocket-connection req)]
              (let [process-message
                    (fn process-message []
                      (d/chain (s/take! socket)
                               (fn [msg]
                                 (when msg
                                   (do
                                     (s/put! socket (handle-message msg))
                                     (process-message))))))]
                (process-message)))
  (d/catch Exception
    (fn [e]
      {:status 400
       :headers {"content-type" "application/text"}
       :body (str "Expected a websocket request. " e)})))



(defn run-aleph [port]
  (println "Aleph listening on port " port)
  (http/start-server dialog-handler {:port port})
  )
