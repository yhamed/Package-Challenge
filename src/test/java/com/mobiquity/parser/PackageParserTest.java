package com.mobiquity.parser;

import com.mobiquity.domain.Package;


import java.util.List;

import com.mobiquity.domain.PackageEntry;
import com.mobiquity.exception.APIException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class PackageParserTest {

    @Test
    void testParsePackage() throws APIException {
        // setup
        String rawPackageData = "81:1,33.38,€45|2,58.62,€98";

        // test
        Package packageResult = PackageParser.parsePackage(rawPackageData);

        // assert
        assertThat(packageResult).isNotNull();
        assertThat(packageResult.getMaxWeight()).isEqualTo(81f);
    }

    @Test
    void testParsePackageWithCorruptedData() {
        // setup
        String corruptedPackageDataSample1 = "811,53.38,€45|2,88.62,€98";
        String corruptedPackageDataSample2 = "81:1,53.38,€45|2,88.62,€:98";

        // test
        Exception exception1 = assertThrows(APIException.class, () -> {
            PackageParser.parsePackage(corruptedPackageDataSample1);
        });
        Exception exception2 = assertThrows(APIException.class, () -> {
            PackageParser.parsePackage(corruptedPackageDataSample2);
        });

        // assert
        assertThat(exception1.getClass()).isEqualTo(APIException.class);
        assertThat(exception2.getClass()).isEqualTo(APIException.class);
        assertThat(exception1.getMessage()).isEqualTo(APIException.CORRUPTED_PACKAGE_DATA);
        assertThat(exception2.getMessage()).isEqualTo(APIException.CORRUPTED_PACKAGE_DATA);
    }

    @Test
    void testParsePackageEntry() throws APIException {
        // setup
        String rawPackageData = "2,88.62,€98";

        // test
        PackageEntry packageResult = PackageParser.parsePackageEntry(rawPackageData);

        // assert
        assertThat(packageResult).isNotNull();
        assertThat(packageResult.getIndexNumber()).isEqualTo(2l);
        assertThat(packageResult.getWeight()).isEqualTo(88.62f);
        assertThat(packageResult.getCost()).isEqualTo(98f);
    }

    @Test
    void testParsePackageEntryWithCorruptedData() {
        // setup
        String corruptedPackageEntrySample1 = "2,88.62,98";
        String corruptedPackageEntrySample2 = "2,8,8.62,€98";
        String corruptedPackageEntrySample3 = "badFormatOfIdentificationNumber,88.62,€98";
        String corruptedPackageEntrySample4 = "2,badFormatOfWeight,€98";
        String corruptedPackageEntrySample5 = "2,88.62,€€98";

        // test & assert
        assertThrows(APIException.class, () -> {
            PackageParser.parsePackageEntry(corruptedPackageEntrySample1);
        });
        assertThrows(APIException.class, () -> {
            PackageParser.parsePackageEntry(corruptedPackageEntrySample2);
        });
        assertThrows(APIException.class, () -> {
            PackageParser.parsePackageEntry(corruptedPackageEntrySample3);
        });
        assertThrows(APIException.class, () -> {
            PackageParser.parsePackageEntry(corruptedPackageEntrySample4);
        });
        assertThrows(APIException.class, () -> {
            PackageParser.parsePackageEntry(corruptedPackageEntrySample5);
        });
    }

    @Test
    void testMapPackageEntries() throws APIException {
        // setup
        String packageEntry = "1,3.38,€45|2,8.62,€98";

        // test
        List<PackageEntry> packageEntriesResult = PackageParser.mapPossiblePackageEntries(packageEntry);

        // assert
        assertThat(packageEntriesResult).isNotEmpty();
        assertThat(packageEntriesResult.size()).isEqualTo(2);
        assertThat(packageEntriesResult.get(0).getIndexNumber()).isEqualTo(1);
        assertThat(packageEntriesResult.get(1).getIndexNumber()).isEqualTo(2);
        assertThat(packageEntriesResult.get(0).getWeight()).isEqualTo(3.38f);
        assertThat(packageEntriesResult.get(1).getWeight()).isEqualTo(8.62f);
        assertThat(packageEntriesResult.get(0).getCost()).isEqualTo(45f);
        assertThat(packageEntriesResult.get(1).getCost()).isEqualTo(98f);
    }

    @Test
    void testMapPackageEntriesWithCorruptedSamples() {
        // setup
        String corruptedPackageEntrySample1_singlePackageExceedsMaxWeight = "1,100.01,€45";
        String corruptedPackageEntrySample2_packagesExceedsMaximumCapacity = "1,1.2,€45|2,1.2,€45|3,1.2,€45|4,1.2,€45|5,1.2,€45|6,1.2,€45|7,1.2,€45|8,1.2,€45|9,1.2,€45|10,1.2,€45|11,1.2,€45|12,1.2,€45|13,1.2,€45|14,1.2,€45|15,1.2,€45|16,1.2,€45";

        // test
        assertThrows(APIException.class, () -> {
            PackageParser.mapPossiblePackageEntries(corruptedPackageEntrySample1_singlePackageExceedsMaxWeight);
        });
        Exception exception1 = assertThrows(APIException.class, () -> {
            PackageParser.mapPossiblePackageEntries(corruptedPackageEntrySample2_packagesExceedsMaximumCapacity);
        });

        // assert
        assertThat(exception1.getClass()).isEqualTo(APIException.class);
        assertThat(exception1.getMessage()).isEqualTo(APIException.CORRUPTED_PACKAGE_DATA_SURPASSED_MAX_CAPACITY);
    }

}