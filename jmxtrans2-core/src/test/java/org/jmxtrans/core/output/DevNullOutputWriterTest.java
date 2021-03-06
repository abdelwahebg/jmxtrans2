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
package org.jmxtrans.core.output;

import java.io.IOException;
import java.util.Map;

import org.jmxtrans.core.results.MetricType;
import org.jmxtrans.core.results.QueryResult;

import org.testng.annotations.Test;

import static java.util.Collections.emptyMap;

import static org.assertj.core.api.Assertions.assertThat;

public class DevNullOutputWriterTest {

    @Test
    public void writingResultsDoesNothing() throws IOException, InterruptedException {
        OutputWriter outputWriter = new DevNullOutputWriter();
        QueryResult result = new QueryResult("name", MetricType.UNKNOWN, "value", 0);

        int processedMetricCount = outputWriter.write(result);
        assertThat(processedMetricCount).isZero();
    }

    @Test
    public void factoryCanCreateOutputWriter() {
        Map<String, String> settings = emptyMap();
        OutputWriter outputWriter = new DevNullOutputWriter.Factory().create(settings);

        assertThat(outputWriter).isNotNull();
    }

}
