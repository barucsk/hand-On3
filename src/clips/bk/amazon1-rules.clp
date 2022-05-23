(defrule promoPhone
   (product (name P30) (id ?p))
   =>
   (assert (deal (id ?p)(concept "MICA DE CRISTAL Al 50% de descuento")))
)

(defrule meses
   (product (marca apple) (id ?p))
   (cards (bank HSBC))
   =>
   (assert (msis (id ?p) (bank HSBC) (msi 24)))
)