(ns ohmycards.web.views.about.core
  (:require [cljs.core.async :as async]
            [ohmycards.web.components.spinner.core :as spinner]
            [ohmycards.web.services.fetch-be-version.core
             :as
             services.fetch-be-version]
            [ohmycards.web.version :as version]
            [reagent.core :as r]))

(defn- about-textbox
  "Main textbox with explanation about OhMyCards."
  []
  [:div.about-view__about-textbox
   [:h2 "About"]
   [:p (str "OhMyCards is a personal project aimed at being a simple system to keep track of"
            " cards.")]
   [:p (str "It was also an opportunity to learn Scala and practice my Clojurescript/React skills")]
   [:p (str "If you have any questions, see ")
    [:a {:href "https://github.com/vitorqb/oh-my-cards"}
     "https://github.com/vitorqb/oh-my-cards"]
    " and feel free to drop me a line!"]])

(defn- version-label
  "A label with a version for the app or the server."
  [{::keys [text version]}]
  (if version
    [:div (str text version)]
    [:div text [spinner/smallest]]))

(defn- versions-infobox
  "A box with information for the versions."
  [{::keys [be-version fe-version]}]
  [:div.about-view__versions-infobox
   [:h2 "Versions"]
   [version-label {::text "Server: " ::version be-version}]
   [version-label {::text "Web: " ::version fe-version}]])

(defn- fetch-be-version!
  [{:keys [http-fn]} a]
  "Queries the service to fetch the BE version and set's the atom `a` to it."
  (async/go (reset! a (async/<! (services.fetch-be-version/main {:http-fn http-fn})))))

(defn main
  "A page to display information about the system."
  [props]
  (let [!be-version (r/atom nil)]
    (fetch-be-version! props !be-version)
    (fn [_]
      [:div.about-view
       [about-textbox]
       [versions-infobox {::be-version @!be-version ::fe-version version/VERSION}]])))
