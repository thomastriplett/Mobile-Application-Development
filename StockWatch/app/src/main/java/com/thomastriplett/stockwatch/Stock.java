package com.thomastriplett.stockwatch;

/**
 * Created by Thomas on 3/5/2018.
 */

public class Stock implements Comparable{
    private String symbol;
    private String name;
    private double price;
    private double price_change;
    private double change_percentage;

    public Stock() {
        this.symbol = "TT";
        this.name = "Thomas Triplett";
        this.price = 100.00;
        this.price_change = 0.00;
        this.change_percentage = 0.00;
    }

    public Stock(String s, String n, double p, double pc, double cp) {
        this.symbol = s;
        this.name = n;
        this.price = p;
        this.price_change = pc;
        this.change_percentage = cp;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice_change() {
        return price_change;
    }

    public void setPrice_change(double price_change) {
        this.price_change = price_change;
    }

    public double getChange_percentage() {
        return change_percentage;
    }

    public void setChange_percentage(double change_percentage) {
        this.change_percentage = change_percentage;
    }

    @Override
    public int compareTo(Object o) {
        Stock s = (Stock) o;
        return this.symbol.compareTo(s.getSymbol());
    }
}
