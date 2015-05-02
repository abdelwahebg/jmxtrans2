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
package org.jmxtrans.writers.additional;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.jmxtrans.core.output.OutputWriterFactory;
import org.jmxtrans.core.output.support.BatchingOutputWriter;
import org.jmxtrans.core.output.support.HttpOutputWriter;
import org.jmxtrans.utils.appinfo.AppInfo;

import com.fasterxml.jackson.core.JsonFactory;

import static java.lang.String.format;
import static java.net.Proxy.Type.HTTP;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import static org.jmxtrans.core.output.support.HttpOutputWriter.builder;
import static org.jmxtrans.utils.ConfigurationUtils.getInt;
import static org.jmxtrans.utils.ConfigurationUtils.getString;

@ThreadSafe
public final class LibratoWriterFactory implements OutputWriterFactory<BatchingOutputWriter<HttpOutputWriter<LibratoWriter>>> {

    @Nonnull
    @Override
    public BatchingOutputWriter<HttpOutputWriter<LibratoWriter>> create(@Nonnull Map<String, String> settings) {
        int batchSize = getInt(settings, "batchSize", 100);
        URL url = parseUrl(getString(settings, "libratoUrl", "https://metrics-api.librato.com/v1/metrics"));
        int timeoutInMillis = getInt(settings, "timeoutInMillis", 1000);
        Proxy proxy = getProxy(settings);
        String source = "hostname"; // FIXME: correct handling of source should be implemented after #60 is done.
        String username = getString(settings, "username", null);
        String token = getString(settings, "token", null);

        HttpOutputWriter.Builder<LibratoWriter> httpOutputWriter =
                builder(url, loadAppInfo(), new LibratoWriter(new JsonFactory(), source))
                        .withContentType("application/json; charset=utf-8")
                        .withTimeout(timeoutInMillis, MILLISECONDS);

        if (username != null && !username.isEmpty()) {
            httpOutputWriter.withAuthentication(username, token);
        }
        if (proxy != null) {
            httpOutputWriter.withProxy(proxy);
        }

        return new BatchingOutputWriter<>(batchSize, httpOutputWriter.build());
    }

    @Nullable
    private Proxy getProxy(@Nonnull Map<String, String> settings) {
        String proxyHost = getString(settings, "proxyHost", null);
        Integer proxyPort = getInt(settings, "proxyPort", 0);

        if (proxyHost == null || proxyHost.isEmpty()) return null;

        return new Proxy(HTTP, new InetSocketAddress(proxyHost, proxyPort));
    }

    private AppInfo loadAppInfo() {
        try {
            return AppInfo.load(LibratoWriter.class);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load AppInfo", e);
        }
    }

    private URL parseUrl(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(format("Malformed Librato URL [%s]", urlString), e);
        }
    }
}
