package com.mobiquity.service;

import com.mobiquity.domain.Package;
import com.mobiquity.exception.APIException;

import java.util.List;

public interface IPackageService {
    public List<Package> readPackages(String filePath) throws APIException;
    public String pack(String filePath) throws APIException;
}
