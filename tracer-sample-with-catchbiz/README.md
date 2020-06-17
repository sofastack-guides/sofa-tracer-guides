# 使用 SOFATracer 监听和捕获所有的摘要数据并以默认摘要日志格式输出 xxx-xxx-digest.log日志(包括业务埋点数据)

本示例演示如何在集成了 SOFATracer 的应用，通过配置 SOFATracer 监听和捕获所有的摘要数据(包括业务埋点数据) 并以SOFATracer默认的摘要格式输出， 而且提供了在不破坏面向对象(封装)的基础上，用@Tracer跳过Spring Aop 限制进行业务埋点的案例。

下面的示例中将分别演示在 SOFABoot/SpringBoot 工程中 以及 非 SOFABoot/SpringBoot 工程中如何使用。

## 环境准备

要使用 SOFABoot，需要先准备好基础环境，SOFABoot 依赖以下环境：
- JDK7 或 JDK8
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

## 添加 SOFATracer starter

工程中添加 SOFATracer 依赖：

```xml
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>tracer-sofa-boot-starter</artifactId>
</dependency>
```

## 配置文件

最后，在工程的 `application.properties` 文件下添加一个 SOFATracer 要使用的参数，包括`spring.application.name` 用于标示当前应用的名称；`logging.path` 用于指定日志的输出目录。

```properties
# Application Name
spring.application.name=CatchBizDemo
# logging path
logging.path=./logs
# server port 
server.port=8089
```
## 添加一个最简单的 Controller

在工程代码中，添加一个最简单的 Controller，例如：

```java
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
    @RequestMapping("/springmvc")
    public Map<String, Object> springmvc(@RequestParam(value = "name", defaultValue = "SOFATracer Catch Biz Data DEMO") String name) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("success", true);
        resultMap.put("id", counter.incrementAndGet());
        resultMap.put("content", String.format(TEMPLATE, name));
        return resultMap;
    }
}
```

在启动类中添加resttemplate的相关配置，并将当前ApplicationContext对象传递至SampleRestController内，容器启动后自动请求当前springmvc接口：
```java
@EnableAsync
@SpringBootApplication
public class CatchBizDataApplication {
    private static Logger logger = LoggerFactory.getLogger(CatchBizDataApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(CatchBizDataApplication.class, args);
        SampleRestController.setApplicationContext(applicationContext);
        RestTemplate restTemplate = SofaTracerRestTemplateBuilder.buildRestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "http://localhost:8089/springmvc", String.class);
        logger.info("Response is {}", responseEntity.getBody());
    }

}
```

## 运行

可以将工程导入到 IDE 中运行生成的工程里面中的 `main` 方法（一般上在 XXXApplication 这个类中）启动应用，也可以直接在该工程的根目录下运行 `mvn spring-boot:run`，将会在控制台中看到启动打印的日志：

```
2020-06-17 20:03:55.715  INFO 15092 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8089 (http) with context path ''
2020-06-17 20:03:55.721  INFO 15092 --- [           main] c.a.s.t.e.c.CatchBizDataApplication      : Started CatchBizDataApplication in 6.398 seconds (JVM running for 8.439)
```

也可以通过在浏览器中输入 [http://localhost:8089/springmvc](http://localhost:8089/springmvc) 来再一次访问 REST 服务，结果类似如下：

```json
{
	content: "Hello, SOFATracer Catch Biz Data DEMO!",
	id: 1,
	success: true
}
```

## 查看日志

在上面的 `application.properties` 里面，我们配置的日志打印目录是 `./logs` 即当前应用的根目录（我们可以根据自己的实践需要配置），在当前工程的根目录下可以看到类似如下结构的日志文件：

```
./logs
├── spring.log
└── tracelog
    ├── biz-digest.log
    ├── biz-stat.log
    ├── resttemplate-digest.log
    ├── resttemplate-stat.log
    ├── spring-mvc-digest.log
    ├── spring-mvc-stat.log
    ├── static-info.log
    └── tracer-self.log

```

通过日志，SOFATracer 会记录每一次访问的摘要日志和统计日志，可以打开 `xxx-digest.log` 或`xxx-stat.log` 看到具体的输出内容，而对于每一个输出字段的含义可以看 SOFATracer 的说明文档。

控制台中可看到经过转换后拿到的数据为：
biz-digest.log：
```json
{"time":"2020-06-17 20:03:56.839","local.app":"CatchBizDemo","traceId":"c0a819011592395436495100115092","spanId":"0.1.2","span.kind":"client","result.code":"99","current.thread.name":"http-nio-8089-exec-1","time.cost.milliseconds":"5ms","method":"springmvc","param.types":"java.lang.String","bizDataName":"bizDataVal","bizAopDataName":"bizAopDataVal","result.code":"99","sys.baggage":"","biz.baggage":""}
```
spring-mvc-digest.log：
```json
{"time":"2020-06-17 20:03:57.079","local.app":"CatchBizDemo","traceId":"c0a819011592395436495100115092","spanId":"0.1","span.kind":"server","result.code":"200","current.thread.name":"http-nio-8089-exec-1","time.cost.milliseconds":"285ms","request.url":"http://localhost:8089/springmvc","method":"GET","req.size.bytes":-1,"resp.size.bytes":0,"sys.baggage":"","biz.baggage":""}
```

resttemplate-digest.log：
```json
{"time":"2020-06-17 20:03:57.077","local.app":"CatchBizDemo","traceId":"c0a819011592395436495100115092","spanId":"0","span.kind":"client","result.code":"200","current.thread.name":"main","time.cost.milliseconds":"582ms","request.url":"http://localhost:8089/springmvc","method":"GET","resp.size.bytes":0,"resp.size.bytes":0,"error":"","sys.baggage":"","biz.baggage":""}
```
## 对比数据
通过控制台业务采集到的数据与 ./logs/tracelog/xxx-digest.log 数据做对比，可观测到目标日志数据成功被捕获到。
SOFATracer 使用者大多关心 xxx-digest.log的数据，这样我们就可以实现以默认落盘的数据格式拿到所有的摘要信息，方便后续数据的加工，清洗，处理。

## 具体逻辑
具体的捕获逻辑和处理方案以及格式转换的开关，请查看 /src/目录下的代码。