(ns redcap.meta-test
  (:use code.test)
  (:require [redcap.meta :as meta]))

^{:refer redcap.meta/url? :added "0.1"}
(fact "checks if a string is a url"
  ^:hidden
  
  (meta/url? "https://www.google.com")
  => true

  (meta/url? "http://google")
  => true

  (meta/url? "ws://www.google.com")
  => false

  (meta/url? "www.google.com")
  => false

  (meta/url? "google")
  => false)

^{:refer redcap.meta/format-date :added "0.1"}
(fact "formats the date"
  ^:hidden

  (meta/format-date "YYYY-MM-DD HH:mm"
                    (java.util.Date. 0))
  ;; "1970-01-01 10:00"
  => string?)

^{:refer redcap.meta/populate-entry :added "0.1"}
(fact "populates the api entry"
  ^:hidden

  (meta/populate-entry
   :user-role :import-mapping
   (get-in (into {} meta/+api+) [:user-role :import-mapping]))
  => (contains-in
      {:content "userRoleMapping", :params {:data {:type [:vector [:map]],
                                                   :required true}},
       :category :user-role,
       :tag :import-mapping,
       :action "import",
       :label "import-user-role-mapping"})
  
  (meta/populate-entry
   :storage :create-folder
   (get-in (into {} meta/+api+) [:storage :create-folder]))
  => (contains-in
      {:content "fileRepository",
       :action "createFolder",
       :params {:name {:type :string, :required true},
                :folder-id {:type :string, }},
       :category :storage,
       :tag :create-folder,
       :label "create-storage-folder"}))
