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
package com.alipay.sofa.tracer.examples.catchbiz.customized;

import com.alipay.common.tracer.core.SofaTracer;
import com.alipay.common.tracer.core.middleware.parent.AbstractDigestSpanEncoder;
import com.alipay.common.tracer.core.span.SofaTracerSpan;
import com.alipay.sofa.common.utils.StringUtil;
import com.alipay.sofa.tracer.examples.catchbiz.factory.DigestSpanEncoderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


/**
 * AsyncBizDemo
 *
 * @author chenchen(chenmudu@gmail.com)
 * @since 2020/06/12
 */
@Component
public class AsyncCatchDigestService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * It's up to you. true / false;
     */
    private final boolean jsonFlag = true;

    /**
     * common digest name.
     *
     */
    private final String commonDigestName = "common";

    /**
     * current initialCapacity value  according to the type provided by the official.
     * current value is 8, It's not actually 8.
     */
    private final Map<String, AbstractDigestSpanEncoder>   encoderCache = new ConcurrentHashMap<>(8);

    /**
     * Catch the same data as the contents of some xxx-xxx-digest.log files.
     * @param sofaTracerSpan
     * @throws IOException
     */
    public void asyncExecuteDigestData(SofaTracerSpan sofaTracerSpan) throws IOException {
        AbstractDigestSpanEncoder encoder = null;
        if(Objects.nonNull(sofaTracerSpan)) {
            SofaTracer sofaTracer = sofaTracerSpan.getSofaTracer();
            if(Objects.nonNull(sofaTracer)) {
                AbstractDigestSpanEncoder currentEncoder = null;
                String tracerType = sofaTracer.getTracerType();
                if(StringUtil.isBlank(tracerType)) {
                    // form common encoder.  generally will not be empty.
                    currentEncoder = encoderCache.get(commonDigestName);
                } else {
                    currentEncoder = encoderCache.get(tracerType);
                }
                // judge current encoder.
                if(Objects.nonNull(currentEncoder)) {
                    encoder =currentEncoder;
                } else {
                    encoder = DigestSpanEncoderFactory.createEncoder(tracerType, jsonFlag);
                    // when encoder is empty :
                    //      1. current digest not find encoder;
                    //      2. current digest is empty;
                }
            }
        }
        getCurrentCodeByEncoderFromSpan(sofaTracerSpan, encoder);
    }

    private void getCurrentCodeByEncoderFromSpan(SofaTracerSpan sofaTracerSpan, AbstractDigestSpanEncoder encoder) throws IOException {
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
            String tracerType = sofaTracerSpan.getSofaTracer().getTracerType();
            if(StringUtil.isNotBlank(tracerType)) {
                //current digest not find encoder.
                encoderCache.put(tracerType, encoder);
            } else {
                //current digest is empty.
                // put common digest for common digest.
                encoderCache.put(commonDigestName, encoder);
            }
            logger.info(code);
        }
    }

}
