package com.mobiquity.domain;

import java.util.ArrayList;
import java.util.Collection;

public class PackageBuilder {
    protected float maxWeight;
    protected Collection<PackageEntry> packageEntries = new ArrayList<>();

    public static PackageBuilder builder() {
        return new PackageBuilder();
    }

    public PackageBuilder withMaxWeight(float maxWeight) {
        this.maxWeight = maxWeight;
        return this;
    }

    public PackageBuilder withPackageEntries(Collection<PackageEntry> packageEntry) {
        this.packageEntries.addAll(packageEntry);
        return this;
    }

    public Package get() {
        return new Package(this);
    }
}
