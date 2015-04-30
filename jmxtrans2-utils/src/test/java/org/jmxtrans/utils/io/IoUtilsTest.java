/**
 * The MIT License
 * Copyright (c) 2014 JMXTrans Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jmxtrans.utils.io;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jmxtrans.utils.io.Charsets.UTF_8;

public class IoUtilsTest {

    public static final String TEST_CONTENT = "Hello World";
    private byte[] testContentBytes;
    private final DummyFiles dummyFiles = new DummyFiles();

    @BeforeMethod
    public void setUp() throws Exception {
        testContentBytes = TEST_CONTENT.getBytes(UTF_8);
    }

    @Test
    public void canCopyStreamToEmptyDestination() throws IOException {
        testContentBytes = TEST_CONTENT.getBytes(UTF_8);
        InputStream in = new ByteArrayInputStream(testContentBytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IoUtils.copy(in, out);

        assertThat(out.toByteArray()).isEqualTo(testContentBytes);
    }

    @AfterMethod
    public void cleanUpDummyFiles() {
        dummyFiles.cleanUpTestFiles();
    }

}
