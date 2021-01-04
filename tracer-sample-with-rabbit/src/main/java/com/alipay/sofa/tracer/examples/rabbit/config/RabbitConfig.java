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
package com.alipay.sofa.tracer.examples.rabbit.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *  RabbitConfig.
 *
 * @author chenchen6   2020/8/1 22:48
 */
@Configuration
public class RabbitConfig {
    @Bean
    public Exchange exchange() {
        return new TopicExchange(CommonConstans.EXCHANGE_NAME);
    }


    @Bean
    public Queue queue() {
        return  new Queue(CommonConstans.QUENE_NAME);
    }


    @Bean
    public Binding bindingFirstQueueToExchangeWithRoutingKey() {
        return BindingBuilder.bind(queue()).to(exchange()).with(CommonConstans.ROUNTING_KEY).noargs();
    }
}
