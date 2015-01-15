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
package org.jmxtrans.example;

import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.File;
import java.io.IOException;

import org.jmxtrans.JmxTransformer;
import org.jmxtrans.cli.JmxTransConfiguration;
import org.jmxtrans.guice.JmxTransModule;
import org.jmxtrans.model.JmxProcess;
import org.jmxtrans.model.Query;
import org.jmxtrans.model.Server;
import org.jmxtrans.model.output.RRDToolWriter;
import org.jmxtrans.util.JsonPrinter;

/**
 * This example shows how to query an ActiveMQ server for some information.
 * 
 * The point of this example is to show that * works as part of the objectName.
 * It also shows that you don't have to set an attribute to get for a query.
 * jmxtrans will get all attributes on an object if you don't specify any.
 * 
 * @author jon
 */
public class ActiveMQ2 {

	private static final JsonPrinter jsonPrinter = new JsonPrinter(System.out);

	@SuppressFBWarnings(
			value = "DMI_HARDCODED_ABSOLUTE_FILENAME",
			justification = "Path to RRD binary is hardcoded as this is example code")
	public static void main(String[] args) throws Exception {
		File outputFile = new File("target/w2-TEST.rrd");
		if (!outputFile.exists() && !outputFile.createNewFile()) {
			throw new IOException("Could not create output file");
		}
		RRDToolWriter gw = RRDToolWriter.builder()
				.setTemplateFile(new File("memorypool-rrd-template.xml"))
				.setOutputFile(outputFile)
				.setBinaryPath(new File("/opt/local/bin"))
				.setDebugEnabled(true)
				.setGenerate(true)
				.addTypeName("Destination")
				.build();

		JmxProcess process = new JmxProcess(Server.builder()
				.setHost("w2")
				.setPort("1105")
				.setAlias("w2_activemq_1105")
				.addQuery(Query.builder()
						.setObj("org.apache.activemq:BrokerName=localhost,Type=Queue,Destination=*")
						.addAttr("QueueSize")
						.addAttr("MaxEnqueueTime")
						.addAttr("MinEnqueueTime")
						.addAttr("AverageEnqueueTime")
						.addAttr("InFlightCount")
						.addAttr("ConsumerCount")
						.addAttr("ProducerCount")
						.addAttr("DispatchCount")
						.addAttr("DequeueCount")
						.addAttr("EnqueueCount")
						.addOutputWriter(gw)
						.build())
				.addQuery(Query.builder()
						.setObj("org.apache.activemq:BrokerName=localhost,Type=Topic,Destination=*")
						.addAttr("QueueSize")
						.addAttr("MaxEnqueueTime")
						.addAttr("MinEnqueueTime")
						.addAttr("AverageEnqueueTime")
						.addAttr("InFlightCount")
						.addAttr("ConsumerCount")
						.addAttr("ProducerCount")
						.addAttr("DispatchCount")
						.addAttr("DequeueCount")
						.addAttr("EnqueueCount")
						.addOutputWriter(gw)
						.build()).build());
		jsonPrinter.prettyPrint(process);

		Injector injector = Guice.createInjector(new JmxTransModule(new JmxTransConfiguration()));
		JmxTransformer transformer = injector.getInstance(JmxTransformer.class);
		transformer.executeStandalone(process);
	}
}