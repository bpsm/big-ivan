(ns big-ivan.iban-test
  (:use clojure.test
        big-ivan.iban))

(deftest test-srotl
  (are [s d x] (= x (srotl s d))
       "abcdef"  0 "abcdef"
       "abcdef"  2 "cdefab"
       "abcdef" -2 "efabcd"
       "abcdef" 5 "fabcde"
       "abcdef" 6 "abcdef"
       "abcdef" -6 "abcdef"
       "abcdef" -5 "bcdefa"
       ))

(deftest test-letter->digits
  (let [digits-for-a-to-z (map str (range 10 36))]
    (are [s] (= digits-for-a-to-z (map (comp letter->digits str) s))
         "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
         "abcdefghijklmnopqrstuvwxyz")))

(deftest test-iban?
  (testing "nil is not an iban"
    (is (not (iban? nil))))
  (testing "only strings can be iban"
    (are [x] (not (iban? x))
         1 true 1.0 1N 1.0M \return))
  (testing "examples of valid ibans in 'electronic' format"
    (is (iban? "SA0380000000608010167519")))
  (testing "various malformed ibans"
    (are [x] (not (iban? x))
         "X"
         "X2"
         "XX9"
         "XX9X"
         "XX99"
         "NO0086011117947"              ; bad checksum
         ;;         "NO698601111794"               ; too short
         ;;         "NO37860111179470"             ; too long
         ;;         "NO95X6011117947"              ; malformed bban
         "XX00^&^@(#*&)@)")))

(deftest test-formatting
  (testing "remove-spaces does what it says"
    (are [x s] (= x (remove-spaces s))
         "" ""
         "" "    "
         "ab" " a  b "
         "12345678" "1234 5678"))
  (testing "add-spaces produce space-separated groups of four non-space chars."
    (are [x s] (= x (add-spaces s))
         "" ""
         "abcd" "a b cd"
         "abcd efg" "a bcdefg")))

(def valid-iban-examples
  ["AD12 0001 2030 2003 5910 0100"
   "AE07 0331 2345 6789 0123 456"
   "AL47 2121 1009 0000 0002 3569 8741"
   "AT61 1904 3002 3457 3201"
   "AZ21 NABZ 0000 0000 1370 1000 1944"
   "BA39 1290 0794 0102 8494"
   "BE68 5390 0754 7034"
   "BG80 BNBG 9661 1020 3456 78"
   "BH67 BMAG 0000 1299 1234 56"
   "CH93 0076 2011 6238 5295 7"
   "CR05 1520 2001 0262 8406 6"
   "CY17 0020 0128 0000 0012 0052 7600"
   "CZ65 0800 0000 1920 0014 5399"
   "CZ94 5500 0000 0010 1103 8930"
   "DE89 3704 0044 0532 0130 00"
   "DK50 0040 0440 1162 43"
   "DO28 BAGR 0000 0001 2124 5361 1324"
   "EE38 2200 2210 2014 5685"
   "ES91 2100 0418 4502 0005 1332"
   "FI21 1234 5600 0007 85"
   "FO62 6460 0001 6316 34"
   "FR14 2004 1010 0505 0001 3M02 606"
   "GB29 NWBK 6016 1331 9268 19"
   "GE29 NB00 0000 0101 9049 17"
   "GI75 NWBK 0000 0000 7099 453"
   "GL89 6471 0001 0002 06"
   "GR16 0110 1250 0000 0001 2300 695"
   "GT82 TRAJ 0102 0000 0012 1002 9690"
   "HR12 1001 0051 8630 0016 0"
   "HU42 1177 3016 1111 1018 0000 0000"
   "IE29 AIBK 9311 5212 3456 78"
   "IL62 0108 0000 0009 9999 999"
   "IS14 0159 2600 7654 5510 7303 39"
   "IT60 X054 2811 1010 0000 0123 456"
   "KW81 CBKU 0000 0000 0000 1234 5601 01"
   "KZ86 125K ZT50 0410 0100"
   "LB62 0999 0000 0001 0019 0122 9114"
   "LI21 0881 0000 2324 013A A"
   "LT12 1000 0111 0100 1000"
   "LU28 0019 4006 4475 0000"
   "LV80 BANK 0000 4351 9500 1"
   "MD24 AG00 0225 1000 1310 4168"
   "ME25 5050 0001 2345 6789 51"
   "MK07 2501 2000 0058 984"
   "MR13 0002 0001 0100 0012 3456 753"
   "MT84 MALT 0110 0001 2345 MTLC AST0 01S"
   "MU17 BOMM 0101 1010 3030 0200 000M UR"
   "NL91 ABNA 0417 1643 00"
   "NO93 8601 1117 947"
   "PK36 SCBL 0000 0011 2345 6702"
   "PL61 1090 1014 0000 0712 1981 2874"
   "PT50 0002 0123 1234 5678 9015 4"
   "RO49 AAAA 1B31 0075 9384 0000"
   "RS35 2600 0560 1001 6113 79"
   "SA03 8000 0000 6080 1016 7519"
   "SE45 5000 0000 0583 9825 7466"
   "SI56 1910 0000 0123 438"
   "SK31 1200 0000 1987 4263 7541"
   "SM86 U032 2509 8000 0000 0270 100"
   "TN59 1000 6035 1835 9847 8831"
   "TR33 0006 1005 1978 6457 8413 26"
   "VG96 VPVG 0000 0123 4567 8901"])

(deftest test-valid-iban-examples
  (doseq [x valid-iban-examples]
    (is (iban? (remove-spaces x)))))

(deftest iban-construction
  (testing "can construct from 'printed' format"
    (doseq [x valid-iban-examples]
      (is (iban? (iban x)))))
  (testing "can construct out of components"
    (doseq [x (map iban valid-iban-examples)]
      (is (= x (iban (country-code x) (bban x)))))))