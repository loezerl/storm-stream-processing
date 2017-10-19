package bolt;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by loezerl-fworks on 19/10/17.
 */
public class CleanInstance extends BaseRichBolt {
    OutputCollector _collector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        _collector = outputCollector;
        try{
            _collector = outputCollector;
        }catch (Exception e){
            _collector.reportError(e);
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void execute(Tuple tuple) {
        String re1="\\d*\\.\\d+";
        String sentecen = tuple.toString();
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher match = pattern.matcher(sentecen);

        List<String> instance = new ArrayList<>();
        String clean_instance = "";
        if(match.find()){
            instance = Arrays.asList(match.group(1).split(", "));
            for(String el: instance){
                if(el.matches(re1)){
                    clean_instance = clean_instance + el + " ";
                }
            }
            try {
                _collector.emit(tuple, new Values(clean_instance));
                _collector.ack(tuple);
            }catch(Exception e){
                System.err.println("[ERROR] Can't emit Instance Tuple -> " + e.getMessage());
                _collector.reportError(e);
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("instance_double"));
    }
}
