package com.alipay.sofa.tracer.examples.catchbiz.customized;

import com.alipay.common.tracer.core.SofaTracer;
import com.alipay.common.tracer.core.middleware.parent.AbstractDigestSpanEncoder;
import com.alipay.common.tracer.core.span.SofaTracerSpan;
import com.alipay.sofa.tracer.examples.catchbiz.factory.DigestSpanEncoderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@Component
public class AsyncBizDemo {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final boolean jsonFlag = true;

    /**
     * Catch the same data as the contents of the xxx-xxx-digest.log file.
     * @param sofaTracerSpan
     * @throws IOException
     */
    public void asyncBizService(SofaTracerSpan sofaTracerSpan) throws IOException {
        AbstractDigestSpanEncoder encoder = null;
        if(Objects.nonNull(sofaTracerSpan)) {
            SofaTracer sofaTracer = sofaTracerSpan.getSofaTracer();
            if(Objects.nonNull(sofaTracerSpan.getSofaTracer())) {
                encoder = DigestSpanEncoderFactory.createEncoder(sofaTracer.getTracerType(), jsonFlag);
            }
        }
        printCurrentCodeByEncoderFromSpan(sofaTracerSpan, encoder);
    }

    private void printCurrentCodeByEncoderFromSpan(SofaTracerSpan sofaTracerSpan, AbstractDigestSpanEncoder encoder) throws IOException {
        String code = "";
        if(Objects.nonNull(encoder)) {
            code = encoder.encode(sofaTracerSpan);
            logger.info(code);

        } else {
            encoder = new AbstractDigestSpanEncoder() {
                @Override
                public String encode(SofaTracerSpan span) throws IOException {
                    return super.encode(span);
                }
            };
            code = encoder.encode(sofaTracerSpan);
            logger.info(code);
        }
    }

}
