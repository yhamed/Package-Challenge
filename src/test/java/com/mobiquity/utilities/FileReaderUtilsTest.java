package com.mobiquity.utilities;

import com.mobiquity.exception.APIException;

import static org.assertj.core.api.Assertions.assertThat;

import io.micronaut.core.io.ResourceResolver;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class FileReaderUtilsTest {

    @Test
    void fileReaderTestWithEmptyLinesAndWhiteSpacesIgnored() throws IOException {
        // setup
        ClassPathResourceLoader loader = new ResourceResolver().getLoader(ClassPathResourceLoader.class).get();
        Optional<URL> resource = loader.getResource("classpath:testData.txt");

        // test
        List<String> result = FileReaderUtils.extractFileData(resource.get().getPath());

        // assert
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(7);
    }

    @Test
    void fileReaderTestExtractFileDataReturnsApiExceptionWhenRandomFalseFilePath() throws IOException {
        // test
        Exception exception = assertThrows(APIException.class, () -> {
            FileReaderUtils.extractFileData("randomInexistantFilePath");
        });

        // assert
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(APIException.class);
        assertThat(exception.getMessage()).isEqualTo(APIException.FILE_PATH_NOT_FOUND);
    }


    @Test
    void fileReaderTestTransformFileDataToParseReadyStructure() throws IOException {
        // setup
        ClassPathResourceLoader loader = new ResourceResolver().getLoader(ClassPathResourceLoader.class).get();
        Optional<URL> resource = loader.getResource("classpath:testDataWithRandomLineBreaks.txt");

        // test
        List<String> transformedResult = FileReaderUtils.transformFileData(FileReaderUtils.extractFileData(resource.get().getPath()));

        // assert
        assertThat(transformedResult).isNotEmpty();
        assertThat(transformedResult.size()).isEqualTo(4);
        transformedResult.stream().forEach(line -> {
            assertThat(line.split(":").length).isEqualTo(2);
        });
        assertThat(transformedResult.get(0)).isEqualTo("81:1,53.38,€45|2,88.62,€98|3,78.48,€3|4,72.30,€76|5,30.18,€9|6,46.34,€48");
    }

    @Test
    void fileReaderTestTransformFileDataReturnsApiExceptionWhenWeHaveACorruptedFileStructure() throws IOException {
        // setup
        // file has a missing close parenthesis on line 16
        ClassPathResourceLoader loader = new ResourceResolver().getLoader(ClassPathResourceLoader.class).get();
        Optional<URL> resource = loader.getResource("classpath:testDataThatIsCorrupted.txt");

        // test
        Exception exception = assertThrows(APIException.class, () -> {
            FileReaderUtils.transformFileData(FileReaderUtils.extractFileData(resource.get().getPath()));
        });

        // assert
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(APIException.class);
        assertThat(exception.getMessage()).isEqualTo(APIException.CORRUPTED_PACKAGE_DATA);
    }


    @Test
    void checkDataStructure() {
        // setup
        String packageData1 = "52:1,1.5,12|(";
        String packageData2 = "52:1,1.5,)12|";

        // test
        Exception exception = assertThrows(APIException.class, () -> {
            FileReaderUtils.checkDataStructure(packageData1);
        });

        // assert
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(APIException.class);
        assertThat(exception.getMessage()).isEqualTo(APIException.CORRUPTED_PACKAGE_DATA);

        // test
        Exception exception2 = assertThrows(APIException.class, () -> {
            FileReaderUtils.checkDataStructure(packageData2);
        });

        // assert
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(APIException.class);
        assertThat(exception.getMessage()).isEqualTo(APIException.CORRUPTED_PACKAGE_DATA);
    }
}
