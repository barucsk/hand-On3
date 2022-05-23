(defrule promoPhone
   (product (id ?p))
   =>
   (assert (deal (id ?p)(concept "En la compra de un segundo producto te lleavas un 10% de descuento")))
)

(defrule meses
   (product (name P30) (id ?p))
   (cards (bank BBVA))
   =>
   (assert (msis (id ?p) (bank BBVA) (msi 6)))
)