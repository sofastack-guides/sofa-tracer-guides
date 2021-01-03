# 使用 SOFATracer 记录 Kafka Mq 链路调用数据

本示例演示如何在集成了 SOFATracer 的应用，通过配置 Spring Kafka 的使用方式将链路数据记录在文件中。

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

同时引入 Spring Amqp 需要的相关配置信息。

```
# Application Name
spring.application.name=KafkaMqDemo
# logging path
logging.path=./logs
```

## 添加 Spring Kafka、SOFATracer 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>

<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>tracer-sofa-boot-starter</artifactId>
</dependency>

```


## 添加一个提供 RESTful 服务的 Controller

```java
@RestController
public class SampleKafkaController {

    @Autowired
    private KafkaMqProducer kafkaMqProducer;

    @GetMapping("kafkamq")
    public String testKafkaMq() {
        kafkaMqProducer.sendMessage();
        return "hi,tracer!";
    }
}
```
## 运行

可以将工程导入到 IDE 中运行工程里面中的 `main` 方法（本实例 main 方法在 MongoDbDemoRunApplication 中）启动应用，在控制台中看到启动打印的日志如下：

```
2021-01-03 16:08:47.931  INFO 3588 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 9004 (http) with context path ''
2021-01-03 16:08:47.940  INFO 3588 --- [           main] c.a.s.t.e.kafka.KafkaMqDemoApplication   : Started KafkaMqDemoApplication in 9.549 seconds (JVM running for 13.424)
```

调用当前暴露的Rest接口：localhost:9004/kafkamq。

## 查看日志

在上面的 `application.properties` 里面，我们配置的日志打印目录是 `./logs` 即当前应用的根目录（我们可以根据自己的实践需要进行配置），在当前工程的根目录下可以看到类似如下结构的日志文件：

```
./logs
├── spring.log
└── tracelog
    ├── kafkamq-consume-digest.log
    ├── kafkamq-consume-stat.log
    ├── kafkamq-send-digest.log
    ├── kafkamq-send-stat.log
    ├── spring-mvc-digest.log
    ├── spring-mvc-stat.log
    ├── static-info.log
    └── tracer-self.log

```

示例中通过Spring Amqp 对 Kafka Server 进行消息的投递与消费，调用完成后可以在 kafkamq-send-digest.log 及 kafkamq-consume-digest.log 中看到类似如下的日志，而对于每一个输出字段的含义可以看 SOFATracer 的说明文档：

```
{"time":"2021-01-03 16:09:28.188","local.app":"KafkaMqDemo","traceId":"ac1dd451160966136795110013588","spanId":"0.1","span.kind":"client","result.code":"00","current.thread.name":"http-nio-9004-exec-1","time.cost.milliseconds":"94ms","kafka.topic":"sofa-tracer-test-topic","kafka.partition":-1,"error":"","sys.baggage":"","biz.baggage":""}
```

```
{"time":"2021-01-03 16:09:28.260","local.app":"KafkaMqDemo","traceId":"ac1dd451160966136795110013588","spanId":"0.1.1","span.kind":"server","result.code":"00","current.thread.name":"org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1","time.cost.milliseconds":"67ms","kafka.topic":"sofa-tracer-test-topic","kafka.partition":0,"kafka.offset":0,"sys.baggage":"","biz.baggage":""}
```
