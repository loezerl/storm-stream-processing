package util;

import java.util.Vector;

/**
 * Created by loezerl-fworks on 19/10/17.
 */
public class Similarity {

    public static double EuclideanDistance(Vector<Double> a, Vector<Double> b){
        double dist = 0.0;

        if(a.size() != b.size()){
            System.err.println("Instances with different sizes.");
            System.err.println("Instance a: " + a.toString());
            System.err.println("Instance b: " + b.toString());
            return 999;
        }

        for(int i=0; i<a.size(); i++){
            if(a.size()-1 != i){
                double x = a.get(i);
                double y = b.get(i);
                dist += (x - y) * (x - y);
            }
        }
        return Math.sqrt(dist);
    }
}