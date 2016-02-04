package ca.mcgill.dp2.group52.core;

import java.io.*;
import java.util.concurrent.*;
import java.util.Properties;
import ca.mcgill.dp2.group52.logging.LogUtil;
import static java.util.concurrent.TimeUnit.*;


public class Core {

    private final ScheduledExecutorService pool;

    private CoreScheduler scheduler;
    private Network cn;

    protected int loss_threshold, valuation_threshold;
    protected String data_period, data_granularity;

    protected DataSet data_set;

    public Core() {
        pool = Executors.newScheduledThreadPool(2);
        deserialize();

        data_set = new DataSet();
    }

    public void run() {
        cn = new Network(this, data_set);
        scheduler = new CoreScheduler(this, cn);

        System.out.print("\nWelcome to MLTrader.\nAttempting connection to TWS...");
        cn.connect();

        if (!cn.check_connection()) {
            //TODO handle error condition
            System.out.print("\nConnection failure. Terminating...");
            return;
        }

        System.out.print("\nConnection successful.");

        scheduler.schedule_all();

        for (;;) {
            System.out.print("\n> ");

            String[] cmd = get_cmd();

            proc_cmd(cmd);
        }
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

    protected void set_loss_threshold(int value) {
        scheduler.loss_threshold = value;
    }

    protected void set_valuation_threshold(int value) {
        scheduler.valuation_threshold = value;
    }

    private String[] get_cmd() {
        String line = "";

        try {

            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);

            line = br.readLine();

            br.close();
            isr.close();

        } catch (IOException e) {
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
                cmd_portfolio(input);
            default:
                break;
        }
    }
    
    private void cmd_user_order(String[] input) {
        Company company = Company.valueOf(input);
        int quantity = Integer.parseInt(input[2]);
    
        cn.place_user_order(company, quantity, input[0].toUpperCase());
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
            set_loss_threshold(value);
            LogUtil.log("\nSETTING CHANGED: Loss Threshold now is " + value);
        }
    }

    private void cmd_get(String[] input) {

    }
}
