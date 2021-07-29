package com.mobiquity.domain;

public class PackageEntryBuilder {
    protected long indexNumber;
    protected float weight;
    protected float cost;

    public static PackageEntryBuilder builder() {
        return new PackageEntryBuilder();
    }

    public PackageEntryBuilder withIndexNumber(long indexNumber) {
        this.indexNumber = indexNumber;
        return this;
    }

    public PackageEntryBuilder withWeight(float weight) {
        this.weight = weight;
        return this;
    }

    public PackageEntryBuilder withCost(float cost) {
        this.cost = cost;
        return this;
    }

    public PackageEntry get() {
        return new PackageEntry(this);
    }
}
