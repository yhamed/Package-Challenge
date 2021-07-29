package com.mobiquity.exception;

import java.io.FileNotFoundException;

public class ApiException extends FileNotFoundException {

    public static final String FILE_PATH_NOT_FOUND = "The file in the specified filePath is nowhere to be found";
    public static final String CORRUPTED_PACKAGE_DATA = "The file data structure is corrupted, we could not recognize any packages";

    public ApiException(String reason) {
        super(reason);
    }

    public static ApiException getCorruptedFileException() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ApiException.CORRUPTED_PACKAGE_DATA);

        return new ApiException(stringBuilder.toString());
    }
}
