;;; -*- mode: clojure ; coding: utf-8 -*-
;;; (c) 2012 Ben Smith-Mannschott -- Distributed under the Eclipse Public License

(ns big-ivan.bic-test
  (:use clojure.test
        big-ivan.bic))

(deftest syntactic-validity
  (testing "syntactically valid BICs"
    (are [s] (identical? s (bic? s))
         "AAAAAAAA"
         "AAAAAAAAAAA"
         "AAAAAA11"
         "AAAAAA11111"
         "DEUTDEFF"
         "DSBACNBXSHA"))
  (testing "syntactically invalid BICs"
    (are [s] (not (bic? s))
         ""
         "A"
         "AA"
         "AAA"
         "AAAA"
         "AAAAA"
         "AAAAAA"
         "AAAAAAA"
         "AAAAAAAAA"
         "AAAAAAAAAA"
         "clearly not a bic"))
  (testing "non-strings"
    (are [s] (not (bic? s))
         nil 1 1.1 \x true false)))

(deftest parsing
  (are [f s] (= s (f "DEUTDEFF"))
       institution-code "DEUT"
       country-code "DE"
       location-code "FF"
       branch-code nil)
  (is (= "007" (branch-code "DEUTDEFF007"))))

(deftest construction
  (is (= "DEUTDEFF007" (bic "DEUT" "DE" "FF" "007")))
  (is (= "DEUTDEFF" (bic "DEUT" "DE" "FF" nil)))
  (is (= "DEUTDEFF" (bic "DEUT" "DE" "FF" "")))
  (is (= "DEUTDEFF" (bic "DEUT" "DE" "FF"))))

(deftest bic?-versus-bic
  (testing "bic is strict"
    (is (thrown? AssertionError (bic "DEU" "DE" "FF")))
    (is (thrown? AssertionError (bic "DEU9DEFF"))))
  (testing "bic? returns nil or is identity"
    (is (nil? (bic? nil)))
    (is (nil? (bic? "NOT-A-BIC")))
    (is (nil? (bic? false)))
    (let [x "DEUTDEFF"]
      (is (identical? x (bic? x))))))