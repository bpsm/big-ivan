;;; -*- mode: clojure ; coding: utf-8 -*-
;;; (c) 2012 Ben Smith-Mannschott -- Distributed under the Eclipse Public License

(ns big-ivan.bic
  "Functions which validate, parse and construct BIC strings.")


(defn bic?
  "Determine if s is syntactically a valid BIC, returning s or nil.

A BIC is a string that begins with 6 uppercase ascii letters, followed
by 2 or 5 uppercase letters or digits."
  [s]
  (when (and  (string? s)
              (re-matches #"[A-Z]{4}[A-Z]{2}[A-Z0-9]{2}(?:[A-Z0-9]{3})?" s))
    s))


(defn institution-code
  "Extract the four letter institution code form bic. bic must be valid."
  [^String bic]
  {:pre [(bic? bic)]
   :post [(re-matches #"[A-Z]{4}" %)]}
  (.substring bic 0 4))


(defn country-code
  "Extract the two-letter ISO-3166 country code from bic. bic must be valid."
  [^String bic]
  {:pre [(bic? bic)]
   :post [(re-matches #"[A-Z]{2}" %)]}
  (.substring bic 4 6))


(defn location-code
  "Extract the two-character location code from bic. bic must be valid."
  [^String bic]
  {:pre [(bic? bic)]
   :post [(re-matches #"[0-9A-Z]{2}" %)]}
  (.substring bic 6 8))


(defn branch-code
  "Extract the optional three-character branch code from bic. bic must be valid."
  [^String bic]
  {:pre [(bic? bic)]
   :post [(or (nil? %) (re-matches #"[0-9A-Z]{3}" %))]}
  (when (= 11 (count bic))
    (.substring bic 8 11)))


(defn bic
  "Construct a valid BIC given institution, country, location and an optional
branch. 

institution-code: extactly 4 upper-case ascii letters.
country-code: a 2 letter ISO-3166 code.
location-code: exactly 2 letters or digits.
branch: nil or emtpy or exactly 3 letters or digits.

The single-argument version of this function returns its argument,
which must satisfy bic?."
  ([s]
     {:pre [(bic? s)]}
     s)
  ([institution-code country-code location-code]
     (bic institution-code country-code location-code nil))
  ([institution-code country-code location-code branch-code]
     {:pre [(re-matches #"[A-Z]{4}" institution-code)
            (re-matches #"[A-Z]{2}" country-code)
            (re-matches #"[A-Z0-9]{2}" location-code)
            (or (nil? branch-code)
                (empty? branch-code)
                (re-matches #"[A-Z0-9]{3}" branch-code))]
      :post [(bic? %)]}
     (str institution-code country-code location-code (or branch-code ""))))