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
package org.jmxtrans.core.output.support;

import java.io.IOException;

import org.jmxtrans.core.output.OutputWriter;
import org.jmxtrans.core.results.MetricType;
import org.jmxtrans.core.results.QueryResult;
import org.jmxtrans.utils.mockito.MockitoTestNGListener;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Listeners(MockitoTestNGListener.class)
public class BatchingOutputWriterTest {

    @Mock private BatchedOutputWriter targetOutputWriter;
    @Mock private QueryResult result;

    @BeforeMethod
    public void setupBatchedOutputWriter() throws IOException, InterruptedException {
        when(targetOutputWriter.write(any(QueryResult.class)))
                .thenReturn(1);
    }
    
    @Test
    public void resultsAreNotWrittenWhenBatchSizeIsNotReached() throws IOException, InterruptedException {
        int processedResultCount;
        OutputWriter batchingOutputWriter = new BatchingOutputWriter<>(2, targetOutputWriter);
        
        processedResultCount = batchingOutputWriter.write(result);
        
        assertThat(processedResultCount).isZero();
        
        processedResultCount = batchingOutputWriter.write(result);
        
        assertThat(processedResultCount).isZero();
        verify(targetOutputWriter, never()).beforeBatch();
        verify(targetOutputWriter, never()).write(any(QueryResult.class));
        verify(targetOutputWriter, never()).afterBatch();
    }

    @Test
    public void resultsAreBatchedAtAppropriateSize() throws IOException, InterruptedException {
        int processedResultCount;
        OutputWriter batchingOutputWriter = new BatchingOutputWriter<>(2, targetOutputWriter);
        batchingOutputWriter.write(result);
        batchingOutputWriter.write(result);
        processedResultCount = batchingOutputWriter.write(result); // results are batched when the next result is received
        assertThat(processedResultCount).isEqualTo(2);
        verify(targetOutputWriter, times(2)).write(any(QueryResult.class));
    }

    @Test
    public void beforeAndAfterBatchAreCalledInOrder() throws IOException, InterruptedException {
        InOrder inOrder = inOrder(targetOutputWriter);
        OutputWriter batchingOutputWriter = new BatchingOutputWriter<>(1, targetOutputWriter);
        batchingOutputWriter.write(result);
        batchingOutputWriter.write(result);
        inOrder.verify(targetOutputWriter).beforeBatch();
        inOrder.verify(targetOutputWriter).write(result);
        inOrder.verify(targetOutputWriter).afterBatch();
    }

    @Test
    public void resultsAreProcessedInOrder() throws IOException, InterruptedException {
        InOrder inOrder = inOrder(targetOutputWriter);

        QueryResult result1 = new QueryResult("my.result", MetricType.UNKNOWN, 1, 1);
        QueryResult result2 = new QueryResult("my.result", MetricType.UNKNOWN, 1, 2);
        QueryResult result3 = new QueryResult("my.result", MetricType.UNKNOWN, 1, 3);
        QueryResult result4 = new QueryResult("my.result", MetricType.UNKNOWN, 1, 4);
        QueryResult result5 = new QueryResult("my.result", MetricType.UNKNOWN, 1, 5);

        OutputWriter batchingOutputWriter = new BatchingOutputWriter<>(4, targetOutputWriter);
        batchingOutputWriter.write(result4);
        batchingOutputWriter.write(result5);
        batchingOutputWriter.write(result1);
        batchingOutputWriter.write(result3);
        batchingOutputWriter.write(result2);

        inOrder.verify(targetOutputWriter).write(result1);
        inOrder.verify(targetOutputWriter).write(result3);
        inOrder.verify(targetOutputWriter).write(result4);
        inOrder.verify(targetOutputWriter).write(result5);
        inOrder.verify(targetOutputWriter, never()).write(result2);
    }

}
