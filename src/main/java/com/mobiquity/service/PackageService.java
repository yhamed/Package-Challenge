package com.mobiquity.service;

import com.mobiquity.domain.Package;
import com.mobiquity.domain.PackageEntry;
import com.mobiquity.exception.APIException;
import com.mobiquity.parser.PackageParser;
import com.mobiquity.utilities.FileReaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    public String pack(String filePath) throws APIException {
        return null;
    }

    private PackageEntry mapPackageEntries(List<String> data) {

        return null;
    }
}
