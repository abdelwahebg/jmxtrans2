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
package org.jmxtrans.core.output.support.pool;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import stormpot.Allocator;
import stormpot.Slot;


public class SocketAppenderAllocator implements Allocator<PoolableSocketAppender> {

    @Nonnull private final InetSocketAddress address;
    private final int socketTimeoutMillis;
    private final Charset charset;

    public SocketAppenderAllocator(@Nonnull InetSocketAddress address, int socketTimeoutMillis, Charset charset) {
        this.address = address;
        this.socketTimeoutMillis = socketTimeoutMillis;
        this.charset = charset;
    }

    @Override
    @Nonnull
    public PoolableSocketAppender allocate(@Nonnull Slot slot) throws Exception {
        // re create a Socket to ensure DNS resolution is done each time
        SocketAddress serverAddress = new InetSocketAddress(address.getHostName(), address.getPort());
        return new PoolableSocketAppender(slot, charset, serverAddress, socketTimeoutMillis);
    }

    @Override
    public void deallocate(@Nonnull PoolableSocketAppender poolableSocketAppender) throws Exception {
        poolableSocketAppender.close();
    }
}
