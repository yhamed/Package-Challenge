package com.mobiquity.domain;

import java.io.Serializable;
import java.util.Objects;

public class PackageEntry implements Serializable, Comparable<PackageEntry>{

    private static final long serialVersionUID = -1154114435785633780L;

    private long indexNumber;
    private float weight;
    private float cost;

    public PackageEntry(PackageEntryBuilder packageEntryBuilder) {
        this.indexNumber = packageEntryBuilder.indexNumber;
        this.weight = packageEntryBuilder.weight;
        this.cost = packageEntryBuilder.cost;
    }

    public long getIndexNumber() {
        return indexNumber;
    }

    public float getWeight() {
        return weight;
    }

    public float getCost() {
        return cost;
    }

    private float getCostByWeightRatio() {
        return getCost() / getWeight();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageEntry aPackageEntry = (PackageEntry) o;
        return getIndexNumber() == aPackageEntry.getIndexNumber();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIndexNumber());
    }

    @Override
    public int compareTo(PackageEntry packageEntry) {
        int compareResult = Float.compare(this.getCostByWeightRatio(), packageEntry.getCostByWeightRatio());
        return compareResult == 0 ? Float.compare(this.cost, packageEntry.getCost()): compareResult;
    }

    @Override
    public String toString() {
        return "PackageEntry{" +
                "indexNumber=" + indexNumber +
                ", weight=" + weight +
                ", cost=" + cost +
                '}';
    }
}
