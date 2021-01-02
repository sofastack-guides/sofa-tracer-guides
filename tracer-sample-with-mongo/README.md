# 使用 SOFATracer 记录 MongoDb 链路调用数据

本示例演示如何在集成了 SOFATracer 的应用，通过配置 Spring Data Mongo 的使用方式将链路数据记录在文件中。

## 环境准备

要使用 SOFABoot，需要先准备好基础环境，SOFABoot 依赖以下环境：

- JDK8
- 需要采用 Apache Maven 3.2.5 或者以上的版本来编译

## 引入 SOFATracer

在创建好一个 Spring Boot 的工程之后，接下来就需要引入 SOFABoot 的依赖，首先，需要将上文中生成的 Spring Boot 工程的 `zip` 包解压后，修改 Maven 项目的配置文件 `pom.xml`，将

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>${spring.boot.version}</version>
    <relativePath/>
</parent>
```

替换为：

```xml
<parent>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>sofaboot-dependencies</artifactId>
    <version>${sofa.boot.version}</version>
</parent>
```
这里的 ${sofa.boot.version} 指定具体的 SOFABoot 版本，参考[发布历史](https://github.com/alipay/sofa-build/releases)。

然后，在工程中添加 SOFATracer 依赖：

```xml
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>tracer-sofa-boot-starter</artifactId>
</dependency>
```

最后，在工程的 `application.properties` 文件下添加一个 SOFATracer 要使用的参数，包括`spring.application.name` 用于标示当前应用的名称；`logging.path` 用于指定日志的输出目录。

同时引入SpringDataMongo需要的相关配置信息。

```
# Application Name
spring.application.name=MongoDbDemo
# logging path
logging.path=./logs
```

## 添加 Spring Data Mongo、SOFATracer 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>

<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>tracer-sofa-boot-starter</artifactId>
</dependency>

```


## 添加一个提供 RESTful 服务的 Controller

```java
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
```
## 运行

可以将工程导入到 IDE 中运行工程里面中的 `main` 方法（本实例 main 方法在 MongoDbDemoRunApplication 中）启动应用，在控制台中看到启动打印的日志如下：

```
2021-01-02 18:50:21.597  INFO 1176 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 9001 (http) with context path ''
2021-01-02 18:50:21.604  INFO 1176 --- [           main] c.a.s.t.e.m.MongoDbDemoRunApplication    : Started MongoDbDemoRunApplication in 14.909 seconds (JVM running for 18.294)
```

调用当前暴露的Rest接口：localhost:9001/mongoDb即可。

## 查看日志

在上面的 `application.properties` 里面，我们配置的日志打印目录是 `./logs` 即当前应用的根目录（我们可以根据自己的实践需要进行配置），在当前工程的根目录下可以看到类似如下结构的日志文件：

```
./logs
├── spring.log
└── tracelog
    ├── mongodb-digest.log
    ├── mongodb-stat.log
    ├── spring-mvc-digest.log
    ├── spring-mvc-stat.log
    ├── static-info.log
    └── tracer-self.log

```

示例中通过Spring Data Mongo对Mongo进行先插后查，调用完成后可以在 mongodb-digest.log 中看到类似如下的日志，而对于每一个输出字段的含义可以看 SOFATracer 的说明文档：

```
{"time":"2021-01-02 19:00:42.737","local.app":"MongoDbDemo","traceId":"ac1dd451160958524245410031176","spanId":"0.1","span.kind":"client","result.code":"00","current.thread.name":"http-nio-9001-exec-3","time.cost.milliseconds":"29ms","method":"insert","db.statement":{ "insert" : "userInfoDocument", "ordered" : true, "$db" : "cchen", "documents" : [{ "_id" : { "$oid" : "5ff0525a750a3a049879f1d5" }, "name" : "sofa-mongo-cchen", "age" : 22, "_class" : "com.alipay.sofa.tracer.examples.mongo.eitity.UserInfoDocument" }] },"db.instance":"cchen","peer.hostname":"192.168.2.6","peer.host":"/192.168.2.6:27017","peer.port":27017,"db.type":"mongodb","sys.baggage":"","biz.baggage":""}
{"time":"2021-01-02 19:00:42.796","local.app":"MongoDbDemo","traceId":"ac1dd451160958524245410031176","spanId":"0.2","span.kind":"client","result.code":"00","current.thread.name":"http-nio-9001-exec-3","time.cost.milliseconds":"5ms","method":"find","db.statement":{ "find" : "userInfoDocument", "filter" : { "name" : "sofa-mongo-cchen" }, "limit" : 1, "singleBatch" : true, "$db" : "cchen" },"db.instance":"cchen","peer.hostname":"192.168.2.6","peer.host":"/192.168.2.6:27017","peer.port":27017,"db.type":"mongodb","sys.baggage":"","biz.baggage":""}
```
