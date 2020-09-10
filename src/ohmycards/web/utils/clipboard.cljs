(ns ohmycards.web.utils.clipboard)

(defn to-clipboard!
  "Copies a text to the clipboard"
  [txt]
  (let [el (js/document.createElement "textarea")]
    (set! (.-value el) txt)
    (js/document.body.appendChild el)
    (.select el)
    (js/document.execCommand "copy")
    (js/document.body.removeChild el)))
