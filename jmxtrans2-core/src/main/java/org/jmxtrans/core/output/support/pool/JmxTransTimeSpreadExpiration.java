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

import java.util.concurrent.TimeUnit;

import stormpot.Expiration;
import stormpot.Poolable;
import stormpot.SlotInfo;

/**
 * This is the standard time based {@link Expiration}. It will invalidate
 * objects based on about how long ago they were allocated.
 *
 * @author Chris Vest <mr.chrisvest@gmail.com>
 * @since 2.2
 */
// TODO 3.0 make class final
// FIXME: This class has been copied from stormpot and slightly modified. It should be replaced by upstream as soon as
// https://github.com/chrisvest/stormpot/pull/95 is release. Probably with stormpot 2.4.
public class JmxTransTimeSpreadExpiration<T extends Poolable> implements Expiration<T> {

    private final long lowerBoundMillis;
    private final long upperBoundMillis;
    private final TimeUnit unit;

    /**
     * Construct a new Expiration that will invalidate objects that are older
     * than the given lower bound, before they get older than the upper bound,
     * in the given time unit.
     *
     * If the `lowerBound` is less than 1, the `upperBound` is less than the
     * `lowerBound`, or the `unit` is `null`, then an
     * {@link java.lang.IllegalArgumentException} will be thrown.
     *
     * @param lowerBound Poolables younger than this, in the given unit, are not
     *                   considered expired. This value must be at least 1.
     * @param upperBound Poolables older than this, in the given unit, are always
     *                   considered expired. This value must be greater than the
     *                   lowerBound.
     * @param unit The {@link TimeUnit} of the bounds values. Never `null`.
     */
    public JmxTransTimeSpreadExpiration(
            long lowerBound,
            long upperBound,
            TimeUnit unit) {
        if (lowerBound < 1) {
            throw new IllegalArgumentException(
                    "The lower bound cannot be less than 1.");
        }
        if (upperBound <= lowerBound) {
            throw new IllegalArgumentException(
                    "The upper bound must be greater than the lower bound.");
        }
        if (unit == null) {
            throw new IllegalArgumentException("The TimeUnit cannot be null.");
        }
        this.lowerBoundMillis = unit.toMillis(lowerBound);
        this.upperBoundMillis = unit.toMillis(upperBound);
        this.unit = unit;
    }

    /**
     * Returns `true`, with uniformly increasing probability, if the
     * {@link stormpot.Poolable} represented by the given {@link SlotInfo} is
     * older than the lower bound, eventually returning `true` with
     * 100% certainty when the age of the Poolable is equal to or greater than
     * the upper bound.
     *
     * The uniformity of the random expiration holds regardless of how often a
     * Poolable is checked. That is to say, checking a Poolable more times within
     * an interval of time, does _not_ increase its chances of being
     * declared expired.
     */
    @Override
    public boolean hasExpired(SlotInfo<? extends T> info) {
        long expirationAge = info.getStamp();
        if (expirationAge == 0) {
            long maxDelta = upperBoundMillis - lowerBoundMillis;
            expirationAge = lowerBoundMillis + Math.abs(info.randomInt() % maxDelta);
            info.setStamp(expirationAge);
        }
        long age = info.getAgeMillis();
        return age >= expirationAge;
    }

    /**
     * Produces a String representation of this TimeSpreadExpiration.
     */
    @Override
    public String toString() {
        long lower = unit.convert(lowerBoundMillis, TimeUnit.MILLISECONDS);
        long upper = unit.convert(upperBoundMillis, TimeUnit.MILLISECONDS);
        return "TimeSpreadExpiration(" + lower + " to " + upper + " " + unit + ")";
    }

}
