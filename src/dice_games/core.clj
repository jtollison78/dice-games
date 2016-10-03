(ns dice-games.core
  (:require [clojure.math.combinatorics :as combo])
  (:gen-class))

;This function makes 2 passes to get the depth first behavior I usually want when building trees with memoized fns
;	combineby - a function to collect the leaves once the tree terminates. I've used things from a simple +, 
;		to... (partial reduce max), average, average-elements-of-vector
;	deck - a list of elements which will be fed into digwith
;	digwith - the main funtion that will continue building the tree. This is where the primary logic will go.
(defn doseqfor [combineby deck digwith]
  (do	(doseq [val deck]  (digwith val))
	   (let [vallist (vec (for [val deck] (digwith val)))]
  		(combineby vallist))))


(defn average [v] (/ (reduce + v) (count v)) ) ;to be used as "combineby"

(def dice-choices (combo/subsets (range 6))) ;to be used as the "deck"

(defn score-vp [[k v]] 
	(if (<= k 2)
		(if (>= v 3) (+ (inc k) (max 0 (- v 3)) ) 0)
		0 ))

(defn score-claws [[k v]] (if (= k 2) v 0) )  ;to be switched in below if you want to evaluate non Victory Points dice rolls

(declare rerolls)

;once we have finished the roll, if we have rerolls, we pass this on to step2 which tries all reroll choices and takes the best one
;Note that there are no choices being made here. But we still see similar intermediate repetition from the redraws
;	so building this "internal" table boosts performance (vs just using (combo/selections) or nested dotimes?)
;(v = vector of dice, rrs = # rerolls)
(def fill-dice (memoize (fn [v rrs]
                          ;**ugh, repeating this whole structure feels wrong... (just eat the extra memo'd calls?? that also seems wrong)
                          (cond (and (= (count v) 6) (zero? rrs))  ;need to be able to reroll none, but not call back memo'd fn everytime = check both here and in (doseqfor)
                                  (reduce + (map score-vp (frequencies v))) ;(score)
                                (= (count v) 6)
                                  (second (rerolls v (dec rrs)))
                                :else 
                                 
                                (doseqfor average ;(partial reduce +)  ;**average/max between choices!!
		                               (range 6)
		                               #(let [roll (vec (sort (conj v %)))] ;**may want to -memo (sort) ]
			                               (cond	(and (= (count roll) 6) (zero? rrs))
					                               (reduce + (map score-vp (frequencies roll))) ;(score)
				                               (= (count roll) 6)
					                               (second (rerolls roll (dec rrs)))
				                               :else 
					                               (fill-dice roll rrs)) ) )) ) ))


(def rerolls (memoize (fn [v rrs]
	(doseqfor (comp last (partial sort-by second) ) ;(partial reduce max)  ;;(reduce max (map second %)) ?? if we're packaging a strat into vec ??
		dice-choices
		#(let [roll (mapv v %)]
			[% (fill-dice roll rrs)] 
   ))) ) )  ;;[% (fill-dice roll rrs)] ;packing the choice of dice in a vec to return



(defn -main [] (fill-dice [] 2))


;-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|
;I also tried a lazy version. I don't really know what I"m doing here, and this blows my stack... but maybe you can make it work. 
;It relies more on the combinatorics library and might give you esier pmap/reducers entry points, BUT it gives up the enormous memoization advantage
;**we're not scoring, are we
(defn rr-select [v rrs]
	;(print (frequencies v))
	(if (and (= (count v) 6) (zero? rrs))
		(reduce + (map score-vp (frequencies v))) ;hmm...
		(reduce max 
			(for [e (map (partial map v) (combo/subsets (range 6)))] 
				(average (map #(rr-select (concat e %) (dec rrs)) (combo/selections (range 6) (- (count e) 6)) )  )) ) ))
				;(average (rr-select (concat e (combo/selections (range 6) (- (count e) 6))) (dec rrs) ;??

(defn rr-select0 []
	(map (comp #(rr-select % 2) vec) dice-choices) ;should try again w/dicechoice in here!!
)


;-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|
;Once you've run the -main program to get the game's value, you can get the VP reroll strategy charts by doing the following

;all possible combinations of ways to roll VPs
(def VP-selections 
  (map vec 
       (apply concat 
              (map #(clojure.math.combinatorics/combinations [0 0 0 0 0 0 1 1 1 1 1 1 2 2 2 2 2 2] %) 
                   (reverse (range 7))
                   ))))

;pad rolls with non-vp's so we can look things up by calling rerolls
(defn fill-w-5s [r]
  (vec (concat r (repeat (- 6 (count r)) 5)) ))

(defn and1 [a b] (and a b)) ;helper to supress nil's returned by print (there has to be a better way, but this is what came to mind)


;OK... so... 
(defn print-vp-strat [rerolls-left]
  (reduce and1 (map 
    #(println (map inc %) " - " 
              ;(map inc (first (rerolls % 2))) " - "  ;uncomment this if you want the positions to hold and not just the actual dice
              (map inc (map % (first (rerolls % (dec rerolls-left))))) " - "
              (float (second (rerolls % (dec rerolls-left))) ) ) 
       (map fill-w-5s VP-selections))
          )
  )

;(print-vp-strat 2)
;(print-vp-strat 1)

;-----
;AND sims to check



