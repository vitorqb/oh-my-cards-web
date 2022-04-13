(ns ohmycards.web.globals)

(goog-define VERSION "dev")
(goog-define DEV false)
(goog-define LOG_ENABLED false)
(goog-define HOSTNAME "127.0.0.1")
(goog-define PORT 3002)

(when DEV
  (prn "------------------------------------------------------------")
  (prn "--------          THIS IS A DEV BUILD               --------")
  (prn "------------------------------------------------------------"))

(when LOG_ENABLED
  (prn "LOG IS ENABLED"))
