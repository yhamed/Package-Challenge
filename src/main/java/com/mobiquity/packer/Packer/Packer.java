package com.mobiquity.packer.Packer;

import com.mobiquity.domain.Package;
import com.mobiquity.domain.PackageEntry;
import com.mobiquity.exception.APIException;
import com.mobiquity.service.PackageService;

import java.util.List;

public class Packer {

    public static String pack(String filePath) throws APIException {
        List<Package> packagedList = PackageService.loadPackages(filePath);
        StringBuilder packagingResult = new StringBuilder();

        packagedList.stream().forEach(pack -> {
            packagingResult.append("\n");
            List<PackageEntry> packageEntries = pack.getPackageEntries();
            for (int index = 0; index < packageEntries.size(); index += 2) {
                packagingResult.append(packageEntries.get(index).getIndexNumber());

                if (index + 1 < packageEntries.size()) {
                    packagingResult.append(",")
                            .append(packageEntries.get(index + 1).getIndexNumber());
                }
                packagingResult.append("\n");
            }
            packagingResult.append("-");
        });

        return packagingResult.toString().substring(1, packagingResult.length() - 1);
    }
}
