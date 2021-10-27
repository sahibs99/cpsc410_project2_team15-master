package com.company;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private int id;
    private String Date;
    private int totalCost;
    private Store orderLocation;

    private void cancelOrder(){
        id = -1;
        return;
    }

}
