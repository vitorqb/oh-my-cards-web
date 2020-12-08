(ns ohmycards.web.services.logging.impl-test
  (:require [ohmycards.web.services.logging.impl :as sut]
            #?(:clj [clojure.test :as t]
               :cljs [cljs.test :refer-macros [are async deftest is testing use-fixtures]])))

#?(:cljs
   (do

     (defn- mk-instance
       "Returns a logging instance for testing."
       [{::keys [calls-atom enabled?]
         :or {calls-atom (atom [])
              enabled? true}}]
       (let [log-fn #(swap! calls-atom conj [::log-fn %&])
             instance (sut/new-instance)]
         (sut/set-log-fn! instance log-fn)
         (when enabled?
           (sut/enable-logging instance))
         (when (not enabled?)
           (sut/disable-logging instance))
         instance))


     (deftest test-log!

       (testing "Does logs if enabled is true"
         (let [calls (atom [])
               instance (mk-instance {::calls-atom calls ::enabled? false})]
           (sut/log! instance "FOO")
           (is (= [] @calls))))

       (testing "Does not log if enabled is false"
         (let [calls (atom [])
               instance (mk-instance {::calls-atom calls ::enabled? true})]
           (sut/log! instance "PREFIX" "FOO")
           (is (= [[::log-fn ["[PREFIX]" "FOO"]]] @calls)))))))
