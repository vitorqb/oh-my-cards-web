(ns ohmycards.web.components.table.core
  "Implements a table component using Material UI"
  (:require ["@material-ui/core/Table" :default RTable]
            ["@material-ui/core/TableHead" :default RTableHead]
            ["@material-ui/core/TableRow" :default RTableRow]
            ["@material-ui/core/TableCell" :default RTableCell]
            ["@material-ui/core/TableBody" :default RTableBody]
            ["@material-ui/core/styles" :as styles]
            [ohmycards.web.kws.components.table.core :as kws]
            [ohmycards.web.kws.components.table.column :as kws.column]
            [ohmycards.web.kws.components.table.row :as kws.row]
            [reagent.core :as r]
            [ohmycards.web.icons :as icons]
            [ohmycards.web.common.utils :as utils]))

(declare expander-cell)

(def Table RTable)
(def TableHead RTableHead)
(def TableRow RTableRow)
(def TableCell RTableCell)
(def TableBody RTableBody)

;; Helpers
(def styled
  "Access the function used for styled components. See https://material-ui.com/styles/basics/"
  (.-styled styles))

(def TableHeaderCell
  "A custom TableCell for headers, implementing custom style."
  ((styled TableCell)
   #js{:font-weight "750"
       :background-color "#73c2fb"}))

(def TableBodyCell
  "A custom TableCell for body, implementing custom style."
  ((styled TableCell)
   #js{:background-color "#d9e8f8"}))

(def ^:private expander-column
  "A column that contains the expand button, when the rows are expandable."
  {kws.column/keyword ::expander
   kws.column/name nil
   kws.column/width "5px"
   kws.column/align "center"
   kws.column/padding "checkbox"})

(defn- get-columns
  "Returns the columns for the table given it's props."
  [props]
  (cond->> (kws/columns props)
    (kws/row-details-comp props) (concat [expander-column])))

(defn- display-row-details?
  "Returns true if the row details should be rendered for a particular row."
  [{::kws/keys [row-details-comp] ::keys [row]}]
  (and row-details-comp (::expanded? row)))

(defn- get-cell-value
  "Get's the value for a cell (row + col)"
  [{::keys [row col] :as props}]
  (let [col-kw (kws.column/keyword col)]
    (condp = col-kw
      ::expander [expander-cell props]
      (some-> row kws.row/values col-kw))))

;; Components
(defn- expander-cell
  "A cell with the clickable expander icon."
  [{::keys [expanded-rows-indexes row]}]
  (let [on-click #(swap! expanded-rows-indexes utils/toggle-el-in-set (::index row))]
    [:button.icon-button {:on-click on-click}
     [icons/expand]]))

(defn- head
  "The head of the table"
  [props]
  (let [columns (get-columns props)]
    [:> TableHead {}
     [:> TableRow {}
      (for [column columns
            :let [name (kws.column/name column)
                  keyword (kws.column/keyword column)
                  width (kws.column/width column)
                  align (kws.column/align column "left")
                  padding (kws.column/padding column "default")]]
        ^{:key keyword}
        [:> TableHeaderCell {:style (cond-> {} width (assoc :width width))
                             :align align
                             :padding padding}
         name])]]))

(defn- details-row
  "Renders the row with details for the above row."
  [{::keys [row] ::kws/keys [row-details-comp] :as props}]
  [:> TableRow {}
   [:> TableCell {:col-span (-> props get-columns count)}
    (if-not (vector? row-details-comp)
      [row-details-comp {kws/row row}]
      (vec
       (let [[c p & rest] row-details-comp]
         (if (map? p)
           (concat [c (assoc p kws/row row)] rest)
           (concat [c {kws/row row}] (concat [p] rest))))))]])

(defn- body-cell
  "A single cell of the table body"
  [{::keys [row col] :as props}]
  (let [col-kw (kws.column/keyword col)
        width (kws.column/width col)
        value (get-cell-value (assoc props ::row row ::col col))]
    [:> TableBodyCell {:style (cond-> {} width (assoc :width width))
                       :align (kws.column/align col "left")
                       :padding (kws.column/padding col "default")}
     value]))

(defn- body-row
  "A single row of the table body"
  [{::keys [row] :as props}]
  [:<>
   [:> TableRow {}
    (for [col (get-columns props) :let [col-kw (kws.column/keyword col)]]
      ^{:key col-kw}
      [body-cell (assoc props ::col col)])]
   ;; !!!! TODO -> We are currently receiving a single display-row-details for
   ;; !!!! all rows. But it would be smarter to have `kws.rows/details-comp` per
   ;; !!!! row, and adapt all this logic to use a per-row details comp.
   (when (display-row-details? props)
     [details-row props])])

(defn- body
  "The body of the table"
  [{::keys [expanded-rows-indexes] :as props}]
  [:> TableBody {}
   (doall
    (for [[row i] (map vector (kws/rows props) (range))
          :let [expanded? (contains? @expanded-rows-indexes i)
                row' (assoc row ::expanded? expanded? ::index i)
                props' (assoc props ::row row')]]
      ^{:key i}
      [body-row props']))])

(defn main
  "A table component that uses Material UI under the hoods"
  [props]
  (let [expanded-rows-indexes (r/atom #{})]
    (fn [props]
      (let [props' (assoc props ::expanded-rows-indexes expanded-rows-indexes)]
        [:> Table {:size "small"}
         [head props']
         [body props']]))))
