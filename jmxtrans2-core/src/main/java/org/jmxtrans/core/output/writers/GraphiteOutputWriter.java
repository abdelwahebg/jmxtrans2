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

import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.jmxtrans.core.output.support.WriterBasedOutputWriter;
import org.jmxtrans.core.results.QueryResult;
import org.jmxtrans.utils.VisibleForTesting;

import static java.util.concurrent.TimeUnit.SECONDS;

@ThreadSafe
public class GraphiteOutputWriter implements WriterBasedOutputWriter {

    @Nullable private volatile String metricPathPrefix;

    @VisibleForTesting
    GraphiteOutputWriter() {}

    @Override
    public int write(@Nonnull Writer writer, @Nonnull QueryResult result) throws IOException {
        writer.write(buildMetricPathPrefix());
        writer.write(result.getName());
        writer.write(" ");
        writer.write(Objects.toString(result.getValue()));
        writer.write(" ");
        writer.write(Long.toString(result.getEpoch(SECONDS)));
        return 1;
    }

    // TODO: rewriting the metric name is a job for the naming strategy, not for the output writers
    @Nonnull
    private String buildMetricPathPrefix() {
        // {@link java.net.InetAddress#getLocalHost()} may not be known at JVM startup when the process is launched as a Linux service.
        // FIXME: there is a 5 second cache on localhost name, it probably make sense to reload it periodically. Hostname can change.
        if (metricPathPrefix != null) return metricPathPrefix;

        metricPathPrefix = "servers." + getHostname() + ".";
        return metricPathPrefix;
    }

    @Nonnull
    private String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName().replaceAll("\\.", "_");
        } catch (UnknownHostException e) {
            return  "#unknown#";
        }
    }

}
