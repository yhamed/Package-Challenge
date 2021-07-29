package com.mobiquity.utilities;

import com.mobiquity.exception.ApiException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FileReaderUtils {
    private final static String COLON = ":";
    private final static String OPEN_PARENTHESIS = "(";
    private final static String CLOSE_PARENTHESIS = ")";

    // extracts the file data in a raw manner while eliminating spaces and empty lines
    public static List<String> extractFileData(String filePath) throws ApiException {
        List<String> rawFileData = new ArrayList();
        try (FileReader fr = new FileReader(filePath, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(fr)) {

            String lineEntry;
            while ((lineEntry = reader.readLine()) != null) {
                if (!lineEntry.trim().isBlank()) {
                    rawFileData.add(lineEntry.trim().replaceAll("\\s", ""));
                }
            }
        } catch (IOException e) {
            throw new ApiException(ApiException.FILE_PATH_NOT_FOUND);
        }

        return rawFileData;
    }

    // remove line breaks and transforms the data loaded in order to have a string list, in order to be ready to be parsed it also replaces all the `)(` with `|`
    // and what is left is an open parenthesis at the start and a closing one at the end of the right side of the data and then at the end we remove the open and close
    // parenthesis after using them to delimit the start and end of each data entry
    // *******************
    // result example 1:
    // $MAX_WEIGHT_1:packageEntry_1_indexNumber,packageEntry_1_weight,packageEntry_1_cost|packageEntry_2_indexNumber,packageEntry_2_weight,packageEntry_2_cost
    // $MAX_WEIGHT_2:packageEntry_2_indexNumber,packageEntry_2_weight,packageEntry_2_cost
    // *******************
    // result example 2: (real data)
    // 81:1,53.38,€45|2,88.62,€98|3,78.48,€3|4,72.30,€76|5,30.18,€96,46.34,€48
    // 8:1,15.3,€34

    // data lifecycle example:
    // 81 :(1,53.38,€45) (2,88.6 2, €98)  => 81:(1,53.38,€45||2,88.62,€98) => 81:   1,53.38,€45|2,88.62,€98
    public static List<String> transformFileData(List<String> rawFileData) throws ApiException {
        try {
            return transformFileData(rawFileData, new ArrayList<>(), new StringBuilder(), false);
        } catch (PatternSyntaxException exception) {
            throw new ApiException(ApiException.CORRUPTED_PACKAGE_DATA);
        }
    }

    // the recursive function that enables us to recover and order the data, one entry a line
    public static List<String> transformFileData(List<String> rawFileData, List<String> refinedData, StringBuilder temporaryData, boolean hasAtLeastOneValidEntry) throws ApiException {
        if (rawFileData.isEmpty()) {
            if (refinedData.isEmpty()) {
                throw new ApiException(ApiException.CORRUPTED_PACKAGE_DATA);
            }
            refinedData.add(removeLineBreaksAndFormat(temporaryData.toString().replaceAll("\\)", "")));
            return refinedData;
        }
        String rawLine = rawFileData.get(0);
        if (doesNotContainColon(rawLine)) {
            temporaryData.append(removeLineBreaksAndFormat(rawLine));

            rawFileData.remove(0);
            return transformFileData(rawFileData, refinedData, temporaryData, hasAtLeastOneValidEntry);
        }
        if (hasAtLeastOneValidEntry) {
            String validRefinedDataForRelativeEntry = removeLineBreaksAndFormat(temporaryData.toString());
            if (validRefinedDataForRelativeEntry.lastIndexOf(CLOSE_PARENTHESIS) != validRefinedDataForRelativeEntry.length() - 1) {
                refinedData.add(validRefinedDataForRelativeEntry.split(CLOSE_PARENTHESIS)[0]);
                temporaryData = new StringBuilder(validRefinedDataForRelativeEntry.split(CLOSE_PARENTHESIS)[1]);
                temporaryData.append(rawLine);

                rawFileData.remove(0);
                return transformFileData(rawFileData, refinedData, temporaryData, true);
            }
            refinedData.add(removeLineBreaksAndFormat(temporaryData.toString().replaceAll("\\)", "")));
            temporaryData = new StringBuilder(rawLine);
            //temporaryData.append(CLOSE_PARENTHESIS);
            rawFileData.remove(0);
            return transformFileData(rawFileData, refinedData, temporaryData, true);
        }
        temporaryData.append(rawLine);
        rawFileData.remove(0);
        return transformFileData(rawFileData, refinedData, temporaryData, true);
    }

    // removes line breaks, removes open parenthesis and transforms `)(` -> `|`
    private static String removeLineBreaksAndFormat(String refinedLineEntry) {
        return refinedLineEntry.replaceAll("\n", "")
                .replaceAll("\r", "")
                .replaceAll("\\)\\(", "|")
                .replaceAll("\\(", "");
    }

    private static boolean doesNotContainColon(String lineEntry) {
        return !lineEntry.contains(COLON);
    }

    private static void populateRefinedData(List<String> refinedData, String entry) throws ApiException{
        refinedData.add(checkDataIntegrity(entry));
    }

    public static String checkDataIntegrity(String refinedData) throws ApiException {
        if(refinedData.contains(OPEN_PARENTHESIS) || refinedData.contains(CLOSE_PARENTHESIS)) {
            throw new ApiException(ApiException.CORRUPTED_PACKAGE_DATA);
        }
        return refinedData;
    }
}
