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
package org.jmxtrans.core.config;

import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Nonnull;

public class ThreadPoolExecutorMetrics implements ThreadPoolExecutorMetricsMBean {
    
    @Nonnull private final ThreadPoolExecutor executor;

    public ThreadPoolExecutorMetrics(@Nonnull ThreadPoolExecutor executor) {
        this.executor = executor;
    }
    
    @Override
    public int getWorkQueueSize() {
        return executor.getQueue().size();
    }
    
    @Override
    public int getWorkQueueRemainingCapacity() {
        return executor.getQueue().remainingCapacity();
    }

    @Override
    public int getActiveCount() {
        return executor.getActiveCount();
    }

    @Override
    public long getCompletedTaskCount() {
        return executor.getCompletedTaskCount();
    }

    @Override
    public int getCorePoolSize() {
        return executor.getCorePoolSize();
    }

    @Override
    public long getLargestPoolSize() {
        return executor.getLargestPoolSize();
    }

    @Override
    public long getMaximumPoolSize() {
        return executor.getMaximumPoolSize();
    }

    @Override
    public long getPoolSize() {
        return executor.getPoolSize();
    }

    @Override
    public long getTaskCount() {
        return executor.getTaskCount();
    }

}
