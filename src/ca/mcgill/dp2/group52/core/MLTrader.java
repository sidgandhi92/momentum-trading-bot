package ca.mcgill.dp2.group52.core;

import ca.mcgill.dp2.group52.enums.Company;
import com.ib.client.Contract;
import com.ib.client.TagValue;

import java.util.Vector;

/**
 * Created by sgandhi on 1/23/16.
 */
public class MLTrader {

    public static void main(String[] args) throws Exception {
        /*Core app = new Core();
        app.run();*/

        Network network = new Network(null, null);
        network.connect();

        while(!network.check_connection());

        System.out.println("Connected to API");

        //network.request_mktData(Company.AXP);
        //network.request_mktData(Company.CAT);
        /*network.request_mktData(Company.BA);*/
        //network.request_mktData(Company.AAPL);
        network.request_histData(Company.AAPL);
        //network.request_mktData(Company.BA);
        //network.request_mktData(Company.CAT);
        //network.request_mktData(Company.IBM);
        //network.cancel_mktData(Company.DIS);

        //Thread.sleep(100000);

        //network.request_mktData(Company.AAPL);
        //network.cancel_mktData(Company.AAPL);

    }
}
