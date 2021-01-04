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
package com.alipay.sofa.tracer.examples.mongo.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.tracer.examples.mongo.eitity.UserInfoDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 * @author chenmudu@gmail.com   2021/1/2 18:02
 */
@RestController
public class SampleMongoController {

    private final String userName = "sofa-mongo-cchen";

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("mongoDb")
    public String dataMongoTest() {

        UserInfoDocument userInfo = new UserInfoDocument();
        userInfo.setName(userName);
        userInfo.setAge(22);
        //save user info.
        mongoTemplate.save(userInfo);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(userName));
        //find user by user name.
        UserInfoDocument one = mongoTemplate.findOne(query, UserInfoDocument.class);
        return JSONObject.toJSONString(one);
    }
}
