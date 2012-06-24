(ns big-ivan.iban
  (:require [clojure.string :as string]))

(def ^:private generic-iban-re #"[A-Z]{2}[0-9]{2}[A-Za-z0-9]{0,30}")
(def ^:private expandable-re #"[A-Za-z0-9]*")
(def ^:private only-digits-re #"[0-9]*")

(def bban-format-map
  "Map from two letter country code to BBAN format.

Each IBAN begins with a two-letter ISO-3166 country code. The two letter country
code determines the format of the BBAN, the portion of the IBAN that follows
after the country code and check digits.

The format notation is described by the IBAN standard. The specifics below
are from the _IBAN Registry_ Version 36 (June 2012)."
  {"AL"  "8!n16!c"
   "AD"  "4!n4!n12!c"
   "AT"  "5!n11!n"
   "AZ"  "4!a20!c"
   "BH"  "4!a14!c"
   "BE"  "3!n7!n2!n"
   "BA"  "3!n3!n8!n2!n"
   "BG"  "4!a4!n2!n8!c"
   "CR"  "3!n14!n"
   "HR"  "7!n10!n"
   "CY"  "3!n5!n16!c"
   "CZ"  "4!n6!n10!n"
   ;; Denmark is assigned 3 ISO-3166 country codes: DK, FO, GL
   "DK"  "4!n9!n1!n"
   "FO"  "4!n9!n1!n"
   "GL"  "4!n9!n1!n"
   "DO"  "4!c20!n"
   "EE"  "2!n2!n11!n1!n"
   "FI"  "6!n7!n1!n"
   ;; FR is also used for French republic subdivision country
   ;; codes: GF, GP, MQ, RE, PF, TF, YT, NC, PM and WF.
   "FR"  "5!n5!n11!c2!n"
   "GE"  "2!a16!n"
   "DE"  "8!n10!n"
   "GI"  "4!a15!c"
   "GR"  "3!n4!n16!c"
   "GT"  "4!c20!c"
   "HU"  "3!n4!n1!n15!n1!n"
   "IS"  "4!n2!n6!n10!n"
   "IE"  "4!a6!n8!n"
   "IL"  "3!n3!n13!n"
   "IT"  "1!a5!n5!n12!c"
   "KZ"  "3!n13!c"
   "KW"  "4!a22!c"
   "LV"  "4!a13!c"
   "LB"  "4!n20!c"
   "LI"  "5!n12!c"
   "LT"  "5!n11!n"
   "LU"  "3!n13!c"
   "MK"  "3!n10!c2!n"
   "MT"  "4!a5!n18!c"
   "MR"  "5!n5!n11!n2!n"
   "MU"  "4!a2!n2!n12!n3!n3!a"
   "MD"  "2!c18!c"
   "MC"  "5!n5!n11!c2!n"
   "ME"  "3!n13!n2!n"
   "NL"  "4!a10!n"
   "NO"  "4!n6!n1!n"
   "PK"  "4!a16!c"
   "PL"  "8!n16!n"
   "PT"  "4!n4!n11!n2!n"
   "RO"  "4!a16!c"
   "SM"  "1!a5!n5!n12!c"
   "SA"  "2!n18!c"
   "RS"  "3!n13!n2!n"
   "SK"  "4!n6!n10!n"
   "SI"  "5!n8!n2!n"
   "ES"  "4!n4!n1!n1!n10!n"
   "SE"  "3!n16!n1!n"
   "CH"  "5!n12!c"
   "TN"  "2!n3!n13!n2!n"
   "TR"  "5!n1!c16!c"
   "AE"  "3!n16!n"
   "GB"  "4!a6!n8!n"
   "VG"  "4!a16!n"
   })

(def bban-fmt->re-str
  "Translate a BBAN format specification as used by the standard into
a String containing an equivalent Java compatible regular expression."
  (let [character-class {"c" "[A-Za-z0-9]", "n" "[0-9]", "a" "[A-Z]", "e" " "}]
    (fn [bban-fmt]
      (string/replace
       bban-fmt #"([0-9]+)([!])?([cnae])"
       (fn [[_ length fixed? c]]
         (str (character-class c) "{" (if fixed? "" "0,") length "}"))))))

(def iban-regex*
  "Create a single regular expression Pattern which will recognize any of the
IBAN variants described by the iban-formats map passed as parameter."
  (fn [iban-formats]
      (->> (for [[country-code bban-fmt] (sort iban-formats)]
             (str country-code "[0-9]{2}" (bban-fmt->re-str bban-fmt)))
           (interpose "|")
           (apply str)
           re-pattern)))

(defmacro iban-regex
  "Use this macro to perform the conversion from IBAN notation to regular
expression Pattern at compile time."
  []
  (iban-regex* bban-format-map))

(defn srotl
  "Rotate the string s to the left by d positions.
  (srotl \"abcdef\" 2) => \"cdefab\"."
  [^String s d]
  {:pre [(string? s)]
   :post [(= (count s) (count %))]}
  (let [n (count s)
        d (rem (if (pos? d) d (+ n d)) n)]
    (str (.substring s d) (.substring s 0 d))))

(def letter->digits
  (let [a (int \a)
        A (int \A)]
    (fn [m]
      (let [c (-> m first int)]
        (-> c
            (- (int (if (<= a c) a A)))
            (+ 10)
            str)))))

(defn expand-letters-to-digits
  [s]
  {:pre [(string? s) (re-matches expandable-re s)]
   :post [#_(re-matches only-digits-re s)]}
  (string/replace s #"[A-Za-z]" letter->digits))

(defn check?
  [s]
  (-> s (srotl 4) expand-letters-to-digits bigint (mod 97) (= 1)))

(defn set-check
  [^String s]
  {:pre [(re-matches generic-iban-re s)]
   :post [(check? %)]}
  (let [cc (.substring s 0 2)
        bban (.substring s 4)
        ck (- 98 (int (mod (bigint
                            (expand-letters-to-digits
                             (str bban cc "00")))
                           97)))]
    (str cc (format "%02d" ck) bban)))

(def iban?
  (let [iban-re (iban-regex)]
    (fn [s]
      (and  (string? s)
            (re-matches iban-re s)
            (check? s)
            ))))

(defn remove-spaces
  [iban]
  (string/replace iban " " ""))

(defn add-spaces
  [iban]
  (string/replace (remove-spaces iban) #"[^ ]{4}(?!$)" #(str % \space)))

(defn country-code? [s]
  (and (string? s) (re-matches #"[A-Z]{2}" s)))

(defn iban
  "Construct an iban from a single string or from a country code and a bban."
  ([s]
     {:pre [(string? s)]
      :post [(or (nil? %) (iban? %))]}
     (let [s* (remove-spaces s)]
       (when (iban? s*) s*)))
  ([country-code bban]
     {:post [(iban? %)]}
     (set-check (str country-code "00" bban))))

(defn country-code
  "Return the country code of iban. iban must be valid."
  [^String iban]
  {:pre [(iban? iban)]
   :post [(country-code? %)]}
  (.substring iban 0 2))

(defn bban
  "Return the BBAN portion of iban. iban must be valid."
  [^String iban]
  {:pre [(iban? iban)]}
  (.substring iban 4))

