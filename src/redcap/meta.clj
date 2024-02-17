(ns redcap.meta
  (:require [std.string :as str]
            [std.lib :as h :refer [defimpl]]
            [std.json :as json])
  (:import (java.text SimpleDateFormat ParsePosition)))

(defn url?
  "checks if a string is a url"
  {:added "0.1"}
  [s]
  (try
    (do
      (java.net.URL. s)
      true)
    (catch java.net.MalformedURLException e false)))

(def TokenSpec
  [:string {:min 32 :max 32}])

(def FormatSpec
  [:enum {:error/message "should be csv|json|xml"}
   "csv" "json" "xml"])

(def TypeSpec
  [:enum {:error/message "should be flat|eav|record"}
   "flat" "eav" "record"])

(def LabelSpec
  [:enum {:error/message "should be raw|label"}
   "raw" "label"])

(def DelimiterSpec
  [:enum {:error/message "should be , or ; or | or ^ or \t"}
   "," ";" "|" "^" "\t"])

(def OverwriteSpec
  [:enum {:error/message "should be normal|overwrite"}
   "normal" "overwrite"])

(def DateFormatSpec
  [:enum {:error/message "should be MDY|DMY|YMD"}
   "MDY" "DMY" "YMD"])

(def ReturnContentSpec
  [:enum {:error/message "should be count|ids|auto_ids|nothing"}
   "count" "ids" "auto_ids" "nothing"])

(def LogTypeSpec
  [:enum {:error/message "should be export|user|manage|record|record_add|record_edit|record_delete|lock_record|page_view"}
   "export"
   "user"
   "manage"
   "record"
   "record_add"
   "record_edit"
   "record_delete"
   "lock_record"
   "page_view"])

(defn format-date
  "formats the date"
  {:added "0.1"}
  [format t]
  (cond (string? t) t

        (inst? t)
        (. (SimpleDateFormat. format)
           (format t))))

(def +actions+
  #{:export
    :import
    :delete
    :rename
    :switch
    :list})

(def +params-default+
  {:token          {:type TokenSpec}
   :content        {:type :string}
   :format         {:type FormatSpec}
   :return-format  {:type FormatSpec}})

(defn record-read-single
  [m]
  (let [ks  (keys m)
        mks (keep (fn [k]
                    (re-find #"^(.+)___(\d+)$" k))
                  ks)
        gks (->> (group-by second mks)
                 (h/map-vals
                  (fn [arr]
                    (sort-by (fn [[_ _ num]]
                               (parse-long num))
                             arr))))
        agg (h/map-vals 
             (fn [arr]
               (mapv (fn [[k]] (get m k))
                     arr))
             gks)
        rks (apply dissoc m (map first mks))]
    (merge agg rks)))

(defn record-read
  [arr]
  (mapv record-read-single arr))

(defn record-write-single
  [m]
  (let [rm   (h/filter-vals vector? m)
        rks  (keys rm)
        agg  (reduce (fn [out [k vs]]
                       (->> (map-indexed
                             (fn [i v]
                               [(str k "___" (+ i 1))
                                v])
                             vs)
                            (into out)))
                     {}
                     rm)]
    (merge (apply dissoc m rks)
           agg)))

(defn record-write
  [arr]
  (mapv record-write-single arr))

(def +api+
  [[:arm         {:export {:content "arm"
                           :params {:arms      {:type [:vector :string]}}}
                  :import {:content "arm"
                           :params {:data      {:type [:vector [:map]]
                                                :transform json/write
                                                :required  true}}}
                  :delete {:content "arm"
                           :params {:arms      {:type [:vector :string]}}}}]
   [:dag         {:export {:content "dag"}
                  :import {:content "dag"
                           :params {:data      {:type [:vector [:map]]
                                                :transform json/write
                                                :required  true}}}
                  :delete {:content "dag"
                           :params {:dags      {:type [:vector :string]}}}
                  :switch {:content "dag"
                           :params {:dag       {:type  :string}}}
                  :export-assignment {:content "userDagMapping"}
                  :import-assignment {:content "userDagMapping"
                                      :params
                                      {:data  {:type [:vector [:map]]
                                               :transform json/write
                                               :required  true}}}}]
   
   [:event       {:export {:content "event"
                           :params {:arms  {:type [:vector :string]}}}
                  :import {:content "event"
                           :params {:data      {:type [:vector [:map]]
                                                :transform json/write
                                                :required  true}}}
                  :delete {:content "event"
                           :params {:events    {:type [:vector :string]}}}}]
   [:field-name  {:export {:content "exportFieldNames"
                           :params {:field  {:type :string}}}}]
   [:file        {:export {:content "file"
                           :params {:record {:type :string}
                                    :field  {:type :string}
                                    :event  {:type :string}}}
                  :import {:content "file"
                           :params {:record {:type :string}
                                    :field  {:type :string}
                                    :event  {:type :string}
                                    :file   {:type :string
                                             :required true}}}
                  :delete {:content "file"
                           :params {:record {:type :string}
                                    :field  {:type :string}
                                    :event  {:type :string}}}}]
   [:storage     {:create-folder {:content "fileRepository"
                                  :action  "createFolder"
                                  :label   "create-storage-folder"
                                  :params  {:name {:type :string
                                                   :required true}
                                            :folder-id {:type :string
                                                        :transform-key str/snake-case
                                                        :required true}}}
                  :list   {:content "fileRepository"
                           :params  {:folder-id {:type :string
                                                 :transform-key str/snake-case
                                                 :required true}}}
                  :export {:content "fileRepository"
                           :params  {:doc-id {:type :string
                                              :transform-key str/snake-case
                                              :required true}}}
                  :import {:content "fileRepository"
                           :params  {:file   {:type :string
                                              :required true}}}
                  :delete {:content "fileRepository"
                           :params  {:doc-id {:type :string
                                              :transform-key str/snake-case
                                              :required true}}}}]
   [:instrument  {:export {:content "instrument"}
                  :export-pdf {:content "pdf"
                               :params {:record     {:type :string}
                                        :instrument {:type :string}}}
                  :export-event-mappings {:content "formEventMapping"}
                  :import-event-mappings {:content "formEventMapping"
                                          :params
                                          {:data      {:type [:vector [:map]]
                                                       :transform json/write
                                                       :required  true}}}
                  :export-repeating {:content "repeatingFormsEvents"}
                  :import-repeating {:content "repeatingFormsEvents"
                                     :params
                                     {:data      {:type [:vector [:map]]
                                                  :transform json/write
                                                  :required  true}}}}]
   [:logging     {:export {:content "log"
                           :params {:logtype   {:type LogTypeSpec}
                                    :user      {:type :string}
                                    :record    {:type :string}
                                    :begin-time {:type inst?
                                                 :transform (partial format-date "YYYY-MM-DD HH:mm")} 
                                    :end-time   {:type inst?
                                                 :transform (partial format-date "YYYY-MM-DD HH:mm")}}}}]
   [:metadata    {:export {:content "metadata"
                           :params {:fields    {:type [:vector :string]}
                                    :forms     {:type [:vector :string]}}}
                  :import {:content "metadata"
                           :params {:data    {:type      [:vector [:map]]
                                              :transform json/write
                                              :required true}}}}]
   [:project     {:export-info {:content "project"}
                  :import-info {:content "project_settings"
                                :params  {:data    {:type      [:map]
                                                    :transform json/write
                                                    :required true}}}
                  :export-xml  {:content "project_xml"
                                :overwrite {:format "xml"
                                            :returnFormat "xml"}
                                :params  {:return-metadata-only {:type :boolean}
                                          :records              {:type [:vector :string]}
                                          :fields               {:type [:vector :string]}
                                          :export-files         {:type :boolean}
                                          :export-survey-fields {:type :boolean}
                                          :export-data-access-groups {:type :boolean}
                                          :filter-logic         {:type :string}}}}]
   [:record      {:export {:content "record"
                           :params  {:type   {:type TypeSpec}
                                     :records  {:type [:vector :string]}
                                     :fields   {:type [:vector :string]}
                                     :forms    {:type [:vector :string]}
                                     :events   {:type [:vector :string]}
                                     :csv-delimiter {:type DelimiterSpec}
                                     :raw-or-label  {:type LabelSpec}
                                     :raw-or-label-headers  {:type LabelSpec}
                                     :export-checkbox-label {:type :boolean}
                                     :export-survey-fields  {:type :boolean}
                                     :export-data-access-groups {:type :boolean}
                                     :date-range-begin          {:type      inst?
                                                                 :transform
                                                                 (partial format-date "YYYY-MM-DD HH:mm:ss")}
                                     :date-range-end            {:type      inst?
                                                                 :transform
                                                                 (partial format-date "YYYY-MM-DD HH:mm:ss")}}
                           :output  {:transform #'record-read}}
                  :import {:content "record"
                           :params  {:type   {:type TypeSpec}
                                     :overwrite-behavior {:type OverwriteSpec}
                                     :force-auto-number  {:type :boolean}
                                     :data      {:type [:vector [:map]]
                                                 :transform (comp json/write #'record-write)
                                                 :required  true}
                                     :data-format        {:type DateFormatSpec}
                                     :return-content     {:type ReturnContentSpec}}}
                  :delete {:content "record"
                           :params  {:roles        {:type [:vector :string]}
                                     :records      {:type [:vector :string]}
                                     :arms         {:type :string}
                                     :instrument   {:type :string}
                                     :event        {:type :string}}}
                  :rename {:content "record"
                           :params  {:record       {:type :string
                                                    :required true}
                                     :new-record-name  {:type :string
                                                        :required true
                                                        :transform-key str/snake-case}
                                     :arms         {:type :string}}}
                  :generate-next {:content "generateNextRecordName"
                                  :label   "export-record-next-name"
                                  :action  "export"}}]
   [:report      {:export {:content "report"
                           :params {:report-id {:type :string
                                                :transform-key str/snake-case}
                                    :csv-delimiter {:type DelimiterSpec}
                                    :raw-or-label  {:type LabelSpec}
                                    :raw-or-label-headers  {:type LabelSpec}
                                    :export-checkbox-label {:type :boolean}}}}]
   [:survey      {:export-link {:content "surveyLink"
                                :params {:instrument {:type :string}
                                         :event      {:type :string}
                                         :record     {:type :string}}}
                  :export-participants {:content "participantList"
                                        :params {:instrument {:type :string}
                                                 :event      {:type :string}}}
                  :export-queue-link {:content "surveyQueueLink"
                                      :params {:instrument {:type :string}
                                               :event      {:type :string}}}
                  :export-return-code {:content "surveyReturnCode"
                                       :params {:instrument {:type :string}
                                                :event      {:type :string}
                                                :record     {:type :string}}}}]
   [:user        {:export {:content "user"}
                  :import {:content "user"
                           :params  {:data  {:type      [:map]
                                             :transform json/write
                                             :required true}}}
                  :delete {:params  {:users {:type  [:vector :string]}}}}]
   [:user-role   {:export {:content "userRole"}
                  :import {:content "userRole"
                           :params {:data  {:type [:vector [:map]]
                                            :transform json/write
                                            :required  true}}}
                  :delete {:content "userRole"
                           :params  {:roles  {:type      [:vector :string]}}}
                  :export-mapping {:content "userRoleMapping"}
                  :import-mapping {:content "userRoleMapping"
                                   :params {:data  {:type      [:vector [:map]]
                                                    :transform json/write
                                                    :required  true}}}}]
   [:version     {:export {:content "version"}}]])

(defn populate-entry
  "populates the api entry"
  {:added "0.1"}
  [category tag entry]
  (let [custom (boolean (get entry :action))
        action (or (get entry :action)
                   (name
                    (or (some (fn [action]
                                (when (str/starts-with? (name tag) (name action))
                                  action))
                              +actions+)
                        (throw (ex-info "Action not valid"
                                        {:category category
                                         :tag tag
                                         :entry entry})))))
        suffix (cond (get entry :label)
                     ""
                     
                     :else
                     (subs (name tag)
                           (count action)))
        label  (or (get entry :label)
                   (str action "-" (name category)
                        suffix))]
    (assoc entry
           :category category
           :tag tag
           :action action
           :label label)))

(def +api-linear+
  (vec (mapcat (fn [[category entries]]
                 (map (fn [[tag entry]]
                        (populate-entry category tag entry))
                      (sort entries)))
               +api+)))
