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
package org.jmxtrans.core.results;

import org.testng.annotations.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import static org.jmxtrans.core.results.MetricType.COUNTER;
import static org.jmxtrans.core.results.MetricType.UNKNOWN;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryResultTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void cannotCreateQueryResultWithNullName() {
        new QueryResult(null, UNKNOWN, new Object(), 1L);
    }

    @Test
    public void initializedCorrectly() {
        QueryResult queryResult = new QueryResult("name", UNKNOWN, "value", 1L);

        assertThat(queryResult.getName()).isEqualTo("name");
        assertThat(queryResult.getType()).isEqualTo(UNKNOWN);
        assertThat(queryResult.getValue()).isEqualTo("value");
        assertThat(queryResult.getEpoch(MILLISECONDS)).isEqualTo(1L);
    }

    @Test
    public void sameValuesAreEquals() {
        assertThat(new QueryResult("name", UNKNOWN, "value", 1L))
                .isEqualTo(new QueryResult("name", UNKNOWN, "value", 1L));
    }

    @Test
    public void differentValuesAreNotEquals() {
        assertThat(new QueryResult("name", UNKNOWN, "value", 1L))
                .isNotEqualTo(new QueryResult("otherName", UNKNOWN, "value", 1L));
        assertThat(new QueryResult("name", UNKNOWN, "value", 1L))
                .isNotEqualTo(new QueryResult("name", COUNTER, "value", 1L));
        assertThat(new QueryResult("name", UNKNOWN, "value", 1L))
                .isNotEqualTo(new QueryResult("name", UNKNOWN, "otherValue", 1L));
        assertThat(new QueryResult("name", UNKNOWN, "value", 1L))
                .isNotEqualTo(new QueryResult("name", UNKNOWN, "value", 2L));
    }

    @Test
    public void sameValuesHaveSameHashCode() {
        assertThat(new QueryResult("name", UNKNOWN, "value", 1L).hashCode())
                .isEqualTo(new QueryResult("name", UNKNOWN, "value", 1L).hashCode());
    }

    @Test
    public void differentValuesHaveDifferentHashCodes() {
        assertThat(new QueryResult("name", UNKNOWN, "value", 1L).hashCode())
                .isNotEqualTo(new QueryResult("otherName", UNKNOWN, "value", 1L).hashCode());
        assertThat(new QueryResult("name", UNKNOWN, "value", 1L).hashCode())
                .isNotEqualTo(new QueryResult("name", COUNTER, "value", 1L).hashCode());
        assertThat(new QueryResult("name", UNKNOWN, "value", 1L).hashCode())
                .isNotEqualTo(new QueryResult("name", UNKNOWN, "otherValue", 1L).hashCode());
        assertThat(new QueryResult("name", UNKNOWN, "value", 1L).hashCode())
                .isNotEqualTo(new QueryResult("name", UNKNOWN, "value", 2L).hashCode());
    }
}
