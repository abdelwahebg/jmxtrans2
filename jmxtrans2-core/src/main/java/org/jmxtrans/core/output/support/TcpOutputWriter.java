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
package org.jmxtrans.core.output.support;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.jmxtrans.core.output.OutputWriter;
import org.jmxtrans.core.output.support.pool.PoolableSocketAppender;
import org.jmxtrans.core.results.QueryResult;

import stormpot.Pool;
import stormpot.Timeout;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Provide base functionality to write a TCP based OutputWriter.
 *
 * To ensure optimal use of network resources, TcpOutputWriter is implemented as a
 * {@link BatchedOutputWriter} and the socket writer is wrapped in a
 * {@link java.io.BufferedWriter}. Socket is opened and connected when writing the first result and closed after the
 * batch.
 *
 * In case of error on the TCP connection during the write of the batch, a new socket is created when writing the next
 * result. Previous results might be lost. Implementation of this class may choose to flush the writer to improve
 * predictability at the cost of performance.
 *
 * Note: At this point a TCP connection is created and closed for each batch. It would probably make sense to pool the
 * connections instead.
 */
@ThreadSafe
public class TcpOutputWriter<T extends AppenderBasedOutputWriter> implements OutputWriter{

    public static final Timeout CLAIM_TIMEOUT = new Timeout(10, MILLISECONDS);
    @Nonnull private final T target;
    @Nonnull private final Pool<PoolableSocketAppender> socketPool;

    public TcpOutputWriter(
            @Nonnull T target,
            @Nonnull Pool<PoolableSocketAppender> socketPool) {
        this.socketPool = socketPool;
        this.target = target;
    }

    @Override
    public int write(@Nonnull QueryResult result) throws IOException, InterruptedException {
        PoolableSocketAppender poolableSocketAppender = socketPool.claim(CLAIM_TIMEOUT);
        try {
            return target.write(poolableSocketAppender, result);
        } finally {
            if (poolableSocketAppender != null) poolableSocketAppender.release();
        }
    }
}
