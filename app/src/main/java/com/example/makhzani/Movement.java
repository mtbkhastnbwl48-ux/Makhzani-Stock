package com.example.makhzani;

public class Movement {

    private int id;
    private int itemId;
    private String type;
    private int quantity;
    private int balanceBefore;
    private int balanceAfter;
    private String date;
    private String time;
    private String username;

    public Movement(
            int id,
            int itemId,
            String type,
            int quantity,
            int balanceBefore,
            int balanceAfter,
            String date,
            String time,
            String username) {

        this.id = id;
        this.itemId = itemId;
        this.type = type;
        this.quantity = quantity;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.date = date;
        this.time = time;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public int getItemId() {
        return itemId;
    }

    public String getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getBalanceBefore() {
        return balanceBefore;
    }

    public int getBalanceAfter() {
        return balanceAfter;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getUsername() {
        return username;
    }
}
