(ns ohmycards.web.common.string.fuzzy-match
  (:require [clojure.string :as s]))

(defn- escape-regex
  "Escapes special chars in regexp"
  [r]
  (s/replace r (re-pattern "[-/\\^$*+?.()|[\\]{}]") "\\$&"))

(defn main
  "Fuzzy matches given a user input string and a seqable of strings."
  [inp strings]
  (let [regexp (-> inp escape-regex (s/replace #" " ".+") s/lower-case re-pattern)
        lower-inp (s/lower-case inp)]
    (for [s strings
          :let [lower-s (s/lower-case s)]
          :when (re-seq regexp lower-s)]
      s)))
