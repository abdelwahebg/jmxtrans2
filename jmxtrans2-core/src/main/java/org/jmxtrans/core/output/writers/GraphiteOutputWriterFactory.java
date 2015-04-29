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
import org.jmxtrans.core.output.support.BatchingOutputWriter;
import org.jmxtrans.core.output.support.TcpOutputWriter;

import static org.jmxtrans.utils.ConfigurationUtils.getInt;
import static org.jmxtrans.utils.io.Charsets.UTF_8;

public class GraphiteOutputWriterFactory implements OutputWriterFactory<BatchingOutputWriter<TcpOutputWriter<GraphiteOutputWriter>>> {
    @Nonnull
    @Override
    public BatchingOutputWriter<TcpOutputWriter<GraphiteOutputWriter>> create(@Nonnull Map<String, String> settings) {

        String hostname = settings.get("hostname");
        int port = getInt(settings, "port");
        int socketTimeoutMillis = getInt(settings, "socketTimeoutMillis", 2000);
        int batchSize = getInt(settings, "batchSize", 100);

        InetSocketAddress server = new InetSocketAddress(hostname, port);

        return new BatchingOutputWriter(
                batchSize,
                new TcpOutputWriter<>(
                        server,
                        socketTimeoutMillis,
                        UTF_8,
                        new GraphiteOutputWriter())
        );
    }
}
