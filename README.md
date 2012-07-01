<!-- -*- mode: markdown ; coding: utf-8 -*- -->

# big-ivan

A library for validating, parsing and constructing BIC and IBAN values.

## Usage

### BIC

A few examples:

    (require '[big-ivan.bic :as bic])

    (bic/bic? "not-a-bic") => nil
    (bic/bic? "DEUTDEFF") => "DEUTDEFF"

    (def some-value "DEUTDEFF")
    (if-let [b (bic/bic? some-value)]
        (bic/country-code b))
    => "DE"

    (bic/bic "DEUT" "DE" "FF" "007")
    => "DEUTDEFF007"

    (bic/bic "$$$$" "DE" "FF" "007")
    => throws AssertionError

See also [docs for `big-ivan.bic`](http://bpsm.github.com/big-ivan/big-ivan.bic.html).

### IBAN

A few examples:

    (require '[big-ivan.iban :as iban])

    (iban/iban? :not-an-iban) => nil
    (iban/iban? "SA0380000000608010167519") => "SA0380000000608010167519"

    (iban/bban "SA0380000000608010167519") => "0000000608010167519"

    (iban/add-spaces "SA0380000000608010167519") 
    => "SA03 8000 0000 6080 1016 7519"

See also [docs for `big-ivan.iban`](http://bpsm.github.com/big-ivan/big-ivan.iban.html).


## License

Copyright © 2012 Ben Smith-Mannschott

Distributed under the Eclipse Public License, the same as Clojure.
