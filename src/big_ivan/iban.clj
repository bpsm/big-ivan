(ns big-ivan.iban
  (:require (clojure [string :as string])
            (big-ivan.iban [check :as check]
                           [registry :as registry])))

(defn iban?
  "Return true if s is a valid IBAN: a string in IBAN 'electronic'
format with consistent check digits."
  [s]
  (and  (string? s)
        (re-matches (registry/iban-pattern) s)
        (check/check? s)))

(defn remove-spaces
  "Return iban with any spaces removed.
This reverts an IBAN in 'printed' representation to the standard's
'electronic' representation.  Only IBANs in electronic representation
are valid IBANs with respect to iban?."
  [iban]
  (string/replace iban " " ""))

(defn add-spaces
  "Return iban with spaces separating non-space characters into groups of four.
This provides the IBAN standard's 'printed' representation."
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
     (check/set-check (str country-code "00" bban))))

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

