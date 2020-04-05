(ns ohmycards.web.common.string.fuzzy-match
  (:require [clojure.string :as s]))

(defn main
  "Fuzzy matches given a user input string and a seqable of strings."
  [inp strings]
  (let [regexp    (re-pattern (s/lower-case (s/replace inp #" " ".+")))
        lower-inp (s/lower-case inp)]
    (for [s strings
          :let [lower-s (s/lower-case s)]
          :when (re-seq regexp lower-s)]
      s)))
