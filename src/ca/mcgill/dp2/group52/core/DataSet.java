package ca.mcgill.dp2.group52.core;

import ca.mcgill.dp2.group52.enums.Company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.concurrent.CountDownLatch;

/**
 * Created by sgandhi on 2/1/16.
 */
public class DataSet {

    // The key is the ordinal value of the Company enum, the List of integers contains prices
    // returned by the getMktData() method
    protected ArrayList[] data;
    //protected HashMap<Integer, List<Double>> data;
    protected CountDownLatch[] latches;
    protected byte[] ready;

    public DataSet() {
        ready = new byte[30];
        data = new ArrayList[30];
        
        init_all();
        //data = new HashMap<Integer, List<Double>>();
    }
    
    private void init_all() {
        for (int i = 0; i<30; i++) {
            data[i] = new ArrayList<Double>();
            latches[i] = new CountDownLatch(1);
        }
    }
    

    public List<Double> get_price_data(Company company) {
        return data[company.ordinal()];
    }

    public void set_data(Company company, int dataType, double value) {
        //Asynchronous or synchronous?
        data[company.ordinal()].set(dataType, value);
    }

    public void set_ready(Company company) {
        ready[company.ordinal()] = 1;
    }

    public void set_ready(int company_int) {
        ready[company_int] = 1;
    }

    public void not_ready(Company company) {
        ready[company.ordinal()] = 0;
    }

    public void not_ready(int company_int) {
        ready[company_int] = 0;
    }

    public boolean is_ready(Company company) {
        return (ready[company.ordinal()] == 1) ? true : false;
    }



}
