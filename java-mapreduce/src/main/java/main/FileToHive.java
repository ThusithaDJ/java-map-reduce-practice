package main;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileToHive {

    public static class HiveTableImportMapper extends Mapper<Object, Text, Text, Text> {

        private Text userId = new Text();
        private Text visitNo = new Text();

        @Override
        protected void map(Object key, Text value, Mapper<Object, Text, Text, Text>.Context context) throws IOException, InterruptedException {
            String[] parts = value.toString().split("\\|");

            userId.set(parts[0]);
            visitNo.set(parts[1]);

            context.write(userId, visitNo);
        }
    }

    public static class HiveTableImportReducer extends Reducer<Text, Text, Text, CompositeWritable> {
        @Override
        protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, CompositeWritable>.Context context) throws IOException, InterruptedException {
            final List<String> checks = new ArrayList<>();
            values.forEach(t -> checks.add(t.toString()));

            int visit = checks.size();

            for (String check : checks) {
                context.write(key, new CompositeWritable(check, visit));
            }
        }
    }

    public static void main(String[] args) {

        try {
            Configuration conf = new Configuration();
            Job job = Job.getInstance(conf, "Hive table import");
            job.setJarByClass(FileToHive.class);
            job.setMapperClass(HiveTableImportMapper.class);
            job.setReducerClass(HiveTableImportReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);
            job.setInputFormatClass(TextInputFormat.class);
            job.setOutputFormatClass(TableOutputFormat.class);

            FileInputFormat.setInputPaths(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1]));

            System.exit(job.waitForCompletion(true) ? 0 : 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
