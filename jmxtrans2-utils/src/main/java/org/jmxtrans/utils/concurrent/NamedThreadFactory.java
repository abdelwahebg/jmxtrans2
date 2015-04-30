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
package org.jmxtrans.utils.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Basic {@linkplain java.util.concurrent.ThreadFactory} to redefine the name of the created thread.
 *
 * Inspired by Google Guava's com.google.common.util.concurrent.ThreadFactoryBuilder.
 *
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
@ThreadSafe
public class NamedThreadFactory implements ThreadFactory {

    @Nonnull
    private final ThreadFactory backingThreadFactory = Executors.defaultThreadFactory();

    private boolean daemon;

    @Nonnull
    private String threadNamePrefix;

    @Nonnull
    private final AtomicLong increment = new AtomicLong();

    public NamedThreadFactory(@Nonnull String threadNamePrefix, boolean daemon) {
        this.threadNamePrefix = threadNamePrefix;
        this.daemon = daemon;
    }


    @Override
    @Nonnull
    public Thread newThread(@Nonnull Runnable r) {
        Thread thread = backingThreadFactory.newThread(r);
        thread.setName(threadNamePrefix + increment.incrementAndGet());
        thread.setDaemon(daemon);
        return thread;
    }
}
