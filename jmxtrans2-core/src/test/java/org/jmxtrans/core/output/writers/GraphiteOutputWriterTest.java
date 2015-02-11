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
package org.jmxtrans.core.output.writers;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.jmxtrans.core.output.OutputWriter;

import org.testng.annotations.Test;

import static org.jmxtrans.core.results.QueryResultFixtures.standardQueryResult;

import static org.assertj.core.api.Assertions.assertThat;

public class GraphiteOutputWriterTest {

    @Test
    public void metricsSendToGraphiteFollowCorrectFormat() throws IOException {
        StringWriter writer = new StringWriter();

        new GraphiteOutputWriter().write(writer, standardQueryResult());

        assertThat(writer.toString())
                .startsWith("servers.")
                .endsWith("some.value 2 3");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void hostnameIsRequired() {
        Map<String, String> settings = new HashMap<>();
        settings.put("port", "80");
        try {
            new GraphiteOutputWriter.Factory().create(settings);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("hostname can't be null");
            throw e;
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void portIsRequired() {
        Map<String, String> settings = new HashMap<>();
        settings.put("hostname", "localhost");
        try {
            new GraphiteOutputWriter.Factory().create(settings);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("No setting 'port' found");
            throw e;
        }
    }
    
    @Test
    public void canCreateGraphiteOutputWriter() {
        Map<String, String> settings = new HashMap<>();
        settings.put("hostname", "localhost");
        settings.put("port", "1234");
        OutputWriter outputWriter = new GraphiteOutputWriter.Factory().create(settings);
        
        assertThat(outputWriter).isNotNull();
    }
}
