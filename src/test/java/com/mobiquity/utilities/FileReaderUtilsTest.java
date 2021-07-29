package com.mobiquity.utilities;

import com.mobiquity.exception.ApiException;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.File;
import java.io.IOException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class FileReaderUtilsTest {

    @Test
    void fileReaderTestWithEmptyLinesAndWhiteSpacesIgnored() throws IOException {
        // setup
        File resource = new ClassPathResource("testData.txt").getFile();

        // test
        List<String> result = FileReaderUtils.extractFileData(resource.getPath());

        // assert
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(7);
    }

    @Test
    void fileReaderTestExtractFileDataReturnsApiExceptionWhenRandomFalseFilePath() throws IOException {
        // test
        Exception exception = assertThrows(ApiException.class, () -> {
            FileReaderUtils.extractFileData("randomFalseFilePath");
        });

        // assert
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(ApiException.class);
        assertThat(exception.getMessage()).isEqualTo(ApiException.FILE_PATH_NOT_FOUND);
    }


    @Test
    void fileReaderTestTransformFileDataToParseReadyStructure() throws IOException {
        // setup
        File resource = new ClassPathResource("testData.txt").getFile();

        // test
        List<String> transfromedResult = FileReaderUtils.transformFileData(FileReaderUtils.extractFileData(resource.getPath()));

        // assert
        assertThat(transfromedResult).isNotEmpty();
        assertThat(transfromedResult.size()).isEqualTo(4);
        transfromedResult.stream().forEach(line -> {
            assertThat(line.split(":").length).isEqualTo(2);
        });
    }

    @Test
    void fileReaderTestTransformFileDataReturnsApiExceptionWhenWeHaveACorruptedFileStructure() throws IOException {
        // setup
        // file has a missing close parenthesis on line 16
        File resource = new ClassPathResource("testDataThatIsCorrupted.txt").getFile();

        // test
        Exception exception = assertThrows(ApiException.class, () -> {
            FileReaderUtils.transformFileData(FileReaderUtils.extractFileData(resource.getPath()));
        });

        // assert
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(ApiException.class);
        assertThat(exception.getMessage()).isEqualTo(ApiException.CORRUPTED_PACKAGE_DATA);
    }


    @Test
    void checkDataIntegrity() {
        // setup
        String packageData1 = "52:1,1.5,12|(";
        String packageData2 = "52:1,1.5,)12|";

        // test
        Exception exception = assertThrows(ApiException.class, () -> {
            FileReaderUtils.checkDataIntegrity(packageData1);
        });

        // assert
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(ApiException.class);
        assertThat(exception.getMessage()).isEqualTo(ApiException.CORRUPTED_PACKAGE_DATA);

        // test
        Exception exception2 = assertThrows(ApiException.class, () -> {
            FileReaderUtils.checkDataIntegrity(packageData2);
        });

        // assert
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(ApiException.class);
        assertThat(exception.getMessage()).isEqualTo(ApiException.CORRUPTED_PACKAGE_DATA);
        }
    }
