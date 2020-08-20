(ns minimalquotes.firebase.storage
  (:require
    [cljs.core.async :refer [go]]
    [cljs.core.async.interop :refer-macros [<p!]]
    [goog.object :as object]))

(set! *warn-on-infer* false)

(defn upload-file
  "Upload a file on a Firebase Cloud Storage bucket."
  [{:keys [^js auth ^js file on-error on-success ^js storage]}]
  (let [file-name (.-name file)
        user (.-currentUser auth)
        uid (.-uid user)
        file-path (str uid "/" file-name)
        storage-ref (.ref storage file-path)]
    ;; uts = upload-task-snapshot
    (go (try (let [uts (<p! (.put storage-ref file))
                   task (.-task uts)
                   observer #js
                             {:complete #(prn "complete")
                              :error #(on-error %)
                              :next (fn [snapshot]
                                       ;; e.g. use this function to update a
                                       ;; progress bar.
                                       (prn "snapshot next" snapshot))}
                   url (<p! (.getDownloadURL (.-ref uts)))]
               ;    (prn "getAllPropertyNames entry" (object/getAllPropertyNames
               ;    task false false))
               (.on task "state_changed" observer)
               (on-success url))
             (catch js/Error err
               (on-error err))))))
