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
package org.jmxtrans.core.query;

import java.util.Hashtable;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.jmxtrans.utils.mockito.MockitoTestNGListener;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Listeners(MockitoTestNGListener.class)
public class ResultNameStrategyTest {

    @Mock private Query query;

    private ResultNameStrategy resultNameStrategy;

    @BeforeMethod
    public void initResultNameStrategy() {
        resultNameStrategy = new ResultNameStrategy();
    }

    @Test
    public void defaultMetricNameIsTypeAndName() throws MalformedObjectNameException {
        Hashtable<String, String> properties = new Hashtable<>();
        properties.put("type", "BufferPool");
        properties.put("name", "direct");
        ObjectName objectName = new ObjectName("java.nio", properties);

        assertThat(resultNameStrategy.getResultName(query, objectName, QueryAttribute.builder("Count").build()))
                .isEqualTo("java.nio.BufferPool.direct.Count");
    }

    @Test
    public void defaultMetricNameIsTypeAndNameAndOtherAttributes() throws MalformedObjectNameException {
        Hashtable<String, String> properties = new Hashtable<>();
        properties.put("type", "BufferPool");
        properties.put("name", "direct");
        properties.put("other", "value");
        ObjectName objectName = new ObjectName("java.nio", properties);

        assertThat(resultNameStrategy.getResultName(query, objectName, QueryAttribute.builder("Count").build()))
                .isEqualTo("java.nio.BufferPool.direct.other__value.Count");
    }

    @Test
    public void typeButNoNameAttribute() throws MalformedObjectNameException {
        Hashtable<String, String> properties = new Hashtable<>();
        properties.put("type", "Memory");
        ObjectName objectName = new ObjectName("java.lang", properties);

        assertThat(resultNameStrategy.getResultName(query, objectName, QueryAttribute.builder("ObjectPendingFinalizationCount").build()))
                .isEqualTo("java.lang.Memory.ObjectPendingFinalizationCount");
    }
}
