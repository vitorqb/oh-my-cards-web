(ns ohmycards.web.components.file-upload-dialog.core-test
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.dialog.core :as dialog]
            [ohmycards.web.components.file-upload-dialog.core :as sut]
            [reagent.core :as r]))

(deftest test-integration-on-change!--does-nothing-if-no-file
  (async
   done
   (let [file-chan (a/chan 1)
         state (r/atom {::sut/selected-file-chan file-chan})
         props {:state state}
         event (clj->js {:target {:files []}})]
     (sut/on-change! props event)
     (a/go
       (is (nil? (a/<! file-chan)))
       (done)))))

(deftest test-integration-on-change!--sends-file-to-chan
  (async
   done
   (let [file-chan (a/chan 1)
         state (r/atom {::sut/selected-file-chan file-chan})
         props {:state state}
         event (clj->js {:target {:files ["file"]}})]
     (sut/on-change! props event)
     (a/go
       (is (= "file" (a/<! file-chan)))
       (done)))))

(deftest test-integration-show-and-hide
  (async
   done
   (let [dialog-show-calls (atom 0)]
     (with-redefs [dialog/show! #(swap! dialog-show-calls inc)]
       (let [state (r/atom {})
             props {:state state}
             chan  (sut/show! props)]
         (is (= 1 @dialog-show-calls))
         (is (= chan (::sut/selected-file-chan @state)))
         (sut/hide! props)
         (is (nil? (::sut/selected-file-chan @state)))
         (a/go
           (is (nil? (a/<! chan)))
           (done)))))))
