
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
package com.alipay.sofa.tracer.examples.catchbiz.listener;

import com.alibaba.fastjson.JSONObject;
import com.alipay.common.tracer.core.listener.SpanReportListener;
import com.alipay.common.tracer.core.span.SofaTracerSpan;
import com.alipay.sofa.tracer.examples.catchbiz.customized.AsyncCatchDigestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * CustomSpanReportListener
 *
 * @author chenchen(chenmudu@gmail.com)
 * @since 2020/06/12
 */
@Component
public class CustomSpanReportListener implements SpanReportListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Executor taskExecutor;

    @Autowired
    private AsyncCatchDigestService bizAsyncService;

    //don't block the current thread(http server worker thread).
    //keeps the current operation asynchronous.
    @Override
    public void onSpanReport(SofaTracerSpan sofaTracerSpan) {
        logger.info("current tracerSpan is :" + JSONObject.toJSONString(sofaTracerSpan));
        taskExecutor.execute(() -> {
            try {
                bizAsyncService.asyncExecuteDigestData(sofaTracerSpan);
            } catch (IOException e) {
                logger.error(String.valueOf(e.getStackTrace()));
            }
        });

    }
}
