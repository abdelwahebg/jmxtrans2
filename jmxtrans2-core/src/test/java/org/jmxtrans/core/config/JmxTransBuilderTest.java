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
package org.jmxtrans.core.config;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.jmxtrans.core.scheduler.NaiveScheduler;
import org.jmxtrans.utils.io.Resource;
import org.jmxtrans.utils.io.StandardResource;

import org.junit.Test;
import org.xml.sax.SAXException;

import static org.assertj.core.api.Assertions.assertThat;

public class JmxTransBuilderTest {

    @Test
    public void schedulerIsBuilt() throws SAXException, IllegalAccessException, IOException, JAXBException, InstantiationException, ParserConfigurationException, ClassNotFoundException {
        NaiveScheduler scheduler = new JmxTransBuilder(false,
                Collections.<Resource>singletonList(new StandardResource(getConfigFile().getAbsolutePath()))).build();
        assertThat(scheduler).isNotNull();
    }

    @Test
    public void parsingErrorsAreIgnoredIfSoConfigured() throws SAXException, IllegalAccessException, IOException, JAXBException, InstantiationException, ParserConfigurationException, ClassNotFoundException {
        NaiveScheduler scheduler = new JmxTransBuilder(true,
                Collections.<Resource>singletonList(new StandardResource("non-existing.xml"))).build();
        assertThat(scheduler).isNotNull();
    }

    @Test(expected = JmxtransConfigurationException.class)
    public void parsingErrorsAreRaisedIfSoConfigured() throws SAXException, IllegalAccessException, IOException, JAXBException, InstantiationException, ParserConfigurationException, ClassNotFoundException {
        NaiveScheduler scheduler = new JmxTransBuilder(false,
                Collections.<Resource>singletonList(new StandardResource("non-existing.xml"))).build();
        assertThat(scheduler).isNotNull();
    }

    @Test(expected = JmxtransConfigurationException.class)
    public void parsingErrorsAreRaisedIfNoParserFound() throws SAXException, IllegalAccessException, IOException, JAXBException, InstantiationException, ParserConfigurationException, ClassNotFoundException {
        NaiveScheduler scheduler = new JmxTransBuilder(false,
                Collections.<Resource>singletonList(new StandardResource("non-parseable.txt"))).build();
        assertThat(scheduler).isNotNull();
    }

    private File getConfigFile() {
        return new File(getClass().getClassLoader().getResource("org/jmxtrans/core/config/simple-configuration.xml").getFile());
    }
}
