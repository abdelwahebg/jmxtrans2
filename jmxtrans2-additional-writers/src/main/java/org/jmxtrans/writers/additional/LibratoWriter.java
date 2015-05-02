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
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import org.jmxtrans.core.log.Logger;
import org.jmxtrans.core.log.LoggerFactory;
import org.jmxtrans.core.output.support.OutputStreamBasedOutputWriter;
import org.jmxtrans.core.results.QueryResult;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

import static com.fasterxml.jackson.core.JsonEncoding.UTF8;

@ThreadSafe
public class LibratoWriter implements OutputStreamBasedOutputWriter {

    @Nonnull private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Nonnull private final ThreadLocal<ResultsClassifier> resultsClassifier = new ThreadLocal<ResultsClassifier>() {
        @Override
        protected ResultsClassifier initialValue() {
            return new ResultsClassifier();
        }
    };
    @Nonnull private final JsonFactory jsonFactory;
    @Nonnull private final String source;

    public LibratoWriter(@Nonnull JsonFactory jsonFactory, @Nonnull String source) {
        this.jsonFactory = jsonFactory;
        this.source = source;
    }

    @Override
    public void beforeBatch(@Nonnull OutputStream out) throws IOException {
        resultsClassifier.get().clear();
    }

    @Override
    public int write(@Nonnull OutputStream out, @Nonnull QueryResult result) throws IOException {
        resultsClassifier.get().addResult(result);
        return 0;
    }

    @Override
    public int afterBatch(@Nonnull OutputStream out) throws IOException {
        try(JsonGenerator jsonGenerator = jsonFactory.createGenerator(out, UTF8)) {
            int resultsWritten = 0;
            jsonGenerator.writeStartObject();
            resultsWritten += writeResultsAs("counters", jsonGenerator, resultsClassifier.get().getCounters());
            resultsWritten += writeResultsAs("gauges", jsonGenerator, resultsClassifier.get().getGauges());
            jsonGenerator.writeEndObject();
            jsonGenerator.flush();
            return resultsWritten;
        } finally {
            resultsClassifier.remove();
        }
    }

    private int writeResultsAs(String name, JsonGenerator jsonGenerator, Iterable<QueryResult> results) throws IOException {
        int counter = 0;
        jsonGenerator.writeArrayFieldStart(name);
        for (QueryResult result : results) {
            jsonGenerator.writeStartObject();
            
            jsonGenerator.writeStringField("name", result.getName());
            jsonGenerator.writeStringField("source", source);
            jsonGenerator.writeNumberField("measure_time", result.getEpoch(SECONDS));
            
            if (result.getValue() instanceof Number) {
                writeNumberField(jsonGenerator, "value", (Number)result.getValue());
            } else {
                logger.info(format("Value for result [%s] is not a number, cannot send it to Librato", result));
            }
            
            jsonGenerator.writeEndObject();
            counter++;
        }
        jsonGenerator.writeEndArray();
        return counter;
    }

    private void writeNumberField(JsonGenerator jsonGenerator, String name, Number value) throws IOException {
        if (value instanceof Integer) {
            jsonGenerator.writeNumberField(name, (Integer)value);
        } else if (value instanceof Long) {
            jsonGenerator.writeNumberField(name, (Long)value);
        } else if (value instanceof Float) {
            jsonGenerator.writeNumberField(name, (Float)value);
        } else if (value instanceof Double) {
            jsonGenerator.writeNumberField(name, (Double)value);
        } else if (value instanceof AtomicInteger) {
            jsonGenerator.writeNumberField(name, ((AtomicInteger)value).get());
        } else if (value instanceof AtomicLong) {
            jsonGenerator.writeNumberField(name, ((AtomicLong) value).get());
        }
    }

    @NotThreadSafe
    private static final class ResultsClassifier {
        @Nonnull private final Logger logger = LoggerFactory.getLogger(getClass().getName());
        
        @Nonnull private final Queue<QueryResult> counters = new ArrayDeque<>();
        @Nonnull private final Queue<QueryResult> gauges = new ArrayDeque<>();

        public void addResult(@Nonnull QueryResult result) {
            switch (result.getType()) {
                case COUNTER:
                case TIMER:
                case SUMMARY:
                    counters.add(result);
                    break;
                case GAUGE:
                    gauges.add(result);
                    break;
                case UNKNOWN:
                    logger.info(format("Unspecified type for result [%s], export it as counter", result));
                    counters.add(result);
                    break;
            }
        }
        
        public Iterable<QueryResult> getCounters() {
            return counters;
        }
        
        public Iterable<QueryResult> getGauges() {
            return gauges;
        }
        
        public void clear() {
            counters.clear();
            gauges.clear();
        }
    }

}
