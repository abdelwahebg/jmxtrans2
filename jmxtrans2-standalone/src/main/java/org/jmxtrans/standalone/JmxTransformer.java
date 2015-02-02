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
package org.jmxtrans.standalone;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jmxtrans.core.config.JmxTransBuilder;
import org.jmxtrans.standalone.cli.JmxTransParameters;
import org.jmxtrans.utils.appinfo.AppInfo;
import org.jmxtrans.utils.io.FileResource;
import org.jmxtrans.utils.io.Resource;

import com.beust.jcommander.JCommander;

public class JmxTransformer {

    public static void main(String[] args) throws Exception {

        AppInfo.load(JmxTransformer.class).print(System.out);

        JmxTransParameters parameters = new JmxTransParameters();
        new JCommander(parameters, args);

        List<Resource> configurations = new ArrayList<>();
        for (File configFile : parameters.getConfigFiles()) {
            configurations.add(new FileResource(configFile));
        }

        new JmxTransBuilder(
                parameters.isIgnoringParsingErrors(),
                configurations).build().start();

    }

}
