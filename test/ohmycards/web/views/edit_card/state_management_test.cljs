(ns ohmycards.web.views.edit-card.state-management-test
  (:require [cljs.test :refer-macros [are async deftest is testing use-fixtures]]
            [ohmycards.web.common.coercion.result :as coercion.result]
            [ohmycards.web.kws.card :as kws.card]
            [ohmycards.web.kws.common.async-actions.core :as kws.async-actions]
            [ohmycards.web.kws.services.cards-crud.core :as kws.cards-crud]
            [ohmycards.web.kws.views.edit-card.core :as kws]
            [ohmycards.web.views.edit-card.state-management :as sut]))

(deftest test-card->form-input

  (testing "Base"
    (is (=
         {kws.card/id (coercion.result/success "id" "id")
          kws.card/body (coercion.result/success "body" "body")
          kws.card/title (coercion.result/success "title" "title")
          kws.card/tags (coercion.result/success ["A"] ["A"])
          kws.card/ref (coercion.result/success 1 1)}
         (sut/card->form-input {kws.card/id "id"
                                kws.card/body "body"
                                kws.card/title "title"
                                kws.card/tags ["A"]
                                kws.card/created-at "2019-01-01T11:00:00.000Z"
                                kws.card/updated-at "2019-01-01T11:00:00.000Z"
                                kws.card/ref 1})))))

(deftest test-form-input->card

  (testing "Base"
    (is (=
         {kws.card/id "id"
          kws.card/body "body"
          kws.card/title "title"
          kws.card/tags ["A"]
          kws.card/ref 1}
         (sut/form-input->card
          {kws.card/id (coercion.result/success "id" "id")
           kws.card/body (coercion.result/success "body" "body")
           kws.card/title (coercion.result/success "title" "title")
           kws.card/tags (coercion.result/success ["A"] ["A"])
           kws.card/ref (coercion.result/success 1 1)})))))

(deftest test-card-fetch-async-action

  (let [action (sut/card-fetch-async-action {} "id")
        run-condition-fn (kws.async-actions/run-condition-fn action)
        pre-reducer-fn (kws.async-actions/pre-reducer-fn action)
        post-reducer-fn (kws.async-actions/post-reducer-fn action)]

    (testing "Run condition"
      (testing "Don't fetch if we are currently fetching it"
        (is (false? (run-condition-fn {::sut/is-fetching? true
                                       ::sut/last-fetched-card-id "id2"}))))

      (testing "Don't fetch if card is already fetched"
        (is (false? (run-condition-fn {::sut/is-fetching? false
                                       ::sut/last-fetched-card-id "id"}))))

      (testing "Fetch if ids are different"
        (is (true? (run-condition-fn {::sut/is-fetching? false
                                      ::sut/last-fetched-card-id "id2"})))))

    (testing "Pre reducer"
      (testing "Assocs is-fetching?"
        (is (true? (-> {} (pre-reducer-fn "id") ::sut/is-fetching?))))

      (testing "Assocs last-fetched-card-id"
        (is (= "id" (-> {} (pre-reducer-fn "id") ::sut/last-fetched-card-id)))))

    (testing "Post reducer > With error"
      (let [result (post-reducer-fn {} {kws.cards-crud/error-message "err"})]

        (testing "Sets is-fetching? to false"
          (is (false? (::sut/is-fetching? result))))

        (testing "Set's error message"
          (is (= "err" (kws/error-message result))))

        (testing "Sets loading to false"
          (is (false? (kws/loading? result))))))

    (testing "Post reducer > Success"
      (let [card        {kws.card/id 1}
            service-res {kws.cards-crud/read-card card}
            result (post-reducer-fn {} service-res)]
        
        (testing "Sets card form input on success"
          (is (= (sut/card->form-input card) (kws/card-input result))))

        (testing "Sets selected card on success"
          (is (= card (kws/selected-card result))))

        (testing "Sets is-fetching? to false"
          (is (false? (::sut/is-fetching? result))))

        (testing "Sets loading to false"
          (is (false? (kws/loading? result))))))))
