(ns ohmycards.web.kws.http)

(def success? "Boolean indicating whether the request has successed." ::success?)
(def body "The body of the response." ::body)
(def status "The status of the response" ::status)

(def method "Http method for requests." ::method)
(def url "Url for http requests." ::url)
(def json-params "Json params for http requests." ::json-params)
(def query-params "Query params for http requests." ::query-params)
(def token "The Bearer token to use on requests." ::token)
