package com.mobiquity.service;

import com.mobiquity.domain.Package;
import com.mobiquity.exception.APIException;

import java.util.List;

public interface IPackageService {
    public List<Package> readPackages(String filePath) throws APIException;
    public List<Package> pack(List<Package> packageList);
}
