import bolt.CleanInstance;
import bolt.ParserResults;
import bolt.Prequential;
import classifiers.Classifier;
import classifiers.KNN;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.utils.Utils;
import java.util.*;


/**
 * Created by loezerl-fworks on 18/10/17.
 */
public class Experimenter {


    public static void main(String[] args) throws Exception{

        String zkIP = "localhost:";
        String nimbushost = "localhost:9092";
        String zookeeperHost = zkIP + "2181";
        String topic = "instances";

        ZkHosts zkHost = new ZkHosts(zookeeperHost);

        List<String> topics = new ArrayList<>();
        topics.add(topic);

        KafkaSpoutConfig kafkaConfig = KafkaSpoutConfig.builder("localhost:9092", topics)
                .setGroupId("teste")
                .build();

        KafkaSpout kafkaSpout = new KafkaSpout(kafkaConfig);

        TopologyBuilder builder = new TopologyBuilder();
        ParserResults resultsbolt = new ParserResults();

        builder.setSpout("instances", kafkaSpout, 1);
        builder.setBolt("clean_instances", new CleanInstance(), 1)
                .shuffleGrouping("instances");
        builder.setBolt("prequential", new Prequential(), 1)
                .shuffleGrouping("clean_instances");
        builder.setBolt("results", resultsbolt, 1)
                .shuffleGrouping("prequential");

        KNN classifier = new KNN(7, 1000, "euclidean");
        Classifier.setInstance(classifier);

        Config config = new Config();
        config.setDebug(true);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    System.err.println("\n\n=============================\nRunning shutdownhook..");
                    System.err.println("Acertos: " + resultsbolt.hits);
                    System.err.println("Erros: " + resultsbolt.miss);
                    System.err.println("Instancias: " + (resultsbolt.miss + resultsbolt.hits));
                }catch (Exception e){}
            }
        });

        config.setNumWorkers(1);
        LocalCluster cluster = new LocalCluster();
        try {
            cluster.submitTopology("kafka-storm", config, builder.createTopology());
            Utils.sleep(60000);
        }catch(Exception e){
            throw new IllegalStateException("Couldn't initialize the topology", e);
        }
        cluster.killTopology("kafka-storm");
        cluster.shutdown();

//        System.err.println("\n\n==========Results\n");
//        System.err.println("Acertos: " + resultsbolt.hits);
//        System.err.println("Erros: " + resultsbolt.miss);
//        System.err.println("Instancias: " + (resultsbolt.miss + resultsbolt.hits));

//        System.setProperty("storm.jar", "/home/loezerl-fworks/IdeaProjects/storm-knn2/out/artifacts/storm_knn2_jar/storm-knn2.jar");
//        try {
//            config.setNumWorkers(1);
//            StormSubmitter.submitTopology("my-topology", config, builder.createTopology());
//        } catch (Exception e) {
//            throw new IllegalStateException("Couldn't initialize the topology", e);
//        }


    }
}
