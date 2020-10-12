(ns ohmycards.web.kws.components.table.column
  (:refer-clojure :exclude [keyword name]))

(def ^:const keyword "The keyword of the column." ::keyword)
(def ^:const name "The name of the column." ::name)
(def ^:const width "The width for the column. Must be something like \"100px\"" ::width)
(def ^:const align "The alignment for the column. One of 'center', 'justify', 'inherit', 'left', 'right'" ::align)
(def ^:const padding "The padding for the column. One of 'checkbox'|'default'|'none'" ::padding)
