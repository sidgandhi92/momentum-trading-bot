package ca.mcgill.dp2.group52.core;

import java.io.*;
import java.util.concurrent.*;
import java.util.Properties;

import ca.mcgill.dp2.group52.enums.Company;
import ca.mcgill.dp2.group52.logging.LogUtil;
import static java.util.concurrent.TimeUnit.*;
import java.util.concurrent.BlockingQueue;


public class Core {

    //public Logger logger;

    protected LinkedBlockingQueue<String> q;

    private CoreScheduler scheduler;
    private Network cn;

    protected int loss_threshold, valuation_threshold;
    protected String data_period = "180 S", data_granularity = "3 mins";

    protected DataSet data_set;
    protected VolatilityDataSet vds;

    public Core() {
        //pool = Executors.newScheduledThreadPool(2);
        deserialize();

        data_set = new DataSet();
        vds = new VolatilityDataSet();
        q = new LinkedBlockingQueue<String>(100);

        //logger = new Logger(q);
        cn = new Network(this, data_set, vds, q);
        scheduler = new CoreScheduler(this, cn, q);
    }

    public void run() throws IOException {
        System.out.print("\nWelcome to MLTrader.\nAttempting connection to TWS...");
        cn.connect();

        if (!cn.check_connection()) {
            //TODO handle error condition
            System.out.print("\nConnection failure. Terminating...");
            return;
        }

        System.out.print("\nConnection successful.");

        scheduler.start_logger(q);

        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        for (;;) {
            String line = "";
            System.out.print("\n> ");

            String[] cmd = get_cmd(line, isr, br);

            proc_cmd(cmd);

            /*if(!cn.check_connection())
                break;*/
        }

        //br.close();
        //isr.close();
    }

    private void deserialize() {
        Properties prop = new Properties();
        InputStream is = null;

        try {
            is = new FileInputStream("mlt_config.properties");
            prop.load(is);

            scheduler.loss_threshold = Integer.parseInt(prop.getProperty("loss_threshold"));
            scheduler.valuation_threshold = Integer.parseInt(prop.getProperty("valuation_threshold"));
            this.data_period = prop.getProperty("data_period");
            this.data_granularity = prop.getProperty("data_granularity");

        } catch (FileNotFoundException e) {
            //TODO
            System.out.println("No configuration file found; using default settings...");
        } catch (IOException e) {
            //TODO
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    //TODO
                }
            }
        }
    }

    private void serialize() {
        Properties prop = new Properties();
        OutputStream os = null;

        try {
            os = new FileOutputStream("mlt_config.properties");

            prop.setProperty("loss_threshold", Integer.toString(scheduler.loss_threshold));
            prop.setProperty("valuation_threshold", Integer.toString(scheduler.valuation_threshold));
            prop.setProperty("data_period", data_period);
            prop.setProperty("data_granuality", data_granularity);

            prop.store(os, null);
        } catch (FileNotFoundException e) {
            //TODO
            e.printStackTrace();
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    //TODO
                }
            }
        }
    }

    protected void set_data_time_params(String period, String granularity) {
        this.data_granularity = granularity;
        this.data_period = period;
    }

    protected void set_valuation_threshold(int value) {
        //;
    }

    private String[] get_cmd(String line, InputStreamReader isr, BufferedReader br) {
        //String line = "";

        try {

            line = br.readLine();

            //br.close();
            //isr.close();

        } catch (Exception e) {
            LogUtil.log("\nEXCEPTION while reading user input: " + e.getMessage());
        }

        return line.split(" ");
    }

    private void proc_cmd(String[] input) {
        String cmd = input[0];

        switch (cmd) {
            case "set":
                cmd_set(input);
                break;
            case "get":
                cmd_get(input);
                break;
            case "connect":
                cn.connect();
                break;
            case "buy":
                cmd_user_order(input);
                break;
            case "sell":
                cmd_user_order(input);
                break;
            case "portfolio":
                cmd_portfolio();
                break;
            case "begin":
                cmd_start(input);
                break;
            default:
                break;
        }
    }

    private void cmd_start(String[] input) {
        //scheduler.schedule_volatility_analysis();
        scheduler.lt_refresh();

        try {
            scheduler.schedule_trading_routine();
            scheduler.schedule_lt_refresh();
        } catch (InterruptedException e) {
            cn.error(e);
        }
    }

    private void cmd_user_order(String[] input) {
        Company company = Company.valueOf(input[1]);
        int quantity = Integer.parseInt(input[2]);
    
        cn.place_order(company, quantity, input[0].toUpperCase(), (byte)0);
    }
    
    private void cmd_set(String[] input) {
        int value = 0;

        try {
            value = Integer.parseInt(input[2]);
        } catch (NumberFormatException e) {
            LogUtil.log("\nEXCEPTION while attempting cast to integer: " + e.getMessage());
            System.out.print("\nValue was invalid, setting not changed.");
            return;
        }

        if (input[1].contentEquals("valuation_threshold")) {
            set_valuation_threshold(value);
            LogUtil.log("\nSETTING CHANGED: Valuation Threshold now is "  + value);
        } else {
            //set_data_granularity(value);
            LogUtil.log("\nSETTING CHANGED: Loss Threshold now is " + value);
        }
    }

    private void cmd_portfolio() {

    }

    private void cmd_get(String[] input) {

    }
}
