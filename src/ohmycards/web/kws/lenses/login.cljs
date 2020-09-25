(ns ohmycards.web.kws.lenses.login)

(def current-user "The current user, or nil if not logged in. See `kws.user`." ::current-user)
(def initialized? "True if the login service has finished initialization." ::initialized?)
