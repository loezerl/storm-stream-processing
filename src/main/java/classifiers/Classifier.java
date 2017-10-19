package classifiers;

import java.util.Vector;

/**
 * Created by loezerl-fworks on 19/10/17.
 */
public class Classifier {
    private static Classifier instance;

    protected Classifier(){}

    public static synchronized Classifier getInstance(){
        if(instance == null){
            instance = new Classifier();
        }
        return instance;
    }

    public static synchronized void setInstance(Classifier obj){
        instance = obj;
    }

    public boolean test(Vector<Double> instance){return false;}
    public synchronized void train(Vector<Double> instance){}

}
