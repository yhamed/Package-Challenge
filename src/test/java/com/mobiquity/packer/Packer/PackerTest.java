package com.mobiquity.packer.Packer;

import com.mobiquity.exception.APIException;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PackerTest {

    @Test
    void packVerifyFinalOutputResultTest() throws IOException {
        // setup
        File resource = new ClassPathResource("testDataWithRandomLineBreaks.txt").getFile();

        // test
        String packingResult = Packer.pack(resource.getPath());
        System.out.println(packingResult);

        // assert
        assertThat(packingResult).isEqualTo("\n4\n-\n-\n2,3\n4\n-\n9,8\n");
    }

    @Test
    void packVerifyExceptionPropagation() throws IOException {
        // setup
        File resource = new ClassPathResource("testDataThatIsCorrupted.txt").getFile();

        // test
        Exception exception = assertThrows(APIException.class, () -> {
            Packer.pack(resource.getPath());
        });

        // assert
        assertThat(exception.getClass()).isEqualTo(APIException.class);
        assertThat(exception.getMessage()).isEqualTo(APIException.CORRUPTED_PACKAGE_DATA);
    }
}
