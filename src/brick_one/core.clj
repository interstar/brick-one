(ns brick-one.core
  (:gen-class)
  (:require [odoyle.rules :as o]
            [clojure.math :as math]))

(defn move [c1 c2]

   {:x (if (> (:x c2) (:x c1)) (+ 1 (:x c1)) (- 1 (:x c1) ) )
    :y (if (> (:y c2) (:y c1)) (+ 1 (:y c1)) (- 1 (:y c1) ) ) }

)

(defn distance [c1 c2]

 (let [x1 (:x c1)
       y1 (:y c1)
       x2 (:x c2)
       y2 (:y c2)]
    (prn x1 y1 x2 y2)
    (abs (math/sqrt (+ (math/pow (- x1 x2) 2)
                          (math/pow (- y1 y2) 2))))))


(defn dump [session]

   (prn (o/query-all session))
)


(def rules
  (o/ruleset
   {::pages
    [:what
      [id :page/uid uid]
      ;[id :page/cx cx]
      ;[id :page/cy cy]
      [id :page/center coords]
      [id :page/w w]
      [id :page/h h]
      [id :page/attractor? attractor?]
      [id :page/distance-to-attractor distance]
      [id :page/colour c]]

    ::ticker
    [:what
      [::time :time/total tt]]


    ::movement
    [:what
      [::time ::total tt]
      [p1 :page/attractor? false]
      [p2 :page/attractor? true]
      [p1 :page/center c1 {:then false}]
      [p2 :page/center c2]
      [p1 :page/distance-to-attractor d {:then false}]
     :then
       (o/insert!  p1 :page/center (move c1 c2))
       (o/insert!  p1 :page/distance-to-attractor (distance c1 c2))
       ;(prn "******************" total-time)
       (dump session)
]
       ;;(prn "********" (move c1 c2))]
}))


(comment
(def rules
  (o/ruleset
    {::print-time
     [:what
      [::time ::total tt]
      :then
      (println tt)]}))
)


;; create session and add rule
(def *session
  (atom (reduce o/add-rule (o/->session) rules)))


(defn init-session []

  (swap! *session
    (fn [session]
      (-> session

        (o/insert 1 :page/center {:x 2 :y 2 } )
        (o/insert 2 :page/center {:x 15 :y 15})


        (o/insert 2 :page/attractor? true)
        (o/insert 1 :page/attractor? false)
        (o/insert 1 :page/distance-to-attractor 100)
    ) )

    )

    (prn "initial " (o/query-all *session))
  )



(defn tick [session counter]
  (prn counter (o/query-all session))

  (swap! *session
    (fn [session]
      (-> session
         (o/insert ::time ::total counter)
         o/fire-rules)))
)

(defn run [*session iterations]
  (loop [session *session
         counter 0]
    (if (= counter iterations)
      session
      (recur (tick session counter) (inc counter)))))



(defn -main
  "I don't do a whole lot."
  [& x]
  (init-session)
  (run *session 10)
  )
