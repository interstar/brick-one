(ns brick-one.core
  (:gen-class)
  (:require [odoyle.rules :as o]
            [clojure.math :as math]
            [brick-one.ws-channel :as ws]
            [clojure.core.async :refer [chan poll! <!! >!!]]
            ))

;; * Helpers

;; Given coordinates c1 and c2, create a new one that brings c1 closer to c2
(defn move [c1 c2]
   {
    :x (if (> (:x c2) (:x c1)) (+ 1 (:x c1)) (- 1 (:x c1) ) )
    :y (if (> (:y c2) (:y c1)) (+ 1 (:y c1)) (- 1 (:y c1) ) )
   }
)

;; Given coordinates c1 and c2, calculate the Euclidian distance between them
(defn distance [c1 c2]
 (let [x1 (:x c1)
       y1 (:y c1)
       x2 (:x c2)
       y2 (:y c2)]
    (prn x1 y1 x2 y2)
    (abs (math/sqrt (+ (math/pow (- x1 x2) 2)
                          (math/pow (- y1 y2) 2))))))


;; * Rules, entities, and session management

;; Dump the session
(defn dump [session]
  (prn "Engine Inqueue: " ws/*engine-inqueue)
  (prn "Sesssion: "(o/query-all session)))


(def rules
  (o/ruleset
   {::pages
    [:what
      ;; A simple page model
      [id :page/uid uid]
      [id :page/center coords]
      [id :page/w w]
      [id :page/h h]
      [id :page/attractor? is-attractor]
      [id :page/distance-to-attractor distance]
      [id :page/colour c]]

    ::ticker
    [:what
      [::time :time/total tt]]

    ::movement
    [:what
      ;; At every tick, if you find 2 pages such that one isn't an attractor and
      ;; and one is, move the former towards the latter until the distance between
      ;; them becomes less than 2.
      [::time ::total tt]
      [p1 :page/attractor? false]
      [p2 :page/attractor? true]
      [p1 :page/center c1 {:then false}]
      [p2 :page/center c2]
      [p1 :page/distance-to-attractor d {:then false}]
     :when
     (>= d 3)
     :then
       (o/insert!  p1 :page/center (move c1 c2))
       (o/insert!  p1 :page/distance-to-attractor (distance c1 c2))
     ]
   }))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; GLOBALS

;; create the session
(def *session (atom (o/->session)))



;;  Add all the rules and some entities to the session
(defn init-session []

  (swap! *session
    (fn [session]
      (->
        ;; Add all the rules
        (reduce o/add-rule session rules)

        ;; Add some page entities
        (o/insert 1 :page/center {:x 2 :y 2 } )
        (o/insert 1 :page/attractor? false)

        (o/insert 2 :page/center {:x 15 :y 15})
        (o/insert 2 :page/attractor? true)

        ;; we also need to initialise this value with something
        (o/insert 1 :page/distance-to-attractor 100) ))))






;; A step of the runtime

;; If there's a message from websocket, deal with it

;; In principle by querying or updating session. At the moment, just reply that
;; then engine heard it

;; The format we receive messages is {:reply-chan a-reply-channel
;; :edn parsed-edn-of-msg-from-client}

;; The engine replies to the websocket server by pushing the reply to a-reply-channel

;; If there's no message, poll! returns nil

;; Finally update session with only the next counter value

(defn tick [session counter]
  (prn "Counter: " counter)
  (let [msg (poll! ws/*engine-inqueue)
        ]
    (if msg
      (do
        (println (str "******** Engine received from client: " msg))
        (>!! (:reply-chan msg) (str "A reply from the engine about " msg)) ))

    (swap! *session
           (fn [session]
             (-> session
                 (o/insert ::time ::total counter)

                 o/fire-rules))))
)

;; The main loop of the runtime
(defn run [iterations]
  (loop [session *session
         counter 0]
    (if (= counter iterations)
      session
      (do
        (Thread/sleep 50)
        (recur (tick session counter) (inc counter))))))



(defn -main [& x]
  (ws/run-aleph 8888)
  (init-session)
  (run 1500))
