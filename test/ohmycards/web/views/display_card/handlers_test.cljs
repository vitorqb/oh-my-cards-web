(ns ohmycards.web.views.display-card.handlers-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.common.async-actions.core :as kws.async-actions]
            [ohmycards.web.kws.services.cards-crud.core :as kws.services.cards-crud]
            [ohmycards.web.kws.views.display-card.core :as kws]
            [ohmycards.web.test-utils :as tu]
            [ohmycards.web.views.display-card.handlers :as sut]))

(deftest test-fetch-card-async-action
  
  (testing "Pre reducer"

    (let [async-action (sut/fetch-card-async-action {} {:card-id "id" :storage-key "key"})
          pre-reducer-fn (kws.async-actions/pre-reducer-fn async-action)]

      (testing "Set's loading"
        (is (true? (kws/loading? (pre-reducer-fn {})))))

      (testing "Unsets card"
        (is (nil? (kws/card (pre-reducer-fn {kws/card 1})))))

      (testing "Unsets error message"
        (is (nil? (kws/error-message (pre-reducer-fn {kws/error-message "A"})))))))

  (testing "Post reducer"

    (let [async-action (sut/fetch-card-async-action {} {:card-id "id" :storage-key "key"})
          post-reducer-fn (kws.async-actions/post-reducer-fn async-action)
          good-response {kws.services.cards-crud/read-card {:title "A"}}
          bad-response  {kws.services.cards-crud/error-message "B"}]

      (testing "Set's loading to false"
        (is (false? (kws/loading? (post-reducer-fn {} {})))))

      (testing "Set's fetched card"
        (is (= {:title "A"} (kws/card (post-reducer-fn {} good-response)))))

      (testing "Set's error message"
        (is (= "B" (kws/error-message (post-reducer-fn {} bad-response)))))))

  (testing "Action"

    (testing "Fetches card if no storage-key"
      (let [storage-peek! (tu/new-stub)
            fetch-card! (tu/new-stub)
            props {kws/fetch-card! fetch-card!
                   kws/storage-peek! storage-peek!}
            async-action (sut/fetch-card-async-action props {:card-id "id"})
            action-fn (kws.async-actions/action-fn async-action)]
        (action-fn {})
        (is (= [["id"]] (tu/get-calls fetch-card!)))
        (is (= [] (tu/get-calls storage-peek!)))))

    (testing "Fetches card if storage-key but storage is empty"
      (let [storage-peek! (tu/new-stub {:fn (fn [key]
                                              (is (= key "key"))
                                              nil)})
            fetch-card! (tu/new-stub)
            props {kws/fetch-card! fetch-card!
                   kws/storage-peek! storage-peek!}
            async-action (sut/fetch-card-async-action props {:card-id "id" :storage-key "key"})
            action-fn (kws.async-actions/action-fn async-action)]
        (action-fn {})
        (is (= [["id"]] (tu/get-calls fetch-card!)))
        (is (= [["key"]] (tu/get-calls storage-peek!)))))

    (testing "Reads from storage"
      (let [card {kws.card/id "id"}
            storage-peek! (tu/new-stub {:fn (fn [key]
                                              (is (= key "key"))
                                              card)})
            fetch-card! (tu/new-stub)
            props {kws/fetch-card! fetch-card!
                   kws/storage-peek! storage-peek!}
            async-action (sut/fetch-card-async-action props {:card-id "id" :storage-key "key"})
            action-fn (kws.async-actions/action-fn async-action)]
        (action-fn {})
        (is (= [] (tu/get-calls fetch-card!)))
        (is (= [["key"]] (tu/get-calls storage-peek!)))))))

(deftest test-goto-editcard!

  (testing "Calls goto-editcard! from props with id"
    (let [id             1
          state          (atom {kws/card {kws.card/id id}})
          goto-editcard! #(do [::result %])
          props          {:state state kws/goto-editcard! goto-editcard!}]
      (is (= [::result id] (sut/goto-editcard! props))))))
