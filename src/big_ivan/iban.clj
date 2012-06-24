(ns big-ivan.iban
  (:require [clojure.string :as string]))

(def ^:private generic-iban-re #"[A-Z]{2}[0-9]{2}[A-Za-z0-9]{0,30}")
(def ^:private expandable-re #"[A-Za-z0-9]*")
(def ^:private only-digits-re #"[0-9]*")

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

(defn iban? [s]
  (and  (string? s)
        (re-matches generic-iban-re s)
        (check? s)))

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

