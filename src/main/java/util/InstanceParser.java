package util;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Created by loezerl-fworks on 19/10/17.
 */
public class InstanceParser{

    public static Vector<Double> Parser(String element){
        List<String> Elements = Arrays.asList(element.split(" "));

        Vector<Double> Array = new Vector<>();
        for(String el: Elements){
            Array.add(Double.parseDouble(el));
        }
        return Array;
    }
}
