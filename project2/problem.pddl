(define (problem wolf_problem)
(:domain boat)
(:objects
 wolf - cargo
 goat - cargo
 cabbage - cargo
 shore1 - shore
 shore2 - shore)

(:init (atShore wolf shore1) (atShore goat shore1) (atShore cabbage shore1)
(eats wolf goat) (eats goat cabbage) (on shore1))

(:goal (and (atShore wolf shore2) (atShore goat shore2) (atShore cabbage shore2) (on shore2)))
)