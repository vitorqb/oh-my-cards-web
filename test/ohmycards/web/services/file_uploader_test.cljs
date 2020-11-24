(ns ohmycards.web.services.file-uploader-test
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.http :as kws.http]
            [ohmycards.web.services.file-uploader :as sut]
            [ohmycards.web.services.http :as services.http]))

(deftest test-ask-and-upload
  (async
   done
   (let [file (clj->js {:name "FOO"})
         response {kws.http/body {:key "key"} kws.http/success? true}
         calls (atom [])
         ask-for-file! #(do (swap! calls conj [::ask-for-file])
                            (a/go file))
         notify! #(do (swap! calls conj [::notify %1]))
         run-http-action (services.http/mk-mock-run-action {:calls calls :response response})
         to-clipboard! #(do (swap! calls conj [::to-clipboard %1]))
         opts {::sut/ask-for-file! ask-for-file!
               ::sut/notify! notify!
               ::sut/run-http-action run-http-action
               ::sut/to-clipboard! to-clipboard!}]
     (a/go (a/<! (sut/ask-and-upload opts))
           (is (= @calls
                  [[::ask-for-file]
                   [::notify "Uploading file: FOO..."]
                   (sut/->FileUploadHttpAction file opts)
                   [::to-clipboard (sut/url-for-key "key")]]))
           (done)))))
