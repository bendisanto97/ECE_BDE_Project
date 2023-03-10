package com.hadoop_elasticsearch_project;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.elasticsearch.hadoop.mr.EsOutputFormat;
import org.elasticsearch.hadoop.util.WritableUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AccessLogIndexIngestion {
    public static class AccessLogMapper extends Mapper {
        /*
        * Overriding the default method to define the programming logic we want to use
        */
        @Override
        protected void map(Object key, Object value, Context context) throws IOException, InterruptedException {
            // Line of Log we need to map
            String logEntry = value.toString();
            
            // Split on space
            String[] parts = logEntry.split(" ");
            Map<String, String> entry = new LinkedHashMap<>();

            // Applying the necessary formatting for our query 
            // Combined LogFormat "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-agent}i\"" combined
            entry.put("ip", parts[0]);
            
            // Cleanup dateTime String
            entry.put("dateTime", parts[3].replace("[", ""));
            
            // Cleanup extra quote from HTTP Status
            entry.put("httpStatus", parts[5].replace("\"",  ""));
            entry.put("url", parts[6]);
            entry.put("responseCode", parts[8]);
            
            // Set size to 0 if not present
            entry.put("size", parts[9].replace("-", "0"));

            // 
            context.write(NullWritable.get(), WritableUtils.toWritable(entry));
        }
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        
        // Disables speculative execution from Hadoop since output is going to ElasticSearch
        // Enabling it might create duplicate values
        conf.setBoolean("mapred.map.tasks.speculative.execution", false);
        conf.setBoolean("mapred.reduce.tasks.speculative.execution", false);
        conf.set("es.nodes", "127.0.0.1:9200");
        conf.set("es.resource", "logs");

        // Simplify format with TextInput since input is text
        // Output has to be ElasticSearch Index value
        // Apply the mapper class defined above
        // No reducer = 0
        Job job = Job.getInstance(conf);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(EsOutputFormat.class);
        job.setMapperClass(AccessLogMapper.class);
        job.setNumReduceTasks(0);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
