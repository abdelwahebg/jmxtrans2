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
import org.jmxtrans.utils.time.Clock;
import org.jmxtrans.utils.time.Interval;
import org.jmxtrans.utils.time.SystemClock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.Nonnull;
import javax.management.MBeanServer;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singleton;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FullSchedulingTest {

    @Mock private Query query;
    @Mock private OutputWriter outputWriter;
    @Mock private QueryResult result;
    @Nonnull private Collection<QueryResult> results = singleton(result);
    @Nonnull private final Clock clock = new SystemClock();
    @Nonnull private final Interval queryPeriod = new Interval(1, SECONDS);

    @Test
    public void queriesAreFullyProcessed() throws InterruptedException, IOException {
        when(query.collectMetrics(any(MBeanServer.class)))
                .thenReturn(results);

        long shutdownTimerMillis = 1000;

        ExecutorService queryExecutor = createExecutorService("queries", 2, 1000, 1, MINUTES);
        ExecutorService resultExecutor = createExecutorService("results", 2, 1000, 1, MINUTES);
        ScheduledExecutorService queryTimer = createScheduledExecutorService("queryTimer");

        NaiveScheduler scheduler = new NaiveScheduler(
                queryExecutor,
                resultExecutor,
                queryTimer,
                new QueryGenerator(
                        clock,
                        queryPeriod,
                        singleton(query),
                        new QueryProcessor(
                                clock,
                                ManagementFactory.getPlatformMBeanServer(),
                                singleton(outputWriter),
                                queryExecutor,
                                new ResultProcessor(
                                        clock,
                                        resultExecutor
                                )
                        ),
                        queryTimer
                ),
                shutdownTimerMillis
        );

        scheduler.start();
        verify(outputWriter, timeout(1000)).write(results);
        scheduler.stop();
    }

    private ScheduledExecutorService createScheduledExecutorService(@Nonnull String componentName) {
        return new ScheduledThreadPoolExecutor(1, new JmxTransThreadFactory(componentName), new ThreadPoolExecutor.AbortPolicy());
    }

    @Nonnull
    private ExecutorService createExecutorService(
            @Nonnull String componentName,
            int maxThreads,
            int maxQueueSize,
            int keepAliveTime,
            @Nonnull TimeUnit unit) {
        return new ThreadPoolExecutor(
                1, maxThreads,
                keepAliveTime, unit,
                new ArrayBlockingQueue<Runnable>(maxQueueSize),
                new JmxTransThreadFactory(componentName),
                new ThreadPoolExecutor.AbortPolicy());
    }

}