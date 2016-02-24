package ca.mcgill.dp2.group52.core;

import ca.mcgill.dp2.group52.enums.Company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * Created by sgandhi on 2/1/16.
 */
public class DataSet {

    // The key is the ordinal value of the Company enum, the List of integers contains prices
    // returned by the getMktData() method
    public double[] lt_data;
    public double[] st_data;
    public byte[] last_compare;
    public Semaphore[] st_semaphore;
    public Semaphore[] lt_semaphore;
    public CountDownLatch latch;

    public DataSet() {
        latch = new CountDownLatch(10);
        lt_data = new double[30];
        st_data = new double[30];
        last_compare = new byte[30];
        st_semaphore = new Semaphore[30];
        lt_semaphore = new Semaphore[30];
        
        init_all();
        //data = new HashMap<Integer, List<Double>>();
    }
    
    private void init_all() {
        for (int i = 0; i<30; i++) {
            st_semaphore[i] = new Semaphore(0, true);
            lt_semaphore[i] = new Semaphore(1, true);
        }
    }
    
    public void set_st_data(Company company, double wap) {
        int r = company.ordinal();
        st_data[company.ordinal()] = wap;
        st_semaphore[r].release();
    }
    
    public void set_lt_data(Company company, double wap) {
        int r = company.ordinal();
        lt_semaphore[r].acquireUninterruptibly();
        lt_data[r] = wap;
        lt_semaphore[r].release();
    }
}
