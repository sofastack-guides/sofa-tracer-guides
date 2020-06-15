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
package com.alipay.sofa.tracer.examples.catchbiz.controller;

import com.alipay.common.tracer.core.tags.SpanTags;
import com.alipay.sofa.tracer.plugin.flexible.annotations.Tracer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SampleRestController
 *
 * @author chenchen(chenmudu@gmail.com)
 * @since 2020/06/12
 */
@RestController
public class SampleRestController {

    private static final String TEMPLATE = "Hello, %s!";

    private final AtomicLong    counter  = new AtomicLong();
    
    private static ApplicationContext appContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        appContext =  applicationContext;
    }

    /***
     * http://localhost:8089/springmvc
     * @param name name
     * @return map
     */
    @Tracer
    @RequestMapping("/springmvc")
    public Map<String, Object> springmvc(@RequestParam(value = "name", defaultValue = "SOFATracer Catch Biz Data DEMO") String name) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        SpanTags.putTags("bizDataName", "bizDataVal");
        resultMap.put("success", true);
        resultMap.put("id", counter.incrementAndGet());
        resultMap.put("content", String.format(TEMPLATE, name));
        appContext.getBean(SampleRestController.class).setOthersTagByApplicationContext();
        return resultMap;
    }

    /**
     * it's ok! but, not recommend.
     * In this way, it is independent of the modifier of the method.
     */
    @Tracer
    private void setOthersTagByApplicationContext() {
        SpanTags.putTags("bizAopDataName", "bizAopDataVal");
    }
}
