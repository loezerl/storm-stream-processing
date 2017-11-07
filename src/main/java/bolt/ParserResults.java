package bolt;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

/**
 * Created by loezerl-fworks on 19/10/17.
 */
public class ParserResults extends BaseRichBolt{
    public long hits=0;
    public long miss=0;
    OutputCollector _collector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        try{
            _collector = outputCollector;
        }catch(Exception e){
            System.err.println("ERROR -> " + e.getMessage());
        }
    }

    @Override
    public void execute(Tuple tuple) {
        try{
            Boolean value = (Boolean) tuple.getValue(0);
            if(value){
                this.hits++;
            }else{
                this.miss++;
            }
            _collector.ack(tuple);
        }catch(Exception e){
            System.err.println("[ERROR] Parser Results -> " + e.getMessage()
            + "\t Value -> " + tuple.toString());
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public void cleanup(){

        System.err.println("\n\n\nALO BRASIL\n\n\n");

        float accuracy = hits + miss;
        accuracy = (hits*100)/accuracy;

        System.err.println("\n\n\nInstancias: " + (hits+miss) + "\nTotal acertos: " + hits + " Total erros: " + miss + "\nAcuracia: " + accuracy);
        System.exit(1);

    }
}
