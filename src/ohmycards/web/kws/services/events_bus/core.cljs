(ns ohmycards.web.kws.services.events-bus.core)

(def handler
  "The handler function to handle events. If must accept `[event-kw event-args]`
and it will be called every time an event is sent to the bus."
  ::handler)
