(ns clj-redcap.core
  (:require [cheshire.core :as json]
            [clj-http.client :as client]
            [clojure.string :as str]))


;; TO DO:
;; - Verify the REDCap server's SSL certificate.


(defn clj-http-ex [e]
  (let [body (json/parse-string (:body (ex-data e)))]
    (Exception. (str/join "\n" (map (fn [[k v]] (str k ": " v)) body)))))


(defn version [config]
  (:body
   (client/post
    (:api-url config)
    {:accept :json
     :form-params {:token (:token config)
                   :content "version"}})))


(defn metadata [config]
  (json/parse-string
   (:body
    (client/post
     (:api-url config)
     {:form-params {:token (:token config)
                    :content "metadata"
                    :format "json"
                    :returnFormat "json"}}))))


(defn select-records
  "Selects a subset of records from a project.  The id argument is a
  vector containing the value of the primary key field of the desired
  records, e.g. (select-records config [\"abc\" \"xyz\"]). Returns nil
  when no records are found."
  [config ids]
  (assert (vector? ids) "ids must be a vector of integers or strings.")
  (try
    (json/parse-string
     (:body
      (client/post
       (:api-url config)
       {:multi-param-style :indexed
        :form-params
        (let [payload {:token (:token config)
                       :content "record"
                       :format "json"
                       :type "flat"
                       :rawOrLabel "raw"
                       :rawOrLabelHeaders "raw"
                       :exportCheckboxLabel "false"
                       :exportSurveyFields "false"
                       :exportDataAccessGroups "false"
                       :returnFormat "json"}]
          (if (empty? ids)
            payload
            (assoc payload :records ids)))})))
    (catch clojure.lang.ExceptionInfo e
      (throw (clj-http-ex e)))))


(defn select-one-record [config id]
  "Select a single record from a project.  The id argument is the
  value of primary key field for the desired record, e.g.
  (select-one-record config \"xyz\".  Returns nil when no records are
  found."
  (assert (or (integer? id) (string? id)) "id must be an integer or string.")
  (first
   (select-records config [id])))


(defn select-all-records [config]
  (select-records config []))


(defn upsert-record [config record]
  "Inserts a new record or updates an exsiting record in a project.
  The record arguement is a hash-map whose keys are field names and
  whose values are the corresponding field values.  When the record
  hash-map contains a key which is the project's primary key, and the
  associated value corresponds to an existing record, the values of
  that record will be updated.  When the record hash-map contains a
  key which is the project's primary key, and the associated value
  does not correspond to an existing record, a new record is created.
  Returns true when a single record has been successfully inserted or
  updated, and throws a exception otherwise. For example, suppose
  participant_id is the primary key for a project.  If there is no
  existing record with participant_id=3, then (insert-record config
  {:participant_id 3}) will insert a new record with no other fields
  populated.  If there is no existing record with participant_id=7,
  then (insert-record config {:participant_id 7 :comment_box \"This is
  record 19.\"}) will create a new record, and will populate the
  \"comment_box\" field. However, if there is an existing record with
  participant_id=7, then the comment_box field in the exisiting record
  will be updated, and all other fields will remain unchanged."
  (assert (map? record) "record must be a map.")
  (try
    (let [return-value
          (json/parse-string
           (:body
            (client/post
             (:api-url config)
             {:form-params {:token (:token config)
                            :content "record"
                            :format "json"
                            :type "flat"
                            :overwriteBehavior "normal"
                            :dateFormat "Y-M-D"
                            :returnContent "count"
                            ;; :returnContent "ids"
                            :returnFormat "json"
                            :data (json/generate-string (vector record))}})))
          count (get return-value "count")]
      (if (= count 1)
        true
        (throw (Exception. (format "upsert-record affected %d records" count)))))
    (catch clojure.lang.ExceptionInfo e
      (throw (clj-http-ex e)))))


(defn- delete-records [config ids]
  (assert (vector? ids) "ids must be a vector of integers or strings.")
  (try
    (let [return-value
          (json/parse-string
           (:body
            (client/post
             (:api-url config)
             {:multi-param-style :indexed
              :form-params
              (let [payload {:token (:token config)
                             :content "record"
                             :action "delete"
                             :returnFormat "json"}]
                (if (empty? ids)
                  payload
                  (assoc payload :records ids)))})))]
      return-value)
    (catch clojure.lang.ExceptionInfo e
      (throw (clj-http-ex e)))))


(defn delete-one-record [config id]
  "Deletes a single record from a project.  The id argument is the
  value of primary key field of the record to be deleted, e.g.
  (delete-one-record config \"xyz\").  Throws an exception when zero
  or more than one record is deleted."
  (assert (or (integer? id) (string? id)) "id must be an integer or string.")
  (let [count (delete-records config [id])]
    (if (= count 1)
      true
      (throw (Exception. (format "delete-one-record affected %d records" count))))))


(defn- delete-all-records [config]
  (delete-records config []))

