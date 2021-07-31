package com.mobiquity.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PackageBuilder {
    protected float maxWeight;
    protected List<PackageEntry> packageEntries;

    public PackageBuilder() {
    }

    public PackageBuilder(Package pack) {
        this.withMaxWeight(pack.getMaxWeight());
        this.withPackageEntries(pack.getPackageEntries());
    }

    public static PackageBuilder builder() {
        return new PackageBuilder();
    }

    public static PackageBuilder builder(Package pack) {
        return new PackageBuilder(pack);
    }

    public PackageBuilder withMaxWeight(float maxWeight) {
        this.maxWeight = maxWeight;
        return this;
    }

    public PackageBuilder withPackageEntries(Collection<PackageEntry> packageEntry) {
        this.packageEntries = new ArrayList<>();
        this.packageEntries.addAll(packageEntry);
        return this;
    }

    public Package get() {
        return new Package(this);
    }
}
