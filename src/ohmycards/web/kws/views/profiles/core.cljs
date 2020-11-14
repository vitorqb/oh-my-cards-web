(ns ohmycards.web.kws.views.profiles.core)

(def ^:const profile-names "A sequence with all profile names." ::profile-names)
(def ^:const load-profile! "A fn that receives the name of the profile and loads it" ::load-profile!)
(def ^:const goto-grid! "A 0-arg fn that redirects to the main grid." ::goto-grid!)
