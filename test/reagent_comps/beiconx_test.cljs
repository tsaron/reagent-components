(ns reagent-comps.beiconx-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [beicon.core :as rx]
            [reagent-comps.beiconx :as sut]
            [cljs.test :as t :refer-macros [deftest async is testing]]
            [cljs.core.async :as a]))




(deftest test-converter
  (testing "Simple values"
    (let [[v f c] (sut/factory)
          state   (sut/x->ratom "" v)]
      (f "First")
      (f "Latest")
      (is (= "Latest" @state))
      (c)))

  (testing "Using fold"
    (let [[v f c] (sut/factory)
          state   (sut/x->ratom [] conj v)]
      (doseq [x (range 10)]
        (f x))
      (is (-> @state count (= 10)))
      (c))))


(deftest test-channel
  (testing "Single Dispatch"
    (async done
           (let [e   (a/chan 1)
                 v   (sut/from-chan e)
                 sub (rx/on-value v #(is (= % 10)))]
             (a/put! e 10)
             (done))))

  (testing "Multi Dispatch"
    (async done
           (let [e (a/chan 1)
                 v (sut/from-chan e)
                 sub (rx/on-value v #(is (<= % 10)))]
             (doseq [x [1 2 3 10]]
               (a/put! e x))
             (done)))))
