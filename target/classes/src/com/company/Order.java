package com.company;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private int id;
    private String Date;
    private int $totalCost$;
    private Store orderLocation;

    private void CANCELORDER(){
        id = -1;
        return;
    }

    private int addToTotalSales() {
        orderLocation.totalSales = $totalCost$ + orderLocation.totalSales;
        return orderLocation.totalSales;
    }


}
