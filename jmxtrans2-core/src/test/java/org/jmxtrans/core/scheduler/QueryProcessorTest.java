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
package org.jmxtrans.core.scheduler;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executor;

import javax.management.MBeanServer;

import org.jmxtrans.core.output.OutputWriter;
import org.jmxtrans.core.query.InProcessServer;
import org.jmxtrans.core.query.Query;
import org.jmxtrans.core.query.ResultNameStrategy;
import org.jmxtrans.core.results.QueryResult;
import org.jmxtrans.utils.mockito.MockitoTestNGListener;
import org.jmxtrans.utils.time.ManualClock;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static java.util.Collections.singleton;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Listeners(MockitoTestNGListener.class)
public class QueryProcessorTest {

    private ManualClock clock = new ManualClock();
    @Mock private MBeanServer mBeanServer;
    @Mock private OutputWriter outputWriter;
    private Executor queryExecutor = directExecutor();
    @Mock private ResultProcessor resultProcessor;
    @Mock private Query query;
    @Mock private QueryResult result;
    private Collection<QueryResult> results;

    private QueryProcessor queryProcessor;

    @BeforeMethod
    public void createQueryProcessor() throws IOException {
        results = singleton(result);
        queryProcessor = new QueryProcessor(clock, singleton(outputWriter), queryExecutor, resultProcessor, new ResultNameStrategy());

        when(query.collectMetrics(any(MBeanServer.class), any(ResultNameStrategy.class))).thenReturn(results);
    }

    @Test
    public void queryAreProcessed() {
        queryProcessor.process(1, new InProcessServer(Collections.<Query>emptyList()), query);
        verify(resultProcessor).writeResult(1, result, outputWriter);
    }
}
