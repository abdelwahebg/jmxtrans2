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
import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import org.jmxtrans.core.output.support.pool.*;
import org.jmxtrans.core.results.QueryResult;
import org.jmxtrans.utils.mockito.MockitoTestNGListener;

import org.mockito.Mock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import stormpot.BlazePool;
import stormpot.Config;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.jmxtrans.utils.io.Charsets.UTF_8;

import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;

@Listeners(MockitoTestNGListener.class)
public class TcpOutputWriterTest {

    @Mock private QueryResult result;
    private TcpSinkServer server;
    private Charset charset = UTF_8;

    @BeforeMethod
    public void startTcpServer() throws IOException {
        server = new TcpSinkServer(charset);
        server.start();
    }

    @Test
    public void test() throws IOException, InterruptedException {
        BlazePool<PoolableSocketAppender> socketPool = createSocketPool();

        TcpOutputWriter<DummyWriter> tcpOutputWriter = new TcpOutputWriter<>(new DummyWriter(), socketPool);
        int processedResultCount = tcpOutputWriter.write(result);

        socketPool.shutdown();

        assertThat(processedResultCount).isEqualTo(1);
        await().until(server.hasReceived("test"));
    }

    private BlazePool<PoolableSocketAppender> createSocketPool() {
        return new BlazePool<>(
                new Config<>()
                        .setAllocator(
                                new SocketAppenderAllocator(server.getLocalSocketAddress(), 100, UTF_8))
                        .setExpiration(
                                new CompoundExpiration<>(
                                        new JmxTransTimeSpreadExpiration<PoolableSocketAppender>(15, 60, MINUTES),
                                        new SocketAppenderValidator()))
                        .setSize(1));
    }

    @AfterMethod
    public void stopTcpServer() throws IOException {
        server.stop();
    }

    private class DummyWriter implements AppenderBasedOutputWriter {
        @Override
        public int write(@Nonnull Appendable writer, @Nonnull QueryResult result) throws IOException {
            writer.append("test");
            return 1;
        }
    }
}
