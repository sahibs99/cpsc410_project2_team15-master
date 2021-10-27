package com.company;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart extends Vehicle{

    private int id;
    private List<Integer> productIDs;

    private void addProducts(List<Integer> newProducts){
        productIDs = newProducts;
        return;
    }

    private void removeEverySingleProductFromShoppingCart() {
        productIDs = new ArrayList<>();
        return;
    }


}
