(defrule promoPhone
   (product (name MacBook) (id ?p))
   =>
   (assert (deal (id ?p)(concept "En la compra de una MacBook llevate el iPhone13 con 30% de descuento")))
)

