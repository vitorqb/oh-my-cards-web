(ns ohmycards.web.components.header.core)

(defn main
  "An header component for the app."
  [{:keys [left center right extra-class] :as props}]
  [:div {:class (cond-> ["header"] extra-class (conj extra-class))}
   [:span.header__left left]
   [:span.header__center center]
   [:span.header__right right]])
