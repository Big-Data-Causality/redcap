(ns redcap.core-test
  (:use code.test)
  (:require [redcap.core :as rc]))

^{:refer redcap.core/set-site-opts :added "0.1"}
(fact "sets the default site opts"
  ^:hidden
  
  (rc/set-site-opts {:url   "https://redcapdemo.vanderbilt.edu/api/"
                     :token "0CCE5D579105060CB418DB05FCF13045"
                     :return :raw}))

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
  
  
  


(comment
  
  (rc/set-site-opts {:url   "https://redcapdemo.vanderbilt.edu/api/"
                     :token "0CCE5D579105060CB418DB05FCF13045"})
  (rc/import-metadata
   {:data []})
  => 0

  (rc/export-record)
  
  
  (rc/import-metadata
   {:data [{"form_name" "form_1",
            "matrix_group_name" "",
            "section_header" "",
            "text_validation_max" "",
            "text_validation_type_or_show_slider_number" "",
            "field_note" "",
            "custom_alignment" "",
            "required_field" "",
            "field_annotation" "",
            "branching_logic" "",
            "field_label" "Record ID",
            "matrix_ranking" "",
            "identifier" "",
            "field_type" "text",
            "question_number" "",
            "select_choices_or_calculations" "",
            "text_validation_min" "",
            "field_name" "record_id"}
           {"form_name" "form_1",
            "matrix_group_name" "",
            "section_header" "",
            "text_validation_max" "",
            "text_validation_type_or_show_slider_number" "",
            "field_note" "",
            "custom_alignment" "",
            "required_field" "y",
            "field_annotation" "",
            "branching_logic" "",
            "field_label" "Name",
            "matrix_ranking" "",
            "identifier" "",
            "field_type" "text",
            "question_number" "",
            "select_choices_or_calculations" "",
            "text_validation_min" "",
            "field_name" "name"}
           {"form_name" "form_2",
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
            "field_name" "age"}]})
  
  )


(comment

  (export-record
   {:url +demo-url+
    :token +demo-token+}
   {:format "xml"})
  
  
  
  (http/post "https://redcapdemo.vanderbilt.edu/api/",
             {:headers {"Content-Type" "application/x-www-form-urlencoded",
                        "Accept" "application/json"},
              :body "token=8A3B0ED30F7595CC2615A53E7597F0C7&content=version"}))

(comment
  {:url "https://redcapdemo.vanderbilt.edu/api/", :headers {"Content-Type" "application/json", "Accept" "application/json"}, :body "token=8A3B0ED30F7595CC2615A53E7597F0C7&&content=version"})
