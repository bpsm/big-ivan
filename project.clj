(defproject org.clojars.bpsm/big-ivan "0.1.0-SNAPSHOT"
  :description "A validating parser for BIC and IBAN"
  :url "http://github.com/bpsm/big-ivan"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]]

  :html5-docs-docs-dir "apidocs"
  :html5-docs-name "Big Ivan"
  :html5-docs-ns-includes #"^big-ivan.[^.]+$"
  :html5-docs-repository-url "https://github.com/bpsm/big-ivan/blob/master")


;; html-5-docs configurables
;; -------------------------
;; :html5-docs-docs-dir            ;; Optional: where to put the HTML files.  Defaults to "docs".
;; :html5-docs-name "FooBar"       ;; Optional: if specified, overrides the project :name
;; :html5-docs-page-title          ;; Optional: defaults to "<ProjectName> API Documentation"
;; :html5-docs-source-path         ;; Optional: overrides :source-path, handy if you want to document only
;;                                 ;; src/foo, but not src/bar, src/baz, ...
;; :html5-docs-ns-includes         ;; Required: A regex that is matched against namespace names.  If your
;;                                 ;; all your project's namespaces start with "foo.bar", then it should
;;                                 ;; be something like #"^foo\.bar.*".
;; :html5-docs-ns-excludes         ;; Optional: A regex that is matched against namespace names.  For example,
;;                                 ;; you probably want to set it to #".*\.test\..*" to exclude your test
;;                                 ;; namespaces.
;; :html5-docs-repository-url      ;; Required: The URL of your repository, e.g., for github projects it is
