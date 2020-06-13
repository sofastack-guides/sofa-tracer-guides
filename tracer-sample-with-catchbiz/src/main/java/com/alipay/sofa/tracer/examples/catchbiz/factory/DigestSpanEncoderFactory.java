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
package com.alipay.sofa.tracer.examples.catchbiz.factory;
import com.alipay.common.tracer.core.constants.ComponentNameConstants;
import com.alipay.common.tracer.core.middleware.parent.AbstractDigestSpanEncoder;
import com.alipay.sofa.tracer.plugin.flexible.FlexibleDigestEncoder;
import com.alipay.sofa.tracer.plugin.flexible.FlexibleDigestJsonEncoder;
import com.alipay.sofa.tracer.plugins.datasource.tracer.DataSourceClientDigestEncoder;
import com.alipay.sofa.tracer.plugins.datasource.tracer.DataSourceClientDigestJsonEncoder;
import com.alipay.sofa.tracer.plugins.dubbo.encoder.DubboServerDigestEncoder;
import com.alipay.sofa.tracer.plugins.dubbo.encoder.DubboServerDigestJsonEncoder;
import com.alipay.sofa.tracer.plugins.httpclient.HttpClientDigestEncoder;
import com.alipay.sofa.tracer.plugins.httpclient.HttpClientDigestJsonEncoder;
import com.alipay.sofa.tracer.plugins.springmvc.SpringMvcDigestEncoder;
import com.alipay.sofa.tracer.plugins.springmvc.SpringMvcDigestJsonEncoder;
import com.sofa.alipay.tracer.plugins.rest.RestTemplateDigestEncoder;
import com.sofa.alipay.tracer.plugins.rest.RestTemplateDigestJsonEncoder;



/**
 * DigestSpanEncoderFactory
 *
 * @author chenchen
 * @since 2020/06/12
 */
public class DigestSpanEncoderFactory extends ComponentNameConstants {

    //private final Logger logger = LoggerFactory.getLogger(getClass());

    public static AbstractDigestSpanEncoder createEncoder(String nameSpace, boolean jsonFlag) {
        AbstractDigestSpanEncoder encoder = null;
        if(jsonFlag) {
            encoder = createJsonEncoder(nameSpace);
        } else {
            encoder = createEncoder(nameSpace);
        }
        return encoder;
    }

    private static AbstractDigestSpanEncoder createJsonEncoder(String nameSpace) {
        AbstractDigestSpanEncoder encoder = null;
        switch (nameSpace) {
            case FLEXIBLE:
                encoder = new FlexibleDigestJsonEncoder();
                break;
            case SPRING_MVC:
                encoder = new SpringMvcDigestJsonEncoder();
                break;
            case REST_TEMPLATE:
                encoder = new RestTemplateDigestJsonEncoder();
                break;
            case HTTP_CLIENT:
                encoder = new HttpClientDigestJsonEncoder();
                break;
            case DUBBO_SERVER:
                encoder = new DubboServerDigestJsonEncoder();
                break;
            case DUBBO_CLIENT:
                encoder = new DubboServerDigestJsonEncoder();
                break;
            case DATA_SOURCE:
                encoder = new DataSourceClientDigestJsonEncoder();
                break;
            default:
                //log.("current name not be support!");
                encoder = null;
                break;
        }
        return encoder;
    }

    private static AbstractDigestSpanEncoder createEncoder(String nameSpace) {
        AbstractDigestSpanEncoder encoder = null;
        switch (nameSpace) {
            case FLEXIBLE:
                encoder = new FlexibleDigestEncoder();
                break;
            case SPRING_MVC:
                encoder = new SpringMvcDigestEncoder();
                break;
            case REST_TEMPLATE:
                encoder = new RestTemplateDigestEncoder();
                break;
            case HTTP_CLIENT:
                encoder = new HttpClientDigestEncoder();
                break;
            case DUBBO_SERVER:
                encoder = new DubboServerDigestEncoder();
                break;
            case DUBBO_CLIENT:
                encoder = new DubboServerDigestEncoder();
                break;
            case DATA_SOURCE:
                encoder = new DataSourceClientDigestEncoder();
                break;
            default:
                //log.("current name not be support!");
                encoder = null;
                break;
        }
        return encoder;
    }
}
