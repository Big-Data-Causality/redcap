# redcap

[![Build Status](https://github.com/Big-Data-Causality/redcap/actions/workflows/clojure.yml/badge.svg)](https://github.com/Big-Data-Causality/redcap/actions)
[![Clojars Project](https://img.shields.io/clojars/v/com.bigdatacausality/redcap.svg)](https://clojars.org/com.bigdatacausality/redcap)

REDCap client library for Clojure

    
## Usage

#### All Functions

The following are a list of functions available in the `redcap.core` namespace

```clojure
create-storage-folder
delete-arm
delete-dag
delete-event
delete-file
delete-record
delete-storage
delete-user
delete-user-role
export-arm
export-dag
export-dag-assignment
export-event
export-field-name
export-file
export-instrument
export-instrument-event-mappings
export-instrument-pdf
export-instrument-repeating
export-logging
export-metadata
export-project-info
export-project-xml
export-record
export-record-next-name
export-report
export-storage
export-survey-link
export-survey-participants
export-survey-queue-link
export-survey-return-code
export-user
export-user-role
export-user-role-mapping
export-version
import-arm
import-dag
import-dag-assignment
import-event
import-file
import-instrument-event-mappings
import-instrument-repeating
import-metadata
import-project-info
import-record
import-storage
import-user
import-user-role
import-user-role-mapping
list-storage
rename-record
switch-dag
```

#### API Site Options

```clojure
(require '[redcap.core :as rc])

(rc/set-site-opts {:url   "https://redcapdemo.vanderbilt.edu/api/"
                   :token "<API TOKEN>"})
```

#### Project Info

```clojure
(require '[redcap.core :as rc])

(rc/export-project-info)
=> {"project_pi_lastname" "",
    "project_irb_number" "",
    "project_grant_number" "",
    "bypass_branching_erase_field_prompt" 0,
    "in_production" 0,
    "creation_time" "2024-02-05 21:39:47",
    "external_modules" "",
    "surveys_enabled" 0,
    "project_pi_firstname" "",
    "custom_record_label" "",
    "production_time" "",
    "has_repeating_instruments_or_events" 0,
    "project_title" "Test Project",
    "project_id" 55912,
    "ddp_enabled" 0,
    "missing_data_codes" "",
    "is_longitudinal" 0,
    "record_autonumbering_enabled" 1,
    "secondary_unique_field" "",
    "display_today_now_button" 1,
    "randomization_enabled" 0,
    "scheduling_enabled" 0,
    "purpose_other" "",
    "project_notes" "hello",
    "project_language" "English",
    "purpose" 0}
```

#### Project Metadata - Export

```clojure
(require '[redcap.core :as rc])

(rc/export-metadata)
=> [{"form_name" "form_1",
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
    ;;
    ;; ... MORE FIELDS ...
    ;; 
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
     "field_name" "age"}]
```

#### Project Metadata - Import

```clojure
(require '[redcap.core :as rc])


(rc/import-metadata
 {:format "csv"
  :data (slurp "sample.csv")}
 {:ignore true})
```

#### Project Records - Export

```clojure
(require '[redcap.core :as rc])

(rc/export-record)
;; => <EXPORTS ALL RECORDS>

(rc/export-record
 {:records ["1" "2" "3"]})
;; => <EXPORTS SELECTED RECORDS>
```

#### Project Records - Import

```clojure
(require '[redcap.core :as rc])

(rc/import-record
 {:data [{...}]})
;; => <IMPORTS ALL RECORDS>
```

#### Export Project

The entire project can be exported with the `export-project-xml` function

```clojure
(rc/export-project-xml)
=> 
[:odm
  {:description "Test Project",
   :creationdatetime "2024-02-06T03:55:05",
   :sourcesystemversion "14.0.10",
   :odmversion "1.3.1",
   :xmlns:xsi "http://www.w3.org/2001/XMLSchema-instance",
   :xmlns "http://www.cdisc.org/ns/odm/v1.3",
   :fileoid "000-00-0000",
   :asofdatetime "2024-02-06T03:55:05",
   :xsi:schemalocation
   "http://www.cdisc.org/ns/odm/v1.3 schema/odm/ODM1-3-1.xsd",
   :xmlns:ds "http://www.w3.org/2000/09/xmldsig#",
   :filetype "Snapshot",
   :xmlns:redcap "https://projectredcap.org",
   :sourcesystem "REDCap"}
  [:study
   {:oid "Project.TestProject"}
   [:globalvariables
    [:studyname "Test Project"]
    [:studydescription
     "This file contains the metadata, events, and data for REDCap project \"Test Project\"."]
    [:protocolname "Test Project"]
    [:redcap:recordautonumberingenabled "1"]
    [:redcap:customrecordlabel]
    [:redcap:secondaryuniquefield]
    [:redcap:schedulingenabled "0"]
    [:redcap:surveysenabled "0"]
    [:redcap:surveyinvitationemailfield]
    [:redcap:displaytodaynowbutton "1"]
    [:redcap:preventbranchingerasevalues "0"]
    [:redcap:requirechangereason "0"]
    [:redcap:datahistorypopup "1"]
    [:redcap:orderrecordsbyfield]
    [:redcap:mycapenabled "0"]
    [:redcap:purpose "0"]
    [:redcap:purposeother]
    [:redcap:projectnotes "hello"]
    [:redcap:surveyqueuecustomtext]
    [:redcap:surveyqueuehide "0"]
    [:redcap:surveyauthenabled "0"]
    [:redcap:surveyauthfield1]
    [:redcap:surveyauthevent1]
    [:redcap:surveyauthfield2]
    [:redcap:surveyauthevent2]
    [:redcap:surveyauthfield3]
    [:redcap:surveyauthevent3]
    [:redcap:surveyauthminfields]
    [:redcap:surveyauthapplyallsurveys "1"]
    [:redcap:surveyauthcustommessage]
    [:redcap:surveyauthfaillimit]
    [:redcap:surveyauthfailwindow]
    [:redcap:datamartprojectenabled "0"]
    [:redcap:datamartallowrepeatrevision "0"]
    [:redcap:datamartallowcreaterevision "0"]
    [:redcap:datamartcronenabled "0"]
    [:redcap:hidefilledforms "1"]
    [:redcap:hidedisabledforms "0"]
    [:redcap:formactivationsurveyautocontinue "0"]
    [:redcap:missingdatacodes]
    [:redcap:protectedemailmode "0"]
    [:redcap:protectedemailmodecustomtext]
    [:redcap:protectedemailmodetrigger "ALL"]
    [:redcap:protectedemailmodelogo]
    [:redcap:mycapprojectsgroup [:redcap:mycapprojects]]
    [:redcap:mycapthemesgroup [:redcap:mycapthemes]]
    [:redcap:multilanguagesettingsgroup
     [:redcap:multilanguagesettings
      {:settings
       "YToyMzp7czo3OiJ2ZXJzaW9uIjtzOjc6IjE0LjAuMTAiO3M6NToibGFuZ3MiO2E6MDp7fXM6OToicHJvamVjdElkIjtzOjU6IjU1OTEyIjtzOjE1OiJkZXNpZ25hdGVkRmllbGQiO3M6MDoiIjtzOjY6InN0YXR1cyI7czozOiJkZXYiO3M6NToiZGVidWciO2I6MDtzOjEzOiJhZG1pbi1lbmFibGVkIjtiOjA7czoxNDoiYWRtaW4tZGlzYWJsZWQiO2I6MDtzOjE1OiJhbGxvdy1mcm9tLWZpbGUiO2I6MDtzOjE4OiJhbGxvdy1mcm9tLXNjcmF0Y2giO2I6MDtzOjIxOiJvcHRpb25hbC1zdWJzY3JpcHRpb24iO2I6MDtzOjE4OiJhbGxvdy11aS1vdmVycmlkZXMiO2I6MDtzOjc6InJlZkxhbmciO3M6MDoiIjtzOjEyOiJmYWxsYmFja0xhbmciO3M6MDoiIjtzOjg6ImRpc2FibGVkIjtiOjA7czoyNToiaGlnaGxpZ2h0TWlzc2luZ0RhdGFlbnRyeSI7YjowO3M6MjI6ImhpZ2hsaWdodE1pc3NpbmdTdXJ2ZXkiO2I6MDtzOjIxOiJhdXRvRGV0ZWN0QnJvd3NlckxhbmciO2I6MDtzOjEyOiJhbGVydFNvdXJjZXMiO2E6MDp7fXM6MTA6ImFzaVNvdXJjZXMiO2E6MDp7fXM6MTQ6ImV4Y2x1ZGVkQWxlcnRzIjthOjA6e31zOjE0OiJleGNsdWRlZEZpZWxkcyI7YTowOnt9czoxNjoiZXhjbHVkZWRTZXR0aW5ncyI7YTowOnt9fQ=="}]]]
   [:metadataversion
    {:oid "Metadata.TestProject_2024-02-06_0355",
     :name "Test Project",
     :redcap:recordidfield "record_id"}
    [:formdef
     {:oid "Form.form_1",
      :name "Form 1",
      :repeating "No",
      :redcap:formname "form_1"}
     [:itemgroupref
      {:itemgroupoid "form_1.record_id", :mandatory "No"}]
     [:itemgroupref
      {:itemgroupoid "form_1.form_1_complete", :mandatory "No"}]]
    [:formdef
     {:oid "Form.form_2",
      :name "Form 2",
      :repeating "No",
      :redcap:formname "form_2"}
     [:itemgroupref {:itemgroupoid "form_2.age", :mandatory "No"}]
     [:itemgroupref
      {:itemgroupoid "form_2.form_2_complete", :mandatory "No"}]]
    [:itemgroupdef
     {:oid "form_1.record_id", :name "Form 1", :repeating "No"}
     [:itemref
      {:itemoid "record_id",
       :mandatory "No",
       :redcap:variable "record_id"}]
     [:itemref
      {:itemoid "name", :mandatory "Yes", :redcap:variable "name"}]]
    [:itemgroupdef
     {:oid "form_1.form_1_complete",
      :name "Form Status",
      :repeating "No"}
     [:itemref
      {:itemoid "form_1_complete",
       :mandatory "No",
       :redcap:variable "form_1_complete"}]]
    [:itemgroupdef
     {:oid "form_2.age", :name "Form 2", :repeating "No"}
     [:itemref
      {:itemoid "age", :mandatory "Yes", :redcap:variable "age"}]]
    [:itemgroupdef
     {:oid "form_2.form_2_complete",
      :name "Form Status",
      :repeating "No"}
     [:itemref
      {:itemoid "form_2_complete",
       :mandatory "No",
       :redcap:variable "form_2_complete"}]]
    [:itemdef
     {:oid "record_id",
      :name "record_id",
      :datatype "text",
      :length "999",
      :redcap:variable "record_id",
      :redcap:fieldtype "text"}
     [:question [:translatedtext "Record ID"]]]
    [:itemdef
     {:oid "name",
      :name "name",
      :datatype "text",
      :length "999",
      :redcap:variable "name",
      :redcap:fieldtype "text",
      :redcap:requiredfield "y"}
     [:question [:translatedtext "Name"]]]
    [:itemdef
     {:oid "form_1_complete",
      :name "form_1_complete",
      :datatype "text",
      :length "1",
      :redcap:variable "form_1_complete",
      :redcap:fieldtype "select",
      :redcap:sectionheader "Form Status"}
     [:question [:translatedtext "Complete?"]]
     [:codelistref {:codelistoid "form_1_complete.choices"}]]
    [:itemdef
     {:oid "age",
      :name "age",
      :datatype "integer",
      :length "999",
      :redcap:variable "age",
      :redcap:fieldtype "text",
      :redcap:textvalidationtype "int",
      :redcap:requiredfield "y"}
     [:question [:translatedtext "Age"]]
     [:rangecheck
      {:comparator "GE", :softhard "Soft"}
      [:checkvalue "0"]
      [:errormessage
       [:translatedtext
        "The value you provided is outside the suggested range (0 - 200). This value is admissible, but you may wish to double check it."]]]
     [:rangecheck
      {:comparator "LE", :softhard "Soft"}
      [:checkvalue "200"]
      [:errormessage
       [:translatedtext
        "The value you provided is outside the suggested range (0 - 200). This value is admissible, but you may wish to double check it."]]]]
    [:itemdef
     {:oid "form_2_complete",
      :name "form_2_complete",
      :datatype "text",
      :length "1",
      :redcap:variable "form_2_complete",
      :redcap:fieldtype "select",
      :redcap:sectionheader "Form Status"}
     [:question [:translatedtext "Complete?"]]
     [:codelistref {:codelistoid "form_2_complete.choices"}]]
    [:codelist
     {:oid "form_1_complete.choices",
      :name "form_1_complete",
      :datatype "text",
      :redcap:variable "form_1_complete"}
     [:codelistitem
      {:codedvalue "0"}
      [:decode [:translatedtext "Incomplete"]]]
     [:codelistitem
      {:codedvalue "1"}
      [:decode [:translatedtext "Unverified"]]]
     [:codelistitem
      {:codedvalue "2"}
      [:decode [:translatedtext "Complete"]]]]
    [:codelist
     {:oid "form_2_complete.choices",
      :name "form_2_complete",
      :datatype "text",
      :redcap:variable "form_2_complete"}
     [:codelistitem
      {:codedvalue "0"}
      [:decode [:translatedtext "Incomplete"]]]
     [:codelistitem
      {:codedvalue "1"}
      [:decode [:translatedtext "Unverified"]]]
     [:codelistitem
      {:codedvalue "2"}
      [:decode [:translatedtext "Complete"]]]]]]
  [:clinicaldata
   {:studyoid "Project.TestProject",
    :metadataversionoid "Metadata.TestProject_2024-02-06_0355"}
   [:subjectdata
    {:subjectkey "1", :redcap:recordidfield "record_id"}
    [:formdata
     {:formoid "Form.form_1", :formrepeatkey "1"}
     [:itemgroupdata
      {:itemgroupoid "form_1.record_id", :itemgrouprepeatkey "1"}
      [:itemdata {:itemoid "record_id", :value "1"}]
      [:itemdata {:itemoid "name", :value "Test"}]]
     [:itemgroupdata
      {:itemgroupoid "form_1.form_1_complete",
       :itemgrouprepeatkey "1"}
      [:itemdata {:itemoid "form_1_complete", :value "2"}]]]
    [:formdata
     {:formoid "Form.form_2", :formrepeatkey "1"}
     [:itemgroupdata
      {:itemgroupoid "form_2.age", :itemgrouprepeatkey "1"}
      [:itemdata {:itemoid "age", :value "10"}]]
     [:itemgroupdata
      {:itemgroupoid "form_2.form_2_complete",
       :itemgrouprepeatkey "1"}
      [:itemdata {:itemoid "form_2_complete", :value "2"}]]]]
   [:subjectdata
    {:subjectkey "2", :redcap:recordidfield "record_id"}
    [:formdata
     {:formoid "Form.form_1", :formrepeatkey "1"}
     [:itemgroupdata
      {:itemgroupoid "form_1.record_id", :itemgrouprepeatkey "1"}
      [:itemdata {:itemoid "record_id", :value "2"}]
      [:itemdata {:itemoid "name", :value "Test 2"}]]
     [:itemgroupdata
      {:itemgroupoid "form_1.form_1_complete",
       :itemgrouprepeatkey "1"}
      [:itemdata {:itemoid "form_1_complete", :value "2"}]]]
    [:formdata
     {:formoid "Form.form_2", :formrepeatkey "1"}
     [:itemgroupdata
      {:itemgroupoid "form_2.age", :itemgrouprepeatkey "1"}
      [:itemdata {:itemoid "age", :value "15"}]]
     [:itemgroupdata
      {:itemgroupoid "form_2.form_2_complete",
       :itemgrouprepeatkey "1"}
      [:itemdata {:itemoid "form_2_complete", :value "2"}]]]]
   [:subjectdata
    {:subjectkey "3", :redcap:recordidfield "record_id"}
    [:formdata
     {:formoid "Form.form_1", :formrepeatkey "1"}
     [:itemgroupdata
      {:itemgroupoid "form_1.record_id", :itemgrouprepeatkey "1"}
      [:itemdata {:itemoid "record_id", :value "3"}]
      [:itemdata {:itemoid "name", :value "Test 2"}]]
     [:itemgroupdata
      {:itemgroupoid "form_1.form_1_complete",
       :itemgrouprepeatkey "1"}
      [:itemdata {:itemoid "form_1_complete", :value "2"}]]]
    [:formdata
     {:formoid "Form.form_2", :formrepeatkey "1"}
     [:itemgroupdata
      {:itemgroupoid "form_2.age", :itemgrouprepeatkey "1"}
      [:itemdata {:itemoid "age", :value "15"}]]
     [:itemgroupdata
      {:itemgroupoid "form_2.form_2_complete",
       :itemgrouprepeatkey "1"}
      [:itemdata {:itemoid "form_2_complete", :value "2"}]]]]]]
```

## License

Copyright © 2024 Chris Zheng

Distributed under the MIT License.
