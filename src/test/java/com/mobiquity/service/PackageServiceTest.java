package com.mobiquity.service;

import com.mobiquity.domain.PackageEntry;
import com.mobiquity.exception.ApiException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class PackageServiceTest {

    @InjectMocks
    private PackageService packageService;

    @Test
    void readPackages() throws ApiException {
        // setup

        // test
        List<PackageEntry> packageEntryList = packageService.readPackages("testPath");
        // assert
        Assertions.assertThat(packageEntryList).isNull();
    }
}