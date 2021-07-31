package com.mobiquity.parser;

import com.mobiquity.domain.Package;
import com.mobiquity.domain.PackageBuilder;
import com.mobiquity.domain.PackageEntry;
import com.mobiquity.domain.PackageEntryBuilder;
import com.mobiquity.exception.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Float.parseFloat;
import static java.lang.Long.parseLong;

@Service
public class PackageParser {
    private final static String EURO_CURRENCY_SIGN = "€";

    public Package parsePackage(String rawPackage) throws ApiException {
        try {
            String[] rawPackageData = rawPackage.split(":");
            checkDataSize(rawPackageData, 2);
            float maxWeight = Float.parseFloat(rawPackageData[0]);

            weightExceedsMaxLimit(maxWeight);

            return PackageBuilder.builder().withMaxWeight(maxWeight)
                    .withPackageEntries(mapPackageEntries(rawPackageData[1])).get();

        } catch (NumberFormatException numberFormatException) {
            StringBuilder apiExceptionMessage = new StringBuilder(ApiException.CORRUPTED_PACKAGE_DATA_PARSE);
            apiExceptionMessage.append("\n");
            apiExceptionMessage.append(rawPackage);
            throw new ApiException(apiExceptionMessage.toString());
        }
    }

    public List<PackageEntry> mapPackageEntries(String rawPackageEntries) throws ApiException {
        return mapPackageEntries( new ArrayList(Arrays.asList(rawPackageEntries.split("\\|"))),new ArrayList(),0f);
    }

    private List<PackageEntry> mapPackageEntries(List<String> rawPackageEntries, List<PackageEntry> packageEntries, float temporaryWeight) throws ApiException {
        if (rawPackageEntries.isEmpty()) {
            if (packageEntries.isEmpty()) {
                throw new ApiException(ApiException.CORRUPTED_PACKAGE_DATA);
            }
            if(packageEntries.size() > 15) {
                throw new ApiException(ApiException.CORRUPTED_PACKAGE_DATA_SURPASSED_MAX_CAPACITY);
            }
            return packageEntries;
        }
        PackageEntry packageEntry = parsePackageEntry(rawPackageEntries.remove(0));
        temporaryWeight += packageEntry.getWeight();

        weightExceedsMaxLimit(temporaryWeight);
        packageEntries.add(packageEntry);

        return mapPackageEntries(rawPackageEntries, packageEntries, temporaryWeight);
    }

    private void weightExceedsMaxLimit(float compare) throws ApiException {
        if (Float.compare(compare, 100f) > 0) {
            throw new ApiException(ApiException.CORRUPTED_PACKAGE_DATA_SURPASSED_MAX_WEIGHT);
        }
    }

    public PackageEntry parsePackageEntry(String rawPackageEntry) throws ApiException {
        String[] rawPackageEntryData = rawPackageEntry.split(",");

        checkDataSize(rawPackageEntryData, 3);
        String rawCost = rawPackageEntryData[2];
        if (StringUtils.countOccurrencesOf(rawCost, EURO_CURRENCY_SIGN) != 1) {
            throw new ApiException(ApiException.CORRUPTED_PACKAGE_DATA_PARSE);
        }
        try {
            float packageEntryWeight = parseFloat(rawPackageEntryData[1]);
            weightExceedsMaxLimit(packageEntryWeight);
            return PackageEntryBuilder.builder().withIndexNumber(parseLong(rawPackageEntryData[0]))
                    .withWeight(packageEntryWeight)
                    .withCost(parseFloat(rawCost.replace(EURO_CURRENCY_SIGN, ""))).get();
        } catch (NumberFormatException numberFormatException) {
            StringBuilder apiExceptionMessage = new StringBuilder(ApiException.CORRUPTED_PACKAGE_DATA_PARSE);
            apiExceptionMessage.append("\n");
            apiExceptionMessage.append(rawPackageEntry);
            throw new ApiException(apiExceptionMessage.toString());
        }
    }

    private void checkDataSize(String[] rawPackageData, int expectedSize) throws ApiException {
        if (rawPackageData == null || rawPackageData.length != expectedSize) {
            throw new ApiException(ApiException.CORRUPTED_PACKAGE_DATA);
        }
    }
}
