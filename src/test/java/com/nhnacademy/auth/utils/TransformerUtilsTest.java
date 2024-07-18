package com.nhnacademy.auth.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransformerUtilsTest {
    private TransformerUtils transformerUtils = new TransformerUtils();

    @BeforeEach
    void setUp() {
        transformerUtils.setClientEncodingKey("asdf");
    }

    @Test
    void testEncodeDecode() {
        String originalText = "1";
        String encodedText = transformerUtils.encode(originalText);
        String decodedText = transformerUtils.decode(encodedText);

        assertEquals(originalText, decodedText);
    }

    @Test
    void testEncode() {
        String originalText = "1";
        String expectedEncoded = "UA==";
        String encodedText = transformerUtils.encode(originalText);

        assertEquals(expectedEncoded, encodedText);
    }

    @Test
    void testDecode() {
        String encodedText = "UA==";
        String expectedDecoded = "1";
        String decodedText = transformerUtils.decode(encodedText);

        assertEquals(expectedDecoded, decodedText);
    }
}