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
package org.jmxtrans.scheduler;

import org.jmxtrans.output.OutputWriter;
import org.jmxtrans.query.embedded.Query;
import org.jmxtrans.results.QueryResult;
import org.jmxtrans.utils.time.ManualClock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.management.MBeanServer;
import java.util.Collection;
import java.util.concurrent.Executor;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static java.util.Collections.singleton;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueryProcessorTest {

    private ManualClock clock = new ManualClock();
    @Mock private MBeanServer mBeanServer;
    @Mock private OutputWriter outputWriter;
    private Executor queryExecutor = directExecutor();
    @Mock private ResultProcessor resultProcessor;
    @Mock private Query query;
    @Mock private QueryResult result;
    private Collection<QueryResult> results = singleton(result);

    private QueryProcessor queryProcessor;

    @Before
    public void createQueryProcessor() {
        queryProcessor = new QueryProcessor(clock, mBeanServer, singleton(outputWriter), queryExecutor, resultProcessor);

        when(query.collectMetrics(any(MBeanServer.class))).thenReturn(results);
    }

    @Test
    public void queryAreProcessed() {
        queryProcessor.process(1, query);
        verify(resultProcessor).writeResults(1, results, outputWriter);
    }
}