package org.simmi.ann;

public class ANIResult {
    public int[] countarr;
    public double[] corrarr;

    public ANIResult(int size) {
        countarr = new int[size*size];
        corrarr = new double[size*size];
    }
}
