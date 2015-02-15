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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.jmxtrans.core.lifecycle.LifecycleAware;
import org.jmxtrans.core.log.Logger;
import org.jmxtrans.core.log.LoggerFactory;

import lombok.Getter;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@ThreadSafe // actually, not threadsafe, but should be "safe enough", see comment below...
public class NaiveScheduler {

    @Nonnull private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Nonnull private final ExecutorService queryExecutor;
    @Nonnull private final ExecutorService resultExecutor;

    // Synchronization around state transition is primitive and actually wrong. Using Guava would be cleaner, but
    // current naive implementation has a fairly low potential for trouble.
    @Nonnull @Getter private volatile State state = State.NEW;
    @Nonnull private final ScheduledExecutorService queryTimer;
    @Nonnull private final QueryGenerator queryGenerator;
    @Nonnull private final Iterable<LifecycleAware> lifecycleListeners;
    private final long shutdownTimeoutMillis;

    public NaiveScheduler(
            @Nonnull ExecutorService queryExecutor,
            @Nonnull ExecutorService resultExecutor,
            @Nonnull ScheduledExecutorService queryTimer,
            @Nonnull QueryGenerator queryGenerator,
            @Nonnull Iterable<LifecycleAware> lifecycleListeners,
            long shutdownTimeoutMillis) {
        this.queryTimer = queryTimer;
        this.lifecycleListeners = lifecycleListeners;
        this.shutdownTimeoutMillis = shutdownTimeoutMillis;
        this.queryExecutor = queryExecutor;
        this.resultExecutor = resultExecutor;
        this.queryGenerator = queryGenerator;
    }

    public void start() throws Exception {
        if (!state.equals(State.NEW)) {
            throw new IllegalStateException("Already started");
        }
        try {
            logger.debug("Starting scheduler");
            this.state = State.STARTING;
            for (LifecycleAware lifecycleListener : lifecycleListeners) {
                lifecycleListener.start();
            }
            queryGenerator.start();
            this.state = State.RUNNING;
            logger.debug("Scheduler started");
        } catch (Exception e) {
            this.state = State.FAILED;
            throw e;
        }
    }

    public void stop() throws Exception {
        if (!state.equals(State.STARTING) && !state.equals(State.RUNNING)) {
            throw new IllegalStateException("Not running");
        }
        try {
            logger.debug("Stopping scheduler");
            this.state = State.STOPPING;
            queryGenerator.stop();
            queryTimer.shutdown();
            queryExecutor.shutdown();
            resultExecutor.shutdown();
            queryTimer.awaitTermination(shutdownTimeoutMillis, MILLISECONDS);
            queryExecutor.awaitTermination(shutdownTimeoutMillis, MILLISECONDS);
            resultExecutor.awaitTermination(shutdownTimeoutMillis, MILLISECONDS);
            for (LifecycleAware lifecycleListener : lifecycleListeners) {
                lifecycleListener.stop();
            }
            this.state = State.TERMINATED;
            logger.debug("Scheduler stopped");
        } catch (Exception e) {
            this.state = State.FAILED;
            throw e;
        }
    }

    public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        resultExecutor.awaitTermination(timeout, unit);
    }

    @ThreadSafe
    public static enum State {
        NEW,
        STARTING,
        RUNNING,
        STOPPING,
        TERMINATED,
        FAILED
    }
}
