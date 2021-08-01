package com.mobiquity.parser;

import com.mobiquity.domain.Package;
import com.mobiquity.domain.PackageBuilder;
import com.mobiquity.domain.PackageEntry;
import com.mobiquity.domain.PackageEntryBuilder;
import com.mobiquity.exception.APIException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Float.parseFloat;
import static java.lang.Long.parseLong;

// this class serves as a first pass on the raw data extracted from the file system to parse, do a basic functional rule check and parse the package data before sorting each relative package
public class PackageParser {
    private final static String EURO_CURRENCY_SIGN = "€";

    // after extracting and transforming the data from the file system this function returns all the packages with all the possible entries given it respects the functional rules
    public static Package parsePackage(String rawPackage) throws APIException {
        try {
            String[] rawPackageData = rawPackage.split(":");
            checkDataSize(rawPackageData, 2);
            float maxWeight = Float.parseFloat(rawPackageData[0]);

            weightOrCostExceedsMaxLimit(maxWeight);

            return PackageBuilder.builder().withMaxWeight(maxWeight)
                    .withPackageEntries(mapPossiblePackageEntries(rawPackageData[1])).get();

        } catch (NumberFormatException numberFormatException) {
            StringBuilder apiExceptionMessage = new StringBuilder(APIException.CORRUPTED_PACKAGE_DATA_PARSE);
            apiExceptionMessage.append("\n");
            apiExceptionMessage.append(rawPackage);
            throw new APIException(apiExceptionMessage.toString());
        }
    }

    public static List<PackageEntry> mapPossiblePackageEntries(String rawPackageEntries) throws APIException {

        return mapPossiblePackageEntries(new ArrayList(Arrays.asList(rawPackageEntries.split("\\|"))),new ArrayList());
    }

    // for a given single package it checks basic functional rules and extracts the possible package entries to a given package.
    private static List<PackageEntry> mapPossiblePackageEntries(List<String> rawPackageEntries, List<PackageEntry> packageEntries) throws APIException {
        if (rawPackageEntries.isEmpty()) {
            if (packageEntries.isEmpty()) {
                throw new APIException(APIException.CORRUPTED_PACKAGE_DATA);
            }
            if(packageEntries.size() > 15) {
                throw new APIException(APIException.CORRUPTED_PACKAGE_DATA_SURPASSED_MAX_CAPACITY);
            }
            return packageEntries;
        }
        PackageEntry packageEntry = parsePackageEntry(rawPackageEntries.remove(0));
        packageEntries.add(packageEntry);

        return mapPossiblePackageEntries(rawPackageEntries, packageEntries);
    }

    // checks if cost or weight exceed their limit of a 100.
    private static void weightOrCostExceedsMaxLimit(float weightOrCost) throws APIException {
        if (Float.compare(weightOrCost, 100f) > 0) {
            throw new APIException(APIException.CORRUPTED_PACKAGE_DATA_SURPASSED_MAX_WEIGHT);
        }
    }

    // for a given package entry checks if it is well structured and parses it.
    public static PackageEntry parsePackageEntry(String rawPackageEntry) throws APIException {
        String[] rawPackageEntryData = rawPackageEntry.split(",");

        checkDataSize(rawPackageEntryData, 3);
        String rawCost = rawPackageEntryData[2];
        if (StringUtils.countOccurrencesOf(rawCost, EURO_CURRENCY_SIGN) != 1) {
            throw new APIException(APIException.CORRUPTED_PACKAGE_DATA_PARSE);
        }
        try {
            float packageEntryWeight = parseFloat(rawPackageEntryData[1]);
            float packageEntryCost = parseFloat(rawCost.replace(EURO_CURRENCY_SIGN, ""));
            weightOrCostExceedsMaxLimit(packageEntryWeight);
            weightOrCostExceedsMaxLimit(packageEntryCost);

            return PackageEntryBuilder.builder().withIndexNumber(parseLong(rawPackageEntryData[0]))
                    .withWeight(packageEntryWeight)
                    .withCost(packageEntryCost).get();
        } catch (NumberFormatException numberFormatException) {
            StringBuilder apiExceptionMessage = new StringBuilder(APIException.CORRUPTED_PACKAGE_DATA_PARSE);
            apiExceptionMessage.append("\n");
            apiExceptionMessage.append(rawPackageEntry);
            throw new APIException(apiExceptionMessage.toString());
        }
    }

    // be it a package or a package entry checks the size of information we expect to have, in case of size mismatch throws a APIException.
    private static void checkDataSize(String[] rawPackageData, int expectedSize) throws APIException {
        if (rawPackageData == null || rawPackageData.length != expectedSize) {
            throw new APIException(APIException.CORRUPTED_PACKAGE_DATA);
        }
    }
}
