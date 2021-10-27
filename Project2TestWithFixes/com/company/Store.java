package com.company;

import java.util.ArrayList;
import java.util.List;

public class Store {

    private String Location;
    int totalSales = 0;

    public void setTotalSales(int sales) {
        totalSales = sales;
    }

    private int addToTotalSales(int newSales) {
        totalSales = newSales + totalSales;
        return totalSales;
    }
}
