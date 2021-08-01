package com.mobiquity.exception;

import java.io.FileNotFoundException;

public class APIException extends FileNotFoundException {

    public static final String FILE_PATH_NOT_FOUND = "The file in the specified filePath is nowhere to be found.";
    public static final String CORRUPTED_PACKAGE_DATA = "The file data structure is corrupted or empty, we could not recognize the packages properly.";
    public static final String CORRUPTED_PACKAGE_DATA_PARSE = "The file data structure is corrupted, we could not parse the following entry: ";
    public static final String CORRUPTED_PACKAGE_DATA_SURPASSED_MAX_WEIGHT = "The file data structure is corrupted, the package has exceeded the maximum weight limit of a 100.";
    public static final String CORRUPTED_PACKAGE_DATA_SURPASSED_MAX_CAPACITY = "The file data structure is corrupted, the package has exceeded the maximum capacity (15 entries at most).";

    public APIException(String message) {
        super(message);
    }

}
