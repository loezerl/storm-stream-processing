package bolt;

import classifiers.Classifier;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import util.InstanceParser;

import java.util.Map;
import java.util.Vector;

/**
 * Created by loezerl-fworks on 19/10/17.
 */
public class Prequential extends BaseRichBolt {
    OutputCollector _collector;
    Classifier classifier;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        _collector = outputCollector;
        try{
            classifier = Classifier.getInstance();
        }catch(Exception e){
            System.err.println("[ERROR] Can't get Classifier Instance -> " + e.getMessage());
            _collector.reportError(e);
        }
    }

    @Override
    public void execute(Tuple tuple) {
        String value=null;
        Boolean result=null;
        try{
            value = (String)tuple.getValue(0);

        }catch(Exception e) {
            System.err.println("[ERROR] Could not parser the Tuple (Instance) -> " + e.getMessage()
            + "\tTuple -> " + tuple.toString());
            _collector.reportError(e);
        }
            Vector<Double> instance = InstanceParser.Parser(value);
            result = classifier.test(instance);
        try{
            classifier.train(instance);
        }catch (Exception e){
            System.err.println("[ERROR] Could not Train the Classifier -> " + e.getMessage() + "\tTuple -> " + tuple.toString());
        }
        try{
            _collector.emit(tuple, new Values(result));
            _collector.ack(tuple);
        }catch(Exception e){
            System.err.println("[ERROR] Could not emit the Boolean tuple -> " + e.getMessage());
            System.err.println("[ERROR] Result value -> " + result);
        }


    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("result"));
    }
}
