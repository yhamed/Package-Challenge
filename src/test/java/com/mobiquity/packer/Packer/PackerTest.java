package com.mobiquity.packer.Packer;

import com.mobiquity.exception.APIException;

import static org.assertj.core.api.Assertions.assertThat;

import io.micronaut.core.io.ResourceResolver;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PackerTest {

    @Test
    void packVerifyFinalOutputResultTest() throws IOException {
        // setup
        ClassPathResourceLoader loader = new ResourceResolver().getLoader(ClassPathResourceLoader.class).get();
        Optional<URL> resource = loader.getResource("classpath:testDataWithRandomLineBreaks.txt");

        // test
        String packingResult = Packer.pack(resource.get().getPath());
        System.out.println(packingResult);

        // assert
        assertThat(packingResult).isEqualTo("4\n-\n-\n2,3\n4\n-\n9,8\n");
    }

    @Test
    void packVerifyExceptionPropagation() throws IOException {
        // setup
        ClassPathResourceLoader loader = new ResourceResolver().getLoader(ClassPathResourceLoader.class).get();
        Optional<URL> resource = loader.getResource("classpath:testDataThatIsCorrupted.txt");

        // test
        Exception exception = assertThrows(APIException.class, () -> {
            Packer.pack(resource.get().getPath());
        });

        // assert
        assertThat(exception.getClass()).isEqualTo(APIException.class);
        assertThat(exception.getMessage()).isEqualTo(APIException.CORRUPTED_PACKAGE_DATA);
    }
}
