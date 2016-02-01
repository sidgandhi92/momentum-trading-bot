package ca.mcgill.dp2.group52.core;

import ca.mcgill.dp2.group52.enums.Company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sgandhi on 2/1/16.
 */
public class DataSet {

    // The key is the ordinal value of the Company enum, the List of integers contains prices
    // returned by the getMktData() method
    protected HashMap<Integer, List<Double>> data;
    protected byte[] ready;

    public DataSet() {
        ready = new byte[30];

        data = new HashMap<Integer, List<Double>>();
    }

    public List<Double> get_price_data(Company company) {
        return data.get(company.ordinal());
    }

    public List<Double> set_data(Company company, int dataType, double price) {
        //Asynchronous or synchronous?
        List<Double> list = data.get(company.ordinal());

        data.put(company.ordinal(), data.)

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
