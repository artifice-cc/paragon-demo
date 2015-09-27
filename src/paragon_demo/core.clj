(ns paragon-demo.core
  (:require [clojure.string :as str])
  (:require [clojure.pprint])
  (:require [clojure.java.io :as io])
  (:require [clojure.java.shell :as shell])
  (:require [paragon.visualization :as viz])
  (:require [clojure-watch.core :refer [start-watch]]))

(defn render-file
  [f]
  (when (re-matches #".*\.clj" f)
    (try
      (let [pdfname (format "%s.pdf" (str/replace f #"\.clj$" ""))
            sexp (read-string (format "(do (use '(paragon core coloring)) %s)" (slurp f)))
            fdn (eval sexp)]
        (viz/save-pdf fdn pdfname :stroke-labels? true)
        (shell/sh "open" pdfname))
      (catch java.io.FileNotFoundException _)
      (catch Exception e (.printStackTrace e)))))

(defn start-watch-dir
  [dir]
  (.mkdirs (io/file dir))
  (start-watch [{:path dir :event-types [:create :modify]
                 :callback (fn [e f] (render-file f))}]))


