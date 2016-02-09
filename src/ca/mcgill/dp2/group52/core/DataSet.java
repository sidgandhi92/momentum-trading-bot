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
    public double[] lt_data;
    public double[] st_data;
    public byte[] last_compare;
    public Semaphore[] st_semaphore;
    public Semaphore[] lt_semaphore;

    public DataSet() {
        lt_data = new double[30];
        st_data = new double[30];
        last_compare = new byte[30];
        semaphore = new Semaphore[30];
        
        init_all();
        //data = new HashMap<Integer, List<Double>>();
    }
    
    private void init_all() {
        for (int i = 0; i<30; i++) {
            st_semaphore[i] = new Semaphore(1, true);
            lt_semaphore[i] = new Semaphore(1, true);
        }
    }
    
    public void set_st_data(Company company, double wap) {
        int r = company.ordinal();
        st_semaphore[r].acquireUninterruptibly();
        st_data[company.ordinal()] = wap;
        st_semaphore[r].release();
    }
    
    public void set_lt_data(Company company, double wap) {
        int r = company.ordinal();
        lt_semaphore[r].acquireUninterruptibly();
        lt_data[r] = wap;
        lt_semaphore[r].release();
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
