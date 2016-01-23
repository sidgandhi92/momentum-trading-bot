package ca.mcgill.dp2.group52.core;

import java.io.*;
import java.sql.Connection;
import java.util.concurrent.*;
import java.util.Properties;
import ca.mcgill.dp2.group52.logging.LogUtil;


public class Core {

    private final ExecutorService pool;

    protected int loss_threshold, valuation_threshold;

    public Core() {
        pool = Executors.newCachedThreadPool();
        deserialize();
    }

    public void run() {
        CoreNetwork cn = new CoreNetwork(this);

        System.out.print("\nWelcome to MLTrader.\nAttempting connection to TWS...");
        cn.init_connection();

        if (!cn.check_connection()) {
            //TODO handle error condition
            System.out.print("\nConnection failure. Terminating...");
            return;
        }

        System.out.print("\nConnection successful.");

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

            this.loss_threshold = Integer.parseInt(prop.getProperty("loss_threshold"));
            this.valuation_threshold = Integer.parseInt(prop.getProperty("valuation_threshold"));


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

            prop.setProperty("loss_threshold", Integer.toString(this.loss_threshold));
            prop.setProperty("valuation_threshold", Integer.toString(this.valuation_threshold));

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
        loss_threshold = value;
    }

    protected void set_valuation_threshold(int value) {
        valuation_threshold = value;
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

                break;
            default:
                break;
        }
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
