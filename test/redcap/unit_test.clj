(ns redcap.unit-test
  (:use code.test)
  (:require [redcap.unit :as unit]
            [redcap.meta :as meta]
            [std.lib :as h]))

^{:refer redcap.unit/unit-invoke :added "0.1"}
(fact "invokes a redcap api unit"
  ^:hidden
  
  (unit/unit-invoke {})
  => {})

^{:refer redcap.unit/unit-display :added "0.1"}
(fact "displays a unit"
  ^:hidden
  
  (unit/unit-display {:input {:category :arm
                              :tag :export}})
  => [:arm :export])

^{:refer redcap.unit/unit-string :added "0.1"}
(fact "creates a unit string"
  ^:hidden
  
  (unit/unit-string {:input {:category :arm
                             :tag :export}})
  => "#redcap.unit[:arm :export]")

^{:refer redcap.unit/generate-unit-spec :added "0.1"}
(fact "generates a unit spec"
  ^:hidden
  
  (unit/generate-unit-spec
   {:category :events
    :tag      :export
    :action   "export"
    :content "event"
    :params {:arms  {:type [:vector :string]}}})
  => [:map
      [:token [:string {:min 32, :max 32}]]
      [:content [:enum "event"]]
      [:action [:enum "export"]]
      [:format {:optional true} [:enum #:error{:message "should be csv|json|xml"} "csv" "json" "xml"]]
      [:returnFormat {:optional true} [:enum #:error{:message "should be csv|json|xml"} "csv" "json" "xml"]]
      [:arms {:optional true} [:vector :string]]]
  
  (unit/generate-unit-spec
   (-> (into {} meta/+api+) 
       (get-in [:record :export])
       (assoc :category :record
              :tag :export
              :action "export")))
  => [:map [:token [:string {:min 32, :max 32}]]
      [:content [:enum "record"]]
      [:action [:enum "export"]]
      [:format {:optional true} [:enum #:error{:message "should be csv|json|xml"} "csv" "json" "xml"]]
      [:returnFormat {:optional true} [:enum #:error{:message "should be csv|json|xml"} "csv" "json" "xml"]]
      [:csvDelimiter {:optional true} [:enum #:error{:message "should be , or ; or | or ^ or \t"} "," ";" "|" "^" "\t"]]
      [:dateRangeBegin {:optional true} inst?]
      [:dateRangeEnd {:optional true} inst?]
      [:events {:optional true} [:vector :string]]
      [:exportCheckboxLabel {:optional true} :boolean]
      [:exportDataAccessGroups {:optional true} :boolean]
      [:exportSurveyFields {:optional true} :boolean]
      [:fields {:optional true} [:vector :string]]
      [:forms {:optional true} [:vector :string]]
      [:rawOrLabel {:optional true} [:enum #:error{:message "should be raw|label"} "raw" "label"]]
      [:rawOrLabelHeaders {:optional true} [:enum #:error{:message "should be raw|label"} "raw" "label"]]
      [:records {:optional true} [:vector :string]]
      [:type {} [:enum #:error{:message "should be flat|eav|record"} "flat" "eav" "record"]]])
  
^{:refer redcap.unit/generate-unit-defaults :added "0.1"}
(fact "generate unit defaults"
  ^:hidden
  
  (unit/generate-unit-defaults
   (-> (into {} meta/+api+) 
       (get-in [:record :export])
       (assoc :category :record
              :tag :export
              :action "export")))
  => {:action "export", :content "record"}

  (unit/generate-unit-defaults
   (-> (into {} meta/+api+) 
       (get-in [:version :export])
       (assoc :category :version
              :tag :export
              :action "export")))
  => {:action "export", :content "version"})

^{:refer redcap.unit/generate-unit :added "0.1"}
(fact "creates a unit"
  ^:hidden
  
  (->> (unit/generate-unit
        (-> (into {} meta/+api+) 
            (get-in [:version :export])
            (assoc :category :version
                   :tag :export
                   :action "export")))
       (into {}))
  =>
  (contains
   {:input    map?
    :defaults {:action "export", :content "version"},
    :spec [:map
           [:token [:string {:min 32, :max 32}]]
           [:content [:enum "version"]] [:action [:enum "export"]]
           [:format {:optional true} [:enum #:error{:message "should be csv|json|xml"} "csv" "json" "xml"]]
           [:returnFormat {:optional true} [:enum #:error{:message "should be csv|json|xml"} "csv" "json" "xml"]]]}))
