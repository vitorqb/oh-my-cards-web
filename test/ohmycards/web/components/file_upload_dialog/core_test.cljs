(ns ohmycards.web.components.file-upload-dialog.core-test
  (:require [cljs.core.async :as a]
            [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.components.dialog.core :as dialog]
            [ohmycards.web.kws.components.dialog.core :as kws.dialog]
            [ohmycards.web.components.file-upload-dialog.core :as sut]
            [ohmycards.web.test-utils :as tu]
            [reagent.core :as r]))

(defn mk-props [{::keys [selected-file-chan]}]
  (let [state (r/atom {::sut/selected-file-chan selected-file-chan
                       kws.dialog/last-active-element (tu/new-dom-element-stub)})
        props {:state state}]
    props))

(deftest test-integration-on-change!--does-nothing-if-no-file
  (async
   done
   (let [selected-file-chan (a/chan 1)
         props (mk-props {::selected-file-chan selected-file-chan})
         event (clj->js {:target {:files []}})]
     (sut/on-change! props event)
     (a/go
       (is (nil? (a/<! selected-file-chan)))
       (done)))))

(deftest test-integration-on-change!--sends-file-to-chan
  (async
   done
   (let [selected-file-chan (a/chan 1)
         props (mk-props {::selected-file-chan selected-file-chan})
         event (clj->js {:target {:files ["file"]}})]
     (sut/on-change! props event)
     (a/go
       (is (= "file" (a/<! selected-file-chan)))
       (done)))))

(deftest test-integration-show-and-hide
  (async
   done
   (let [dialog-show-calls (atom 0)]
     (with-redefs [dialog/show! #(swap! dialog-show-calls inc)]
       (let [props (mk-props {})
             state (:state props)
             chan  (sut/show! props)]
         (is (= 1 @dialog-show-calls))
         (is (= chan (::sut/selected-file-chan @state)))
         (sut/hide! props)
         (is (nil? (::sut/selected-file-chan @state)))
         (a/go
           (is (nil? (a/<! chan)))
           (done)))))))
