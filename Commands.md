## Command #1 - Apply indexing for Elasticsearch for the log records
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

## Command #2 - Run the MapReduce project on the logs 
hadoop jar hadoopwithelasticsearch-1.0-SNAPSHOT.jar access.log

## Command #3 - Verifiy the correct indexing of records in Elasticsearch
curl 'localhost:9200/_cat/indices?v'