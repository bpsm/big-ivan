(ns big-ivan.bic-test
  (:use clojure.test
        big-ivan.bic))

(deftest syntactic-validity
  (testing "syntactically valid BICs"
    (are [s] (bic? s)
         "AAAAAAAA"
         "AAAAAAAAAAA"
         "AAAAAA11"
         "AAAAAA11111"
         "DEUTDEFF"
         "DSBACNBXSHA"))
  (testing "syntactically invalid BICs"
    (are [s] (not (bic? s))
         nil
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
         "clearly not a bic")))

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
  (is (= "DEUTDEFF" (bic "DEUT" "DE" "FF"))))