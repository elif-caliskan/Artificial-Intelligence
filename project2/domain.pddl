
(define (domain boat)
  (:requirements :strips :typing :equality :negative-preconditions :adl) 
  (:types cargo
          shore)
  
  (:predicates (atShore ?obj - cargo ?sh - shore)
		       (eats ?obj1 - cargo ?obj2 - cargo)
		       (on ?sh - shore)
		       (onBoat ?obj))
  
(:action TAKE_TO_BOAT  
    :parameters    (?obj - cargo ?sh - shore)
    :precondition  (and (atShore ?obj ?sh) (on ?sh)
                    (not (exists (?cargo1 - cargo ?cargo2 - cargo) 
                    (and (eats ?cargo1 ?cargo2) (atShore ?cargo1 ?sh) (atShore ?cargo2 ?sh)
                    (not( = ?cargo1 ?obj)) (not(= ?cargo2 ?obj))))))
    :effect        (and (not (on ?sh)) (not (atShore ?obj ?sh)) (onBoat ?obj)))

(:action TAKE_TO_SHORE 
    :parameters    (?obj - cargo ?sh1 - shore)
    :precondition (and (not(on ?sh1)) (not(atShore ?obj ?sh1)) (onBoat ?obj)) 
    :effect        (and (on ?sh1) (atShore ?obj ?sh1) (not(onBoat ?obj))))

(:action GO_TO_SHORE
    :parameters    (?sh1 - shore ?sh2 - shore)
    :precondition (and (on ?sh1) (not(on ?sh2))
                    (not (exists (?cargo1 - cargo ?cargo2 - cargo) 
                    (and (eats ?cargo1 ?cargo2) (atShore ?cargo1 ?sh1) (atShore ?cargo2 ?sh1)))))
    :effect        (and (on ?sh2) (not (on ?sh1))))
)

