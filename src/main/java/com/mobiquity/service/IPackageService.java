package com.mobiquity.service;

import com.mobiquity.domain.PackageEntry;
import com.mobiquity.exception.ApiException;

import java.util.List;

public interface IPackageService {
    public List<PackageEntry> readPackages(String filePath) throws ApiException;
}
