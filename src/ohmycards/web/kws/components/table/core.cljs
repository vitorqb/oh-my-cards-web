(ns ohmycards.web.kws.components.table.core)

;; Props
(def ^:const columns "An array of column objects. See `ohmycards.web.kws.components.table.column`." ::columns)
(def ^:const rows "An array of row objects. See `ohmycards.web.kws.components.table.row`." ::rows)
(def ^:const row-details-comp "A react component that displays the details for a row. It will receive `{::row row}` injected in it's props, and shall render the details for this row." ::row-details-comp)

;; Other
(def ^:const row "The row, injected on the prop for `row-details-comp`." ::row)

