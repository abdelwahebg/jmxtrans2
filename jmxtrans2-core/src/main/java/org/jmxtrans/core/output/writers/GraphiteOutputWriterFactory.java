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
package org.jmxtrans.core.output.writers;

import java.net.InetSocketAddress;
import java.util.Map;

import javax.annotation.Nonnull;

import org.jmxtrans.core.output.OutputWriterFactory;
import org.jmxtrans.core.output.support.TcpOutputWriter;
import org.jmxtrans.core.output.support.pool.CompoundExpiration;
import org.jmxtrans.core.output.support.pool.JmxTransTimeSpreadExpiration;
import org.jmxtrans.core.output.support.pool.PoolableSocketAppender;
import org.jmxtrans.core.output.support.pool.SocketAppenderAllocator;
import org.jmxtrans.core.output.support.pool.SocketAppenderValidator;

import stormpot.BlazePool;
import stormpot.Config;
import stormpot.Pool;

import static java.util.concurrent.TimeUnit.MINUTES;

import static org.jmxtrans.utils.ConfigurationUtils.getInt;
import static org.jmxtrans.utils.io.Charsets.UTF_8;

public class GraphiteOutputWriterFactory implements OutputWriterFactory<TcpOutputWriter<GraphiteOutputWriter>> {
    @Nonnull
    @Override
    public TcpOutputWriter<GraphiteOutputWriter> create(@Nonnull Map<String, String> settings) {

        String hostname = settings.get("hostname");
        int port = getInt(settings, "port");
        int socketTimeoutMillis = getInt(settings, "socketTimeoutMillis", 2000);

        InetSocketAddress server = new InetSocketAddress(hostname, port);

        SocketAppenderAllocator socketAppenderAllocator = new SocketAppenderAllocator(server, socketTimeoutMillis, UTF_8);
        Config<PoolableSocketAppender> poolConfig = new Config<>()
                .setAllocator(socketAppenderAllocator)
                .setExpiration(new CompoundExpiration<>(
                        new JmxTransTimeSpreadExpiration<PoolableSocketAppender>(15, 60, MINUTES),
                        new SocketAppenderValidator()
                ));
        Pool<PoolableSocketAppender> socketPool = new BlazePool<>(poolConfig);

        return new TcpOutputWriter<>(new GraphiteOutputWriter(), socketPool);
    }
}
