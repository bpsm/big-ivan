(ns big-ivan.iban.check
    (:require [clojure.string :as string]))

(def letter->digits
  "Translate the letters A-Z, a-z to integers 10-36 case insensitively.
Input is a string containing a single letter. Result is a sting of two digits."
  {"A" "10", "a" "10",
   "B" "11", "b" "11",
   "C" "12", "c" "12",
   "D" "13", "d" "13",
   "E" "14", "e" "14",
   "F" "15", "f" "15",
   "G" "16", "g" "16",
   "H" "17", "h" "17",
   "I" "18", "i" "18",
   "J" "19", "j" "19",
   "K" "20", "k" "20",
   "L" "21", "l" "21",
   "M" "22", "m" "22",
   "N" "23", "n" "23",
   "O" "24", "o" "24",
   "P" "25", "p" "25",
   "Q" "26", "q" "26",
   "R" "27", "r" "27",
   "S" "28", "s" "28",
   "T" "29", "t" "29",
   "U" "30", "u" "30",
   "V" "31", "v" "31",
   "W" "32", "w" "32",
   "X" "33", "x" "33",
   "Y" "34", "y" "34",
   "Z" "35", "z" "35"})

(defn letters->digits
  "Expand each ASCII letter in s into two digits as per letter->digits.
s must be a string containing only letters and digits."
  [s]
  {:pre [(string? s) (re-matches #"[A-Za-z0-9]*" s)]
   :post [(re-matches #"[0-9]*" %) (<= (count s) (count %))]}
  (string/replace s #"[A-Za-z]" letter->digits))

(defn check?
  "Are the check digits in s correct according to ISO-13616?
Check digits can only be verified if s is a string of at least length
4 consisting of exclusively of letters and digits. False is returned
if s does not meet these requirements."
  [^String s]
  (and (string? s)
       (re-matches #"[A-Za-z0-9]{4,}" s)
       (-> (str (.substring s 4) (.substring s 0 4))
           letters->digits
           bigint
           (mod 97)
           (= 1))))

(defn set-check
  "Compute and insert check digits for indexes 2 and 3 of string s
such that the result is consitent for check? s must be a string which
begins with two upper case letters followed by two digits followed by
any number of letters or digits."
  [^String s]
  {:pre [(re-matches #"[A-Z]{2}[0-9]{2}[A-Za-z0-9]*" s)]
   :post [(check? %)]}
  (let [cc (.substring s 0 2)
        bban (.substring s 4)
        ck (- 98 (int (mod (bigint
                            (letters->digits
                             (str bban cc "00")))
                           97)))]
    (str cc (format "%02d" ck) bban)))
