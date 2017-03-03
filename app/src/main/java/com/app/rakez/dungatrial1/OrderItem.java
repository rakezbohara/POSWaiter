package com.app.rakez.dungatrial1;

/**
 * Created by RAKEZ on 2/16/2017.
 */
public class OrderItem {
    private String itemName;
    private String itemQty;
    private String itemId;

    public OrderItem(String itemName, String itemQty, String itemId) {
        this.itemName = itemName;
        this.itemQty = itemQty;
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemQty() {
        return itemQty;
    }

    public void setItemQty(String itemQty) {
        this.itemQty = itemQty;
    }
}
