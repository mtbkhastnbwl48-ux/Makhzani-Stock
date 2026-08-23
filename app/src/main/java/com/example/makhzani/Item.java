package com.example.makhzani;

public class Item {

    private int id;
    private String name;
    private String code;
    private int quantity;

    public Item(int id, String name, String code, int quantity) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.quantity = quantity;
    }

    public Item(String name, String code, int quantity) {
        this(0, name, code, quantity);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
