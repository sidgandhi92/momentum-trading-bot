package ca.mcgill.dp2.group52.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sgandhi on 2/4/16.
 */
public class Logger implements Runnable {

    private LinkedBlockingQueue<String> bq;
    BufferedWriter br;
    FileWriter fw;
    Byte b;

    public Logger (LinkedBlockingQueue<String> bq) {
        this.bq = bq;
    }

    public void run() {
        b = 1;
        Date now = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("MMM-dd");
        File log = new File("log_" + df.format(now) + ".txt");

        try {
            if (!log.exists())
                log.createNewFile();

            fw = new FileWriter(log);
            br = new BufferedWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (;;) {
            if (b == 0)
                break;

            try {
                String s = bq.take();

                br.write(s + "\n");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (br != null)
                br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
