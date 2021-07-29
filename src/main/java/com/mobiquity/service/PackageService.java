package com.mobiquity.service;

import com.mobiquity.domain.PackageEntry;
import com.mobiquity.exception.ApiException;
import com.mobiquity.utilities.FileReaderUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackageService implements IPackageService {

    @Override
    public List<PackageEntry> readPackages(String filePath) throws ApiException {
    List<String> rawData = FileReaderUtils.extractFileData(filePath);

    return null;
    }

    private PackageEntry mapPackageEntries(List<String> data) {

    return  null;
    }
}
