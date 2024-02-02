(ns brick-one.ws-channel
  (:require [aleph.http :as http]
            [manifold.stream :as s]
            [manifold.deferred :as d]
            [clojure.string :as str]
            [clojure.core.async :refer [>!! <!! chan] :as a ]))


;; Global channel with up to 10 messages
;; This is for the websocket handler to post messages to the engine
;; Note that 10 messages can be pushed into this channel
;; before it starts blocking

(def *engine-inqueue (chan 10))



;; handle messages sent over websockets from external clients
;; Currently we bundle up the edn-ified version of the message
;; along with a reply-channel and post it to the engine on the global
;; *engine-inqueue channel
;; we then block, waiting for a reply from the engine (<!! c)
;; and return a message to be sent back to the client on the websocket

(defn handle-message [msg]
  (let [edn (if (str/includes? msg "{")
              (read-string msg)
              msg)
        c (chan)
        for-engine {:reply-chan c :edn edn}]
    (println "Sending to engine:: "for-engine)
    (>!! *engine-inqueue for-engine)
    (<!! c))
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
