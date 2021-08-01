package com.mobiquity.service;

import com.mobiquity.domain.Package;
import com.mobiquity.domain.PackageBuilder;
import com.mobiquity.domain.PackageEntry;
import com.mobiquity.exception.APIException;
import com.mobiquity.parser.PackageParser;
import com.mobiquity.utilities.FileReaderUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PackageService {

    // an integration method that organizes the workflow of data treatment extract -> transform -> parse -> returns the loaded final Packages
    public static List<Package> loadPackages(String filePath) throws APIException {
        List<Package> packageListResult = new ArrayList<>();
        List<String> rawData = FileReaderUtils.extractFileData(filePath);

        for (String rawPackage : FileReaderUtils.transformFileData(rawData)) {
            packageListResult.add(PackageParser.parsePackage(rawPackage));
        }
        return pack(packageListResult);
    }

    // calls the packPackageEntries after sorting the packageEntries in DESC order by cost per weight ratio else if the ratios are equal by higher cost first, then returns a packed package.
    public static List<Package> pack(List<Package> packageList) {

        return packageList.stream().map(pack -> PackageBuilder.builder(pack)
                .withPackageEntries(packPackageEntries(pack.getPackageEntries().stream().sorted(Collections.reverseOrder())
                        .collect(Collectors.toList()), new ArrayList<>(), pack.getMaxWeight(), 0f)).get()).collect(Collectors.toList());
    }

    // after receiving a sorted array of packageEntries (by cost per ratio desc) we choose the first most efficient package entry and while not surpassing the weight limit
    // we continue until we do a pass on all the elements (in case of equality of cost by weight ration we use the one with the higher cost (if they have the same
    // cost we choose one of them at random))
    private static List<PackageEntry> packPackageEntries(List<PackageEntry> packageList, List<PackageEntry> packageListResult, float maxWeight, float currentWeight) {
        if (packageList.isEmpty()) {
            return packageListResult;
        }
        PackageEntry mostEfficientPackage = packageList.remove(0);
        if (Float.compare(Float.sum(currentWeight, mostEfficientPackage.getWeight()), maxWeight) <= 0) {
            packageListResult.add(mostEfficientPackage);
            currentWeight = Float.sum(currentWeight, mostEfficientPackage.getWeight());
        }

        return packPackageEntries(packageList, packageListResult, maxWeight, currentWeight);
    }
}
