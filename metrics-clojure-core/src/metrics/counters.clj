(ns metrics.counters
  (:require [metrics.core  :refer [default-registry metric-name]]
            [metrics.utils :refer [desugared-title]])
  (:import [com.codahale.metrics MetricRegistry Counter]
           java.util.concurrent.TimeUnit))

(defn counter
  "Create and return a new Counter metric with the given title. If a
  Counter already exists with the given title, will return that Counter.

  Title can be a plain string like \"foo\" or a vector of three strings (group,
  type, and title) like:

      [\"myapp\" \"webserver\" \"connections\"]

  "
  ([title]
   (counter default-registry title))
  ([^MetricRegistry reg title]
   (.counter reg (metric-name title))))


(defmacro defcounter
  "Define a new Counter metric with the given title.

  The title uses some basic desugaring to let you concisely define metrics:

    ; Define a counter titled \"default.default.foo\" into var foo
    (defcounter foo)
    (defcounter \"foo\")

    ; Define a counter titled \"a.b.c\" into var c
    (defcounter [a b c])
    (defcounter [\"a\" \"b\" \"c\"])
    (defcounter [a \"b\" c])
  "
  ([title]
   (let [[s title] (desugared-title title)]
     `(def ~s (counter '~title))))
  ([^MetricRegistry reg title]
   (let [[s title] (desugared-title title)]
     `(def ~s (counter ~reg '~title)))))


(defn value
  "Return the current value of the counter."
  [^Counter c]
  (.getCount c))


(defn inc!
  "Increment the counter by the given amount (or 1 if not specified)."
  ([^Counter c]
   (inc! c 1))
  ([^Counter c n]
   (.inc c n)
   c))

(defn dec!
  "Decrement the counter by the given amount (or 1 if not specified)."
  ([^Counter c]
   (dec! c 1))
  ([^Counter c n]
   (.dec c n)
   c))

(defn clear!
  "Clear the given counter, resetting its value to zero."
  [^Counter c]
  (.dec c (.getCount c))
  c)
