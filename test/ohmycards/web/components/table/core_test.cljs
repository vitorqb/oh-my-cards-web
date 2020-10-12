(ns ohmycards.web.components.table.core-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.table.core :as sut]
            [ohmycards.web.kws.components.table.column :as kws.column]
            [ohmycards.web.kws.components.table.core :as kws]
            [ohmycards.web.kws.components.table.row :as kws.row]
            [ohmycards.web.test-utils :as tu]
            ["@material-ui/core/Table" :default Table]
            ["@material-ui/core/TableHead" :default TableHead]
            ["@material-ui/core/TableRow" :default TableRow]
            ["@material-ui/core/TableCell" :default TableCell]
            ["@material-ui/core/TableBody" :default TableBody]
            [reagent.core :as r]))

(def rows [{kws.row/values {::col1 "Foo1" ::col2 "Bar1"}}
           {kws.row/values {::col1 "Foo2" ::col2 "Bar2"}}])

(def columns [{kws.column/keyword ::col1 kws.column/name "Coll 1" kws.column/width "10px"}
              {kws.column/keyword ::col2 kws.column/name "Coll 2"}])

(deftest test-get-columns

  (testing "No `row-details-comp` => Simple get columns"
    (is (= columns (sut/get-columns {kws/columns columns}))))

  (testing "With `row-details-comp` => Add `expander-column`"
    (is (= (concat [sut/expander-column] columns)
           (sut/get-columns {kws/columns columns kws/row-details-comp :div})))))

(deftest test-get-cell-value

  (testing "Regular column -> read from row values"
    (is (= "Foo1" (sut/get-cell-value {::sut/row (first rows) ::sut/col (first columns)}))))

  (testing "Expander column -> use expander-cell"
    (let [props {::sut/row (first rows) ::sut/col sut/expander-column}]
      (is (= [sut/expander-cell props]
             (sut/get-cell-value props))))))

(deftest test-head
  (with-redefs [sut/Table :Table
                sut/TableHead :TableHead
                sut/TableBody :TableBody
                sut/TableRow :TableRow
                sut/TableCell :TableCell
                sut/TableDetailsCell :TableDetailsCell
                sut/TableHeaderCell :TableHeaderCell
                sut/TableBodyCell :TableBodyCell]
    (let [comp (sut/head {kws/columns columns})]
      (is (= [:> :TableHead {}
              [:> :TableRow {}
               [[:> :TableHeaderCell {:style {:width "10px"} :align "left" :padding "default"}
                 "Coll 1"]
                [:> :TableHeaderCell {:style {} :align "left" :padding "default"} "Coll 2"]]]]
             comp)))))

(deftest test-details-row

  (with-redefs [sut/Table :Table
                sut/TableHead :TableHead
                sut/TableBody :TableBody
                sut/TableRow :TableRow
                sut/TableCell :TableCell
                sut/TableDetailsCell :TableDetailsCell
                sut/TableHeaderCell :TableHeaderCell
                sut/TableBodyCell :TableBodyCell]

    (testing "Simple component"
      (let [row (first rows)
            props {kws/columns columns
                   kws/rows rows
                   kws/row-details-comp ::foo
                   ::sut/row row}
            comp (sut/details-row props)]
        (is (= [:> :TableRow {}
                [:> :TableDetailsCell {:col-span 3}
                 [::foo {kws/row row}]]]
               comp))))

    (testing "Vector with children component"
      (let [row (first rows)
            props {kws/columns columns
                   kws/rows rows
                   kws/row-details-comp [::foo1 ::foo2]
                   ::sut/row row}
            comp (sut/details-row props)]
        (is (= [:> :TableRow {}
                [:> :TableDetailsCell {:col-span 3}
                 [::foo1 {kws/row row} ::foo2]]]
               comp))))

    (testing "Vector with props"
      (let [row (first rows)
            props {kws/columns columns
                   kws/rows rows
                   kws/row-details-comp [::foo1 {:a :b} ::foo2]
                   ::sut/row row}
            comp (sut/details-row props)]
        (is (= [:> :TableRow {}
                [:> :TableDetailsCell {:col-span 3}
                 [::foo1 {:a :b kws/row row} ::foo2]]]
               comp))))))

(deftest test-body-cell

  (with-redefs [sut/Table :Table
                sut/TableHead :TableHead
                sut/TableBody :TableBody
                sut/TableRow :TableRow
                sut/TableCell :TableCell
                sut/TableDetailsCell :TableDetailsCell
                sut/TableHeaderCell :TableHeaderCell
                sut/TableBodyCell :TableBodyCell]

    (testing "With width"
      (is (= [:> :TableBodyCell {:style {:width "10px"} :padding "default" :align "left"}
              "Foo1"]
             (sut/body-cell {::sut/row (first rows) ::sut/col (first columns)}))))

    (testing "Without width"
      (is (= [:> :TableBodyCell {:style {} :padding "default" :align "left"} "Bar2"]
             (sut/body-cell {::sut/row (second rows) ::sut/col (second columns)}))))))

(deftest test-row
  (with-redefs [sut/Table :Table
                sut/TableHead :TableHead
                sut/TableBody :TableBody
                sut/TableRow :TableRow
                sut/TableCell :TableCell
                sut/TableDetailsCell :TableDetailsCell
                sut/TableHeaderCell :TableHeaderCell
                sut/TableBodyCell :TableBodyCell]

    (testing "Without expandable rows"
      (let [props {kws/columns columns kws/rows rows ::sut/row (first rows)}
            comp (sut/body-row props)]
        (is (= [:<>
                [:> :TableRow {}
                 [[sut/body-cell (assoc props ::sut/col (first columns))]
                  [sut/body-cell (assoc props ::sut/col (second columns))]]]
                nil]
               comp))))

    (testing "With expandable rows => Not expanded"
      (let [props {kws/columns columns
                   kws/rows rows
                   ::sut/row (first rows)
                   kws/row-details-comp :div}
            comp (sut/body-row props)]
        (is (= [:<>
                [:> :TableRow {}
                 [[sut/body-cell (assoc props ::sut/col sut/expander-column)]
                  [sut/body-cell (assoc props ::sut/col (first columns))]
                  [sut/body-cell (assoc props ::sut/col (second columns))]]]
                nil]
               comp))))
    
    (testing "Expanded"
      (let [row (first rows)
            row (assoc row ::sut/expanded? true)
            props {kws/columns columns
                   kws/rows rows
                   kws/row-details-comp [::row-details {} ::child1]
                   ::sut/row row}
            comp (sut/body-row props)]
        (is (= [:<>
                [:> :TableRow {}
                 [[sut/body-cell (assoc props ::sut/col sut/expander-column)]
                  [sut/body-cell (assoc props ::sut/col (first columns))]
                  [sut/body-cell (assoc props ::sut/col (second columns))]]]
                [sut/details-row props]]
               comp))))))

(deftest test-body
  (with-redefs [sut/Table :Table
                sut/TableHead :TableHead
                sut/TableBody :TableBody
                sut/TableRow :TableRow
                sut/TableCell :TableCell
                sut/TableDetailsCell :TableDetailsCell
                sut/TableHeaderCell :TableHeaderCell
                sut/TableBodyCell :TableBodyCell]
    (let [expanded-rows-indexes (r/atom #{1})
          props {kws/columns columns
                 kws/rows rows
                 ::sut/expanded-rows-indexes expanded-rows-indexes}
          comp (sut/body props)]
      (is (= [:> sut/TableBody {}
              [[sut/body-row (assoc props ::sut/row (assoc (first rows)
                                                           ::sut/expanded? false
                                                           ::sut/index 0))]
               [sut/body-row (assoc props ::sut/row (assoc (second rows)
                                                           ::sut/expanded? true
                                                           ::sut/index 1))]]]
             comp)))))
