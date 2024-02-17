(ns redcap.core-test
  (:use code.test)
  (:require [redcap.core :as rc]
            [net.http :as http]
            [std.string :as str]))

(def +md0+
  [{"form_name" "form_2",
    "matrix_group_name" "",
    "section_header" "",
    "text_validation_max" "200",
    "text_validation_type_or_show_slider_number" "integer",
    "field_note" "",
    "custom_alignment" "",
    "required_field" "y",
    "field_annotation" "",
    "branching_logic" "",
    "field_label" "Age",
    "matrix_ranking" "",
    "identifier" "",
    "field_type" "text",
    "question_number" "",
    "select_choices_or_calculations" "",
    "text_validation_min" "0",
    "field_name" "age"}])

(def +md0-str+
  (str/|
   "field_name,form_name,section_header,field_type,field_label,select_choices_or_calculations,field_note,text_validation_type_or_show_slider_number,text_validation_min,text_validation_max,identifier,branching_logic,required_field,custom_alignment,question_number,matrix_group_name,matrix_ranking,field_annotation"
   "age,form_2,,text,Age,,,integer,0,200,,,y,,,,,\n"))

^{:refer redcap.core/set-site-opts :added "0.1"}
(fact "sets the default site opts"
  ^:hidden

  (rc/set-site-opts {:url   "https://redcapdemo.vanderbilt.edu/api/"
                     :token "0CCE5D579105060CB418DB05FCF13045"})
  => map?
  
  (rc/set-site-opts {:url   "https://redcapdemo.vanderbilt.edu/api/"
                     :token "0CCE5D579105060CB418DB05FCF13045"
                     :return :raw})
  => map?)

^{:refer redcap.core/with-site-opts :added "0.1"}
(fact "binds the actual site opts"
  ^:hidden
  
  (rc/with-site-opts [{:url   "https://CHANGED.COM"
                       :token ""}]
    rc/*site-opts*)
  => {:url "https://CHANGED.COM", :token ""})

^{:refer redcap.core/parse-body :added "0.1"}
(fact "parses the raw return"
  ^:hidden

  (rc/parse-body
   {:body "14.0.10",
    :headers {:content-type "text/csv; charset=utf-8"}
    :status 200})
  => "14.0.10"
  
  (rc/parse-body
   {:body "'14.0.10'",
    :headers {:content-type "application/json; charset=utf-8"}
    :status 200})
  => "14.0.10")

^{:refer redcap.core/call-api :added "0.1"}
(fact "calls the redcap api"
  ^:hidden
  
  (redcap.core/call-api
   (:export-project-info rc/+units+)
   {}
   {:url   "https://redcapdemo.vanderbilt.edu/api/"
    :token "0CCE5D579105060CB418DB05FCF13045"
    :return :raw})
  => map?

  (http/post "https://redcapdemo.vanderbilt.edu/api/",
             {:headers {"Content-Type" "application/x-www-form-urlencoded",
                        "Accept" "application/json"},
              :body "token=0CCE5D579105060CB418DB05FCF13045&content=version"})
  => map?)

^{:refer redcap.core/create-api-form :added "0.1"}
(fact "creates an api form"
  ^:hidden
  
  (rc/create-api-form (last (sort rc/+units+)))
  => '(clojure.core/defn switch-dag
        [& [{:keys [dag] :as params}
            {:keys [url token return],
             :as site-opts}]]
        (redcap.core/call-api
         (clojure.core/get redcap.core/+units+ :switch-dag)
         params
         site-opts))

  rc/+functions+
  => [#'redcap.core/create-storage-folder
      #'redcap.core/delete-arm
      #'redcap.core/delete-dag
      #'redcap.core/delete-event
      #'redcap.core/delete-file
      #'redcap.core/delete-record
      #'redcap.core/delete-storage
      #'redcap.core/delete-user
      #'redcap.core/delete-user-role
      #'redcap.core/export-arm
      #'redcap.core/export-dag
      #'redcap.core/export-dag-assignment
      #'redcap.core/export-event
      #'redcap.core/export-field-name
      #'redcap.core/export-file
      #'redcap.core/export-instrument
      #'redcap.core/export-instrument-event-mappings
      #'redcap.core/export-instrument-pdf
      #'redcap.core/export-instrument-repeating
      #'redcap.core/export-logging
      #'redcap.core/export-metadata
      #'redcap.core/export-project-info
      #'redcap.core/export-project-xml
      #'redcap.core/export-record
      #'redcap.core/export-record-next-name
      #'redcap.core/export-report
      #'redcap.core/export-storage
      #'redcap.core/export-survey-link
      #'redcap.core/export-survey-participants
      #'redcap.core/export-survey-queue-link
      #'redcap.core/export-survey-return-code
      #'redcap.core/export-user
      #'redcap.core/export-user-role
      #'redcap.core/export-user-role-mapping
      #'redcap.core/export-version
      #'redcap.core/import-arm
      #'redcap.core/import-dag
      #'redcap.core/import-dag-assignment
      #'redcap.core/import-event
      #'redcap.core/import-file
      #'redcap.core/import-instrument-event-mappings
      #'redcap.core/import-instrument-repeating
      #'redcap.core/import-metadata
      #'redcap.core/import-project-info
      #'redcap.core/import-record
      #'redcap.core/import-storage
      #'redcap.core/import-user
      #'redcap.core/import-user-role
      #'redcap.core/import-user-role-mapping
      #'redcap.core/list-storage
      #'redcap.core/rename-record
      #'redcap.core/switch-dag])
  
^{:refer redcap.core/metadata->csv :added "0.1"}
(fact "transforms a vector of instruments into csv format"
  ^:hidden

  (rc/metadata->csv +md0+)
  => +md0-str+)

^{:refer redcap.core/csv->metadata :added "0.1"}
(fact "transforms csv into vector of instruments"
  ^:hidden

  (rc/csv->metadata
   (rc/metadata->csv
    +md0+))
  => +md0+)
  
^{:refer redcap.core/export-pipeline :added "0.1"}
(fact "exports pipeline from redcap")

^{:refer redcap.core/import-pipeline :added "0.1"}
(fact "imports pipeline into redcap")
  
^{:refer redcap.core/get-forms :added "0.1"}
(fact "gets all form names from the pipeline"
  ^:hidden

  (rc/get-forms +md0+)
  => ["form_2"])

^{:refer redcap.core/get-fields :added "0.1"}
(fact "gets all field names from the pipeline"
  ^:hidden

  (rc/get-fields +md0+)
  => '(["form_2" ("age")]))
