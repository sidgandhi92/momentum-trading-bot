package ca.mcgill.dp2.group52.enums;

import com.ib.client.Contract;

/**
 * Created by sgandhi on 1/23/16.
 */
public enum Company {
    AXP,
    AAPL,
    BA,
    CAT,
    CVX,
    CSCO,
    KO,
    DIS,
    DD,
    XOM,
    GE,
    GS,
    HD,
    IBM,
    INTC,
    JNJ,
    JPM,
    MCD,
    MMM,
    MRK,
    MSFT,
    NKE,
    PFE,
    PG,
    TRV,
    UTX,
    UNH,
    VZ,
    V,
    WMT;


    public Contract create_contract() {
        Contract contract = new Contract();

        contract.m_symbol = this.name();
        contract.m_exchange = "SMART";
        contract.m_secType = "STK";
        contract.m_currency = "USD";

        return contract;
    }
    
    public Order create_order(String buy_sell) {
        return create_order(buy_sell, 1);
    }
    
    public Order create_order(String buy_sell, int quantity) {
        Order order = new Order();
        
        order.m_action = buy_sell.toUpperCase();
        order.m_totalQuantity = quantity;
        order.m_orderType = "MKT";
        order.m_lmtPrice = 0;
        order.m_allOrNone = false;
    }
}
