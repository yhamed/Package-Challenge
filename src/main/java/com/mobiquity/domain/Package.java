package com.mobiquity.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Package implements Serializable {

    private static final long serialVersionUID = -3201010390319695890L;

    private float maxWeight;
    private final List<PackageEntry> packageEntries;

    public Package(PackageBuilder packageBuilder) {
        this.maxWeight = packageBuilder.maxWeight;
        this.packageEntries = packageBuilder.packageEntries;
    }

    public float getMaxWeight() {
        return maxWeight;
    }

    public List<PackageEntry> getPackageEntries() {
        return packageEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Package aPackage = (Package) o;
        return Float.compare(aPackage.getMaxWeight(), getMaxWeight()) == 0 &&
                Objects.equals(getPackageEntries(), aPackage.getPackageEntries());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaxWeight(), getPackageEntries());
    }

    @Override
    public String toString() {
        return "Package{" +
                "maxWeight=" + maxWeight +
                ", packageEntries=" + packageEntries +
                '}';
    }
}
