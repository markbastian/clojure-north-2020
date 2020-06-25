(ns clojure-north-2020.ch04-application.x04-parting-thoughts)

;; # In Conclusion
;; In this workshop, we've learned some key concepts that should help you in
;; your Clojure journey from novice to full-stack, stateful system developer:
;; 1. Always start with your data. Model the data, think about the data. Don't
;;    write any code until you understand the data.
;; 2. Manipulate your data using Clojure's baked in functions. Clojure truly is
;;    a data DSL. As transformations become useful, elevate them to functions.
;; 3. Datascript/hike, Datomic, and similar are powerful, data-oriented
;;    databases. These allow you to model domains at the attribute level rather
;;    than the record level, giving a much more powerful and flexible way to
;;    work with your data.
;; 3. Stateful systems can be converted into functional systems by pushing all
;;    stateful components to the edge of the system. One powerful library for
;;    doing this is Integrant. Once this is done, all stateful components are
;;    bundled at one edge of the system, all logic is functions, and needed
;;    aspects of the various components are threaded through the functions to be
;;    used as system-agnostic parameters.
;;
;; ## Additional Material
;; If you liked this workshop and want to learn more about any of the topics,
;; feel free to check out these projects:
;; * [datascript-playground](https://github.com/markbastian/datascript-playground)
;; - This is a somewhat disorganized project of mine that explores datascript,
;;   topics, but it has a lot of examples that I've worked through to
;;   understand how to model data in Datascript.
;; * [Datascript and Datomic: Data Modeling for Heroes](https://www.youtube.com/watch?v=tV4pHW_WOrY) -
;;   A talk I gave on data modeling with Datascript and Datomic. One correction:
;;   At the time I misunderstood the db.unique/value schema type so ignore the
;;   brief statements I made on that.
;; * [Partsbin](https://github.com/markbastian/partsbin) - A project I maintain
;;   that provides read-made components for use with Integrant. It also has a
;;   good explanation of how to build composable systems such that each part
;;   remains simple and decoupled.
;; * [Defeating the Four Horsemen of the Coding Apocalypse](https://www.youtube.com/watch?v=jh4hMAvygjk)
;;   - My Clojure/conj 2019 talk in which I discuss 4 coding challenges we face,
;;   one of which is Complexity, which is tied to this presentation.
;; * [Bottom Up vs Top Down Design in Clojure](https://www.youtube.com/watch?v=Tb823aqgX_0)
;;   - My Clojure/conj 2015 talk in which I describe building an application
;;   using many of the techniques discussed today - start with data, build
;;   functions, work your way up to an application.
;; * [Web Development with Clojure](https://www.amazon.com/Web-Development-Clojure-Build-Bulletproof/dp/1680500821) -
;;   An authoritative guide to building web apps with Clojure from the author of
;;   the [Luminus](https://luminusweb.com) framework.
;;
;; Feedback
;; I hope you enjoyed this workshop. I value any feedback you may have, positive
;; or otherwise. Please feel free to reach our to me on twitter (@mark_bastian),
;; gmail (markbastian@gmail.com), or linkedin (https://www.linkedin.com/in/mark-bastian-295553102/).