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
package org.jmxtrans.embedded.output;

import org.jmxtrans.results.QueryResult;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class LibratoWriterTest {
    @Test
    public void testSerialize() throws Exception {

        List<QueryResult> counters = Arrays.asList(
                new QueryResult("counter1", "counter", 10, System.currentTimeMillis()),
                new QueryResult("counter2", "counter", 11.11, System.currentTimeMillis() - 1000),
                new QueryResult("counter2", "counter", 12.12, System.currentTimeMillis())

        );

        List<QueryResult> gauges = Arrays.asList(
                new QueryResult("gauge1", "gauge", 9.9, System.currentTimeMillis()),
                new QueryResult("gauge2", "gauge", 12.12, System.currentTimeMillis() - 1000),
                new QueryResult("gauge2", "gauge", 12.12, System.currentTimeMillis())
        );

        LibratoWriter writer = new LibratoWriter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        writer.serialize(counters, gauges, baos);
        baos.flush();

        System.out.println(new String(baos.toByteArray()));

    }
}