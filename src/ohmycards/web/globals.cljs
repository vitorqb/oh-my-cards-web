(ns ohmycards.web.globals)

(goog-define VERSION "dev")
(goog-define DEV false)

(when DEV
  (prn "------------------------------------------------------------")
  (prn "-- WARNING: THIS IS A DEV BUILD --")
  (prn "------------------------------------------------------------"))
