package com.mobiquity.service;

import com.mobiquity.domain.Package;
import com.mobiquity.domain.PackageBuilder;
import com.mobiquity.domain.PackageEntry;
import com.mobiquity.domain.PackageEntryBuilder;
import com.mobiquity.exception.APIException;
import com.mobiquity.parser.PackageParser;
import com.mobiquity.utilities.FileReaderUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PackageServiceTest {

    @Mock
    private PackageParser packageParser;

    @InjectMocks
    private PackageService packageService;

    @Test
    void readPackages() throws IOException {
        // setup
        PackageEntry packageEntry = PackageEntryBuilder.builder().withIndexNumber(1).withWeight(10f).withCost(50f).get();
        Package packageSample = PackageBuilder.builder().withMaxWeight(100f)
                .withPackageEntries(Arrays.asList(packageEntry)).get();

        File resource = new ClassPathResource("testData.txt").getFile();
        Mockito.when(packageParser.parsePackage(Mockito.anyString())).thenReturn(packageSample);

        // test
        List<Package> packageListResult = packageService.readPackages(resource.getPath());
        // assert
        Mockito.verify(packageParser, Mockito.atLeastOnce()).parsePackage(Mockito.anyString());
        Assertions.assertThat(packageListResult).isNotEmpty();
        Assertions.assertThat(packageListResult.get(0).getMaxWeight()).isEqualTo(100f);
    }

    @Test
    void pack() {
        // setup
        PackageEntry packageEntry1 = PackageEntryBuilder.builder().withIndexNumber(1).withWeight(50f).withCost(100f).get();
        PackageEntry packageEntry2 = PackageEntryBuilder.builder().withIndexNumber(2).withWeight(40f).withCost(55f).get();
        PackageEntry packageEntry3 = PackageEntryBuilder.builder().withIndexNumber(3).withWeight(30f).withCost(50f).get();
        PackageEntry packageEntry4 = PackageEntryBuilder.builder().withIndexNumber(4).withWeight(20f).withCost(40f).get();

        Package packageSample = PackageBuilder.builder().withMaxWeight(100f)
                .withPackageEntries(List.of(packageEntry1, packageEntry2, packageEntry3, packageEntry4)).get();

        // test
        List<Package> packageListResult = packageService.pack(Arrays.asList(packageSample));

        // assert
        Assertions.assertThat(packageListResult.get(0).getMaxWeight()).isEqualTo(100f);
        Assertions.assertThat(packageListResult.get(0).getPackageEntries()).containsExactlyInAnyOrder(packageEntry1, packageEntry3, packageEntry4); // test

        // setup
        packageSample = PackageBuilder.builder(packageSample).withPackageEntries(List.of(packageEntry2 , packageEntry3 , packageEntry4)).get();

        // test
        packageListResult = packageService.pack(Arrays.asList(packageSample));

        // assert
        Assertions.assertThat(packageListResult.get(0).getMaxWeight()).isEqualTo(100f);
        Assertions.assertThat(packageListResult.get(0).getPackageEntries()).containsExactlyInAnyOrder(packageEntry2, packageEntry3, packageEntry4);
    }
}