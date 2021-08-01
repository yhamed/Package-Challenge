package com.mobiquity.service;

import com.mobiquity.domain.Package;
import com.mobiquity.domain.PackageBuilder;
import com.mobiquity.domain.PackageEntry;
import com.mobiquity.domain.PackageEntryBuilder;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PackageServiceTest {

    @Test
    void loadPackages() throws IOException {
        // setup
        ClassPathResourceLoader loader = new ResourceResolver().getLoader(ClassPathResourceLoader.class).get();
        Optional<URL> resource = loader.getResource("classpath:testDataWithRandomLineBreaks.txt");

        // test
        List<Package> packageListResult = PackageService.loadPackages(resource.get().getPath());

        // assert
        Assertions.assertThat(packageListResult).isNotEmpty();
        Assertions.assertThat(packageListResult.get(0).getMaxWeight()).isEqualTo(81f);
        Assertions.assertThat(packageListResult.get(0).getPackageEntries()).singleElement();
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
        List<Package> packageListResult = PackageService.pack(Arrays.asList(packageSample));

        // assert
        Assertions.assertThat(packageListResult.get(0).getMaxWeight()).isEqualTo(100f);
        Assertions.assertThat(packageListResult.get(0).getPackageEntries()).containsExactlyInAnyOrder(packageEntry1, packageEntry3, packageEntry4); // test

        // setup
        packageSample = PackageBuilder.builder(packageSample).withPackageEntries(List.of(packageEntry2, packageEntry3, packageEntry4)).get();

        // test
        packageListResult = PackageService.pack(Arrays.asList(packageSample));

        // assert
        Assertions.assertThat(packageListResult.get(0).getMaxWeight()).isEqualTo(100f);
        Assertions.assertThat(packageListResult.get(0).getPackageEntries()).containsExactlyInAnyOrder(packageEntry2, packageEntry3, packageEntry4);
    }
}