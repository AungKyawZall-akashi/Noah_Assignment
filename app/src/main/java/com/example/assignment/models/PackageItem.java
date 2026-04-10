package com.example.assignment.models;

public class PackageItem {
    private final String id;
    private final String name;
    private final String description;
    private final double priceUsd;

    public PackageItem(String id, String name, String description, double priceUsd) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priceUsd = priceUsd;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPriceUsd() {
        return priceUsd;
    }
}
