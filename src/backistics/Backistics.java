package backistics;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;

import backistics.pageparser.*;

class BackisticsMapper extends Mapper<LongWritable, Text, Text, StatAggregator> 
{
	@Override
	public void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, StatAggregator>.Context context) throws IOException, InterruptedException
	{
		BackyardPage thisPage = new BackyardPage();
		thisPage.parsePage(value.toString());
		int numReports = thisPage.directReports.size();
		StatAggregator statAgg = new StatAggregator(thisPage);
		context.write(new Text("statAgg"), statAgg );
	}
}

class BackisticsCombiner extends Reducer<Text, StatAggregator, Text, StatAggregator>
{
	public void reduce(Text key, Iterable<StatAggregator> values, Reducer<Text, StatAggregator, Text, StatAggregator>.Context context) throws IOException, InterruptedException
	{
		StatAggregator combined = new StatAggregator();
		for(StatAggregator value: values)
			combined.add(value);
		context.write(new Text("reportNum"), combined);
	}
}

class BackisticsReducer extends Reducer<Text, StatAggregator, Text, Text> 
{
	public void reduce(Text key, Iterable<StatAggregator> values, Reducer<Text, StatAggregator, Text, Text>.Context context) throws IOException, InterruptedException 
	{
		StatAggregator avg = new StatAggregator(0, 0);
		for(StatAggregator value: values)
			avg.add( value );
		context.write( new Text("sumOfReports"), new Text("Managers: " + avg.numManagers + ", Reports: " + avg.numReports) );
	}
}

public class Backistics extends Configured implements Tool
{
	@Override
	public int run(String[] args) throws Exception 
	{
		if(args.length < 2)
		{
			System.out.println("Usage: specify input and output directories as arguments");
			return 1;
		}

		Configuration conf = getConf();

		Job job = new Job(conf, "Backistics");
		job.setJarByClass(Backistics.class);
		
		job.setMapperClass(BackisticsMapper.class);
		job.setReducerClass(BackisticsReducer.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setMapOutputValueClass(StatAggregator.class);
		
		FileInputFormat.addInputPaths(job, args[0] );
		FileOutputFormat.setOutputPath(job, new Path( args[1] ) );
		
		job.waitForCompletion(true);
		
		return 0;
	}

	public static void main(String args[]) throws Exception
	{
		ToolRunner.run(new Backistics(), args);
	}

}