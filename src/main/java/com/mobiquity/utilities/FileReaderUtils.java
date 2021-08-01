package com.mobiquity.utilities;

import com.mobiquity.exception.APIException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

// this utility class enables us to read files, adapt them to a more readable format and do a basic check for unstructured or corrupted files in that case APIException will be thrown
public class FileReaderUtils {
    private final static String COLON = ":";
    private final static String OPEN_PARENTHESIS = "(";
    private final static String CLOSE_PARENTHESIS = ")";

    // extracts the file data in a raw manner while eliminating spaces and empty lines
    public static List<String> extractFileData(String filePath) throws APIException {
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
            throw new APIException(APIException.FILE_PATH_NOT_FOUND);
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
    // 81 :(1,53.38,€45) (2,88.6 2, €98)  => 81:(1,53.38,€45||2,88.62,€98) => 81:1,53.38,€45|2,88.62,€98
    public static List<String> transformFileData(List<String> rawFileData) throws APIException {
        try {
            return transformFileData(rawFileData, new ArrayList<>(), new StringBuilder(), false);
        } catch (PatternSyntaxException exception) {
            throw new APIException(APIException.CORRUPTED_PACKAGE_DATA);
        }
    }

    // the recursive function that enables us to recover and order the data, one entry a line
    private static List<String> transformFileData(List<String> rawFileData, List<String> refinedData, StringBuilder temporaryData, boolean hasAtLeastOneValidEntry) throws APIException {
        if (rawFileData.isEmpty()) {
            String refinedDataEntry = temporaryData.toString();
            if (refinedDataEntry.isEmpty() && refinedData.isEmpty()) {
                throw new APIException(APIException.CORRUPTED_PACKAGE_DATA);
            }
            refinedDataEntry = removeLineBreaksAndFormat(refinedDataEntry);
            refinedData.add(placePipeSeparatorsAndAdaptStructure(refinedDataEntry));
            return refinedData;
        }
        String rawLine = rawFileData.remove(0);
        if (doesNotContainColon(rawLine)) {
            temporaryData.append(rawLine);

            return transformFileData(rawFileData, refinedData, temporaryData, hasAtLeastOneValidEntry);
        }
        if (hasAtLeastOneValidEntry) {
            String validRefinedDataForRelativeEntry = removeLineBreaksAndFormat(temporaryData.toString());
            if (validRefinedDataForRelativeEntry.lastIndexOf(CLOSE_PARENTHESIS) != validRefinedDataForRelativeEntry.length() - 1) {
                String[] validRefinedDataForRelativeEntries = validRefinedDataForRelativeEntry.split(CLOSE_PARENTHESIS);
                if (validRefinedDataForRelativeEntries.length != 0) {
                    refinedData.add(placePipeSeparatorsAndAdaptStructure(validRefinedDataForRelativeEntries[validRefinedDataForRelativeEntries.length - 2]));
                } else {
                    refinedData.add(validRefinedDataForRelativeEntry.replace(OPEN_PARENTHESIS, "").replace(CLOSE_PARENTHESIS, ""));
                }
                temporaryData = new StringBuilder(validRefinedDataForRelativeEntry.split(CLOSE_PARENTHESIS)[1]);
                temporaryData.append(rawLine);

                return transformFileData(rawFileData, refinedData, temporaryData, true);
            }
            refinedData.add(placePipeSeparatorsAndAdaptStructure(temporaryData.toString()));
            temporaryData = new StringBuilder(rawLine);

            return transformFileData(rawFileData, refinedData, temporaryData, true);
        }
        temporaryData.append(rawLine);
        return transformFileData(rawFileData, refinedData, temporaryData, true);
    }

    // removes all open and closed parenthesis and places a pipe in between package entries (transforms `)(` into `|`)
    private static String placePipeSeparatorsAndAdaptStructure(String refinedDataEntry) {
        return refinedDataEntry.replace(OPEN_PARENTHESIS, "|")
                .replaceFirst("\\|", "")
                .replace(CLOSE_PARENTHESIS, "");
    }

    // removes line breaks and spaces
    private static String removeLineBreaksAndFormat(String refinedLineEntry) {
        return refinedLineEntry.replace("\n", "")
                .replace("\r", "")
                .replaceAll("\\s+", "");
    }

    private static boolean doesNotContainColon(String lineEntry) {
        return !lineEntry.contains(COLON);
    }

    public static String checkDataStructure(String refinedData) throws APIException {
        if (refinedData.contains(OPEN_PARENTHESIS) || refinedData.contains(CLOSE_PARENTHESIS)) {
            throw new APIException(APIException.CORRUPTED_PACKAGE_DATA);
        }
        return refinedData;
    }
}
