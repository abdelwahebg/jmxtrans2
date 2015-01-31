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
package org.jmxtrans.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Collections;

import org.jmxtrans.core.config.JmxTransBuilder;
import org.jmxtrans.core.log.Logger;
import org.jmxtrans.core.log.LoggerFactory;
import org.jmxtrans.utils.appinfo.AppInfo;
import org.jmxtrans.utils.io.Resource;
import org.jmxtrans.utils.io.StandardResource;

/**
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 */
public class JmxTransAgent {
    private static final Logger logger = LoggerFactory.getLogger(JmxTransAgent.class.getName());

    public static void premain(String configFile, Instrumentation inst) throws IOException {

        AppInfo.load(JmxTransAgent.class).print(System.out);

        if (configFile == null || configFile.isEmpty()) {
            String msg = "JmxTransAgent configurationFile must be defined";
            logger.error(msg);
            throw new IllegalStateException(msg);
        }

        try {
            new JmxTransBuilder(false, Collections.<Resource>singletonList(new StandardResource(configFile))).build().start();
            logger.info("JmxTransAgent started with configuration '" + configFile + "'");
        } catch (Exception e) {
            String msg = "Exception loading JmxTransExporter from '" + configFile + "'";
            logger.error(msg, e);
            throw new IllegalStateException(msg, e);
        }
    }
}
