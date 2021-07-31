package com.mobiquity.service;

import com.mobiquity.domain.Package;
import com.mobiquity.domain.PackageBuilder;
import com.mobiquity.domain.PackageEntry;
import com.mobiquity.exception.APIException;
import com.mobiquity.parser.PackageParser;
import com.mobiquity.utilities.FileReaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackageService implements IPackageService {

    @Autowired
    private PackageParser packageParser;

    // an integration method that organizes the workflow of data treatment extract -> transform -> parse
    @Override
    public List<Package> readPackages(String filePath) throws APIException {
        List<Package> packageListResult = new ArrayList<>();
        List<String> rawData = FileReaderUtils.extractFileData(filePath);

        for (String rawPackage : FileReaderUtils.transformFileData(rawData)) {
            packageListResult.add(packageParser.parsePackage(rawPackage));
        }

        return packageListResult;
    }

    @Override
    public List<Package> pack(List<Package> packageList) {

        return packageList.stream().map(pack -> PackageBuilder.builder(pack)
                .withPackageEntries(packPackageEntries(pack.getPackageEntries().stream().sorted(Collections.reverseOrder())
                        .collect(Collectors.toList()), new ArrayList<>(), pack.getMaxWeight(), 0f)).get()).collect(Collectors.toList());
    }

    // after receiving a sorted array of packageEntries (by cost per ratio desc) we choose the first most efficient package entry and while not surpassing the weight limit
    // we continue until we do a pass on all out elements
    private List<PackageEntry> packPackageEntries(List<PackageEntry> packageList, List<PackageEntry> packageListResult, float maxWeight, float currentWeight) {
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
