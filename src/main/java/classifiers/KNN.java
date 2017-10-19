package classifiers;

import util.InstanceDouble;
import util.Similarity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by loezerl-fworks on 19/10/17.
 */
public class KNN extends Classifier {
    private int K;
    private int WindowSize;
    private String DistanceFunction;
    private List<Vector<Double>> Window;

    public KNN(int kneighbours, int wsize, String function) {
        K = kneighbours;
        WindowSize = wsize;
        if(function.equals("euclidean")){
            DistanceFunction = "euclidean";
        }
        else{
            System.err.println("Distancias disponiveis: euclidean");
            System.exit(1);
        }
        Window = new ArrayList<>(wsize);
    }

    @Override
    public boolean test(Vector<Double> instance){

        if(Window.size() == 0){return false;}

        //Calcula a distancia euclidiana entre a instancia do parametro e as instancias da janela
        Stream<InstanceDouble> distances = Window.stream().map(s -> new InstanceDouble(s, Similarity.EuclideanDistance(s, instance)));

        //Ordena as distancias
        distances = distances.sorted(
                new Comparator<InstanceDouble>() {
                    @Override
                    public int compare(InstanceDouble o1, InstanceDouble o2) {
                        if(o1.value > o2.value){return 1;}
                        else if(o1.value < o2.value){return -1;}
                        return 0;
                    }
                }
        );

        List<InstanceDouble> K_neighbours;
        //Pega os K vizinhos

        if (Window.size() < K){
            K_neighbours = new ArrayList<InstanceDouble>(distances.collect(Collectors.toList()).subList(0, Window.size()));
        }else{
            K_neighbours = new ArrayList<InstanceDouble>(distances.collect(Collectors.toList()).subList(0, K));
        }


        Map majorvote = new HashMap<Double, Integer>();

        for(InstanceDouble x: K_neighbours){
            if(majorvote.containsKey(x.key.get(x.key.size()-1))){
                Integer aux = (Integer)majorvote.get(x.key.get(x.key.size()-1));
                majorvote.put(x.key.get(x.key.size()-1), aux + 1);
            }else{
                majorvote.put(x.key.get(x.key.size()-1), 1);
            }
        }

        Integer bestclass_vote = -600;
        Double bestclass_label = -600.0;

        Iterator<Map.Entry<Double, Integer>> it = majorvote.entrySet().iterator();

        while(it.hasNext()){
            Map.Entry<Double, Integer> pair = it.next();
            if(pair.getValue() > bestclass_vote){
                bestclass_label = pair.getKey();
                bestclass_vote = pair.getValue();
            }
        }

        Double targetclass = instance.get(instance.size()-1);

        if(targetclass.equals(bestclass_label))
            return true;

        return false;
    }

    @Override
    public synchronized void train(Vector<Double> instance){
        if (Window.size() < WindowSize) {
            Window.add(instance);
        }
        else{
            Window.remove(0);
            Window.add(instance);
        }
    }
}
