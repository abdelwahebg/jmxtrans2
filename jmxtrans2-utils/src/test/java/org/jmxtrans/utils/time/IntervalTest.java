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
package org.jmxtrans.utils.time;

import org.junit.Test;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import static org.assertj.core.api.Assertions.assertThat;

public class IntervalTest {

    @Test
    public void sameIntervalAreEquals() {
        assertThat(new Interval(1, SECONDS)).isEqualTo(new Interval(1, SECONDS));
    }

    @Test
    public void intervalsWithDifferentValuesAreNotEquals() {
        assertThat(new Interval(1, SECONDS)).isNotEqualTo(new Interval(2, SECONDS));
    }

    @Test
    public void intervalsWithDifferentUnitsAreNotEquals() {
        assertThat(new Interval(1, SECONDS)).isNotEqualTo(new Interval(1, MINUTES));
    }

    @Test
    public void toStringContainsValueAndUnit() {
        Interval interval = new Interval(10, SECONDS);
        assertThat(interval.toString()).contains("10");
        assertThat(interval.toString()).contains(SECONDS.toString());
    }

    @Test
    public void constructedCorrectly() {
        Interval interval = new Interval(1, SECONDS);
        assertThat(interval.getValue()).isEqualTo(1);
        assertThat(interval.getTimeUnit()).isEqualTo(SECONDS);
    }

    @Test
    public void intervalIsNotEqualToString() {
        assertThat(new Interval(1, SECONDS)).isNotEqualTo("");
    }

}
