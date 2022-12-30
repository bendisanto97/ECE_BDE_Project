## Command #1 - 
curl -X PUT "localhost:9200/logs?pretty" -H 'Content-Type: application/json' -d'
{
	"mappings" : {
    	"properties" : {
        	"ip" : { "type" : "keyword" },
        	"dateTime": {"type" : "date", "format" : "dd/MMM/yyyy:HH:mm:ss"},
        	"httpStatus": {"type" : "keyword"},
        	"url" : { "type" : "keyword" },
        	"responseCode" : { "type" : "keyword" },
        	"size" : { "type" : "integer" }

    	        }
	}
}
'

## Command #2 - 
curl 'localhost:9200/_cat/indices?v'