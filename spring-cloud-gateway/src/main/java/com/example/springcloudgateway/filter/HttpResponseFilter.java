package com.example.springcloudgateway.filter;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.nacos.common.utils.StringUtils;
import com.example.springcloudgateway.api.ResultCode;
import com.example.springcloudgateway.api.ResultData;
import com.example.springcloudgateway.constant.GateWayConstant;
import com.example.springcloudgateway.properties.NotAuthUrlProperties;
import com.example.springcloudgateway.util.JsonUtils;
import com.example.springcloudgateway.util.JwtUtils;
import com.example.springcloudgateway.util.RedisUtil;
import io.jsonwebtoken.Claims;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.DigestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.security.PublicKey;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 网关过滤
 */
@Component
@Order(0)
@EnableConfigurationProperties(value = NotAuthUrlProperties.class)
public class HttpResponseFilter implements GlobalFilter, InitializingBean {

    protected final static String parameterReg = "-{28}([0-9]{24})\r\n.+name=\"(\\S*)\"\r\n\r\n(\\S*)";
    protected final static String fileParameterReg = "-{28}([0-9]{24})\r\n.+name=\"(\\S*)\"; filename=\"(\\S*)\"\r\n.*\r\n\r\n";

    private Logger log = LoggerFactory.getLogger(HttpResponseFilter.class);

    /**
     * jwt的公钥,需要网关启动,远程调用认证中心去获取公钥
     */
    private PublicKey publicKey;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 请求各个微服务 不需要用户认证的URL
     */
    @Autowired
    private NotAuthUrlProperties notAuthUrlProperties;

    //开发环境：dev开发，uat测试
    @Value("${security.oauth2.environment}")
    private String environment;
    @Value("${security.oauth2.appkey}")
    private String appkey;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("===========环境类型：" + environment);
        //获取公钥   http://127.0.0.1:9013/oauth/token_key
        this.publicKey = JwtUtils.genPulicKey(restTemplate,environment);
    }

    private boolean shouldSkip(String currentUrl) {
        //路径匹配器(简介SpringMvc拦截器的匹配器)
        //比如/oauth/** 可以匹配/oauth/token    /oauth/check_token等
        PathMatcher pathMatcher = new AntPathMatcher();
        for(String skipPath:notAuthUrlProperties.getShouldSkipUrls()) {
            if(pathMatcher.match(skipPath,currentUrl)) {
                return true;
            }
        }
        return false;
    }

    private ServerHttpRequest wrapHeader(ServerWebExchange serverWebExchange,Claims claims) {
        String loginUserInfo = JSON.toJSONString(claims);
        log.info("jwt的用户信息:{}",loginUserInfo);
        String userName = claims.get("additionalInfo",Map.class).get("username").toString();
        String userId = claims.get("additionalInfo",Map.class).get("userId").toString();
        //向headers中放文件，记得build
        ServerHttpRequest request = serverWebExchange.getRequest().mutate()
                .header("username",userName)
                .header("userId",userId)
                .build();
        return request;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info(GateWayConstant.REQUEST_TIME_BEGIN, new Date());
        ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
        //获取参数类型
        String contentType = exchange.getRequest().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        log.info("======content type:{}", contentType);
        // 解析参数
        OAuthRequestFactory requestFactory = new WebFluxOAuthRequestFactory();
        OAuthRequest authRequest = requestFactory.createRequest(exchange.getRequest());
        Map<String, String> requestParamsMap = new HashMap<>();
        exchange.getAttributes().put(GateWayConstant.REQUEST_TIME_BEGIN, System.currentTimeMillis());
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        //校验请求
        Mono<Void> check = check(headers, exchange, serverHttpRequest);
        if (check != null) {
            log.warn("======check未通过: {}", check);
            return check;
        }
        //1.过滤不需要认证的url,比如/oauth/**
        String currentUrl = exchange.getRequest().getURI().getPath();
        //过滤不需要认证的url
        if(shouldSkip(currentUrl)) {
            log.info(GateWayConstant.SKIP_CERTIFIED_URL,currentUrl);
        }else {
            log.info(GateWayConstant.URL_REQUIRING_AUTHENTICATION,currentUrl);
            //2. 获取token,从请求头中解析 Authorization  value:  bearer xxxxxxx或者从请求参数中解析 access_token
            //第一步:解析出我们Authorization的请求头  value为: “bearer XXXXXXXXXXXXXX”
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            String acceptLanguage = exchange.getRequest().getHeaders().getFirst("Accept-Language");
            //第二步:判断Authorization的请求头是否为空
            if(StringUtils.isEmpty(authHeader)) {
                log.warn("======需要认证的url,请求头为空");
                return checkHeader(HttpStatus.UNAUTHORIZED.value(),acceptLanguage,exchange);
            }
            //3. 校验token,拿到token后，通过公钥（需要从授权服务获取公钥）校验,校验失败或超时抛出异常 TODO (已实现)这里会调用授权服务的接口，所以授权服务和网关的并发是一样的
            //第三步 校验我们的jwt 若jwt不对或者超时都会抛出异常 TODO 高并发场景下每次都需要去授权服务器调用接口，影响性能
            Claims claims = JwtUtils.validateJwtToken(authHeader,publicKey);
            if(claims == null){
                log.warn("======校验jwt,jwt不对");
                return checkJWT(ResultCode.TOKEN_VALIDATE_FAILED.getCode(),acceptLanguage,exchange);
            }
            //4. 校验通过后，从token中获取的用户登录信息存储到请求头中
            //第四步 把从jwt中解析出来的 用户登陆信息存储到请求头中
            ServerHttpRequest httpRequest = wrapHeader(exchange, claims);
            headers.putAll(httpRequest.getHeaders());
        }
        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class)
                .publishOn(Schedulers.immediate())
                .flatMap(originalBody -> {
                    // 根据请求头，用不同的方式解析Body
                    if (StringUtils.isNotEmpty(contentType)) {
                        if (contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                            this.parseRequestBody(requestParamsMap, originalBody);
                        } else if (contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
                            this.parseRequestJson(requestParamsMap, originalBody);
                        } else if (contentType.startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                            this.parseRequestQuery(requestParamsMap, originalBody);
                        }
                    }
                    // 加载QueryParameter
                    this.parseRequestQuery(requestParamsMap, exchange.getRequest().getQueryParams());
                    log.info("所有参数：{}", JSON.toJSONString(requestParamsMap));
                    // 把信息放置到线程容器内
                    authRequest.setParameters(requestParamsMap);
                    OAuthRequestContainer.set(authRequest);
                    return Mono.just(originalBody);
                });
        log.info("所有参数：{}", JSON.toJSONString(requestParamsMap));
        // 把修改过的参数、消费过的参数，重新封装发布
        //这里创建了一个 BodyInserter 对象，它用于将某种类型的数据（这里是 Mono<String>）插入到 ReactiveHttpOutputMessage 中。modifiedBody 是一个 Mono<String>，表示要插入的响应体内容。String.class 指示响应体的数据类型。
        BodyInserter<Mono<String>, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        //这里创建了一个 CachedBodyOutputMessage 对象，它是对 ServerHttpResponse 的一个包装，用于缓存响应体。exchange 可能是一个 ServerWebExchange 对象，它代表了整个服务器端的请求-响应交换过程。headers 是响应的 HTTP 头部信息。
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
        Mono<Void> result = bodyInserter.insert(outputMessage, new BodyInserterContext())//这行代码使用前面创建的 `BodyInserter` 将 `modifiedBody` 插入到 `outputMessage` 中。
                .then(Mono.defer(() -> {//当响应体插入完成后，执行 `then` 中的操作。这里使用 `Mono.defer` 来延迟执行内部的操作，直到 `then` 被订阅。
                    ServerHttpRequest decorator = decorate(exchange, headers, outputMessage);//这是一个自定义方法，用于装饰或修改请求对象。它返回一个 `ServerHttpRequest` 对象。
                    return chain.filter(exchange.mutate().request(decorator).build());//这里调用了过滤器链（可能是 `WebFilterChain`）中的下一个过滤器。它使用了 `exchange.mutate().request(decorator).build()` 来创建一个新的 `ServerWebExchange` 对象，其中请求对象被替换为 `decorator`。
                })).onErrorResume((Function<Throwable, Mono<Void>>)//如果在上述操作中发生任何错误，`onErrorResume` 会捕获这个错误，并返回一个 `Mono<Void>`，其中执行了 `release(exchange, outputMessage, throwable)` 方法。这个方法可能用于释放资源或执行其他错误处理逻辑。
                        throwable -> release(exchange, outputMessage, throwable));
        log.info(GateWayConstant.REQUEST_TIME_END, new Date());
        return result;
    }

    /**
     * 校验请求头
     * @param code
     * @param acceptLanguage
     * @param exchange
     * @return
     */
    private Mono<Void> checkHeader(int code,String acceptLanguage,ServerWebExchange exchange){
        ResultData resultData = new ResultData();
        resultData.setStatus(false);
        resultData.setCode(code);
        String msg;
        if("en_us".equals(acceptLanguage)){
            msg = "Unauthorized";
        }else if("pl_pl".equals(acceptLanguage)){
            msg = "nieupowa?nione";
        }else if("zh_cn".equals(acceptLanguage)){
            msg = "未授权";
        }else {
            msg = "Unauthorized";
        }
        resultData.setMsg(msg);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(Objects.requireNonNull(
                        JsonUtils.toJson(resultData)).getBytes())));
    }

    /**
     * 校验jwt
     * @param code
     * @param acceptLanguage
     * @param exchange
     * @return
     */
    private Mono<Void> checkJWT(int code,String acceptLanguage,ServerWebExchange exchange){
        ResultData resultData = new ResultData();
        resultData.setStatus(false);
        resultData.setCode(code);
        String msg;
        if("en_us".equals(acceptLanguage)){
            msg = "token validate failed";
        }else if("pl_pl".equals(acceptLanguage)){
            msg = "token validate nie powiod?o si?";
        }else if("zh_cn".equals(acceptLanguage)){
            msg = "token校验失败";
        }else {
            msg = "token validate failed";
        }
        resultData.setMsg(msg);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(Objects.requireNonNull(
                        JsonUtils.toJson(resultData)).getBytes())));
    }

    /**
     * 校验参数
     * @param headers
     * @return
     */
    private Mono<Void> check(HttpHeaders headers, ServerWebExchange exchange, ServerHttpRequest serverHttpRequest) {
        String timestamp = headers.getFirst("timestamp");
        log.info("=========timestamp:" + timestamp);
        if (StringUtils.isEmpty(timestamp)) {
            return resultExchange(exchange);
        }else {
            // 假设这是你的时间戳，以毫秒为单位
            long timestampL = Long.parseLong(timestamp);
            // 获取当前时间戳
            long currentTimestamp = System.currentTimeMillis();
            // 计算100分钟后的时间戳 todo 压测使用这个
            long timestampAfterTenMinutes = currentTimestamp - Duration.of(1000000, ChronoUnit.MINUTES).toMillis();
            // 计算1分钟后的时间戳，生产环境使用较低的时间范围
//            long timestampAfterTenMinutes = currentTimestamp - Duration.of(1, ChronoUnit.MINUTES).toMillis();
            // 检查时间戳是否超过当前时间戳n分钟
            if (timestampL < timestampAfterTenMinutes) {
                log.info("提供的时间戳超过了限制。");
                return resultExchange(exchange);
            }
        }
        String acceptLanguage = headers.getFirst("Accept-Language");
        log.info("=========acceptLanguage:" + acceptLanguage);
        if (StringUtils.isEmpty(acceptLanguage)) {
            return resultExchange(exchange);
        }
        String vcode = headers.getFirst("vcode");
        log.info("=========vcode:" + vcode);
        if (StringUtils.isEmpty(vcode)) {
            return resultExchange(exchange);
        } else {
            log.info("=========与前端约定好的密钥，不进行网络传输:" + appkey);
            String keyMd5 = appkey + timestamp;
            //密钥加时间戳进行md5加密生成验签对比请求中的vcode是否一致，判断请求是否被篡改
            String generatorVcode = DigestUtils.md5DigestAsHex(keyMd5.getBytes());
            log.info("=========生成的Vcode:" + generatorVcode);
            if (!vcode.equals(generatorVcode)) {
                log.info("===========vcode校验不对");
                return resultExchange(exchange);
            }
        }
        //校验是否重复提交 防止重复提交：在Web应用中，有时可能会因为各种原因（如网络延迟、用户误操作、浏览器自动刷新等）导致用户重复提交相同的请求。如果没有适当的机制来防止这种情况，那么服务器可能会处理多次相同的请求，导致数据的不一致或其他问题。
        String commitRedisKey = GateWayConstant.TOKEN + vcode + serverHttpRequest.getURI().getRawPath();
        //加锁 在高并发场景下，如果没有分布式锁，大量的重复请求可能会同时到达，这会对系统造成巨大的压力，可能导致系统崩溃或性能下降。通过使用分布式锁，可以确保同一时间只有一个请求被处理，从而保护系统的稳定性。
        boolean success = RedisUtil.getLock(commitRedisKey, commitRedisKey, 1);
        if (!success) {
            log.info("=========请求太快了！请稍后再试！");
            return resultExchange(exchange);
        } else {
            //释放锁
            RedisUtil.releaseLock(commitRedisKey, commitRedisKey);
        }
        return null;
    }

    /**
     * @param exchange
     * @return Mono<Void>
     * @Description 定义拦截返回状态码
     * @Author zhiwei Liao
     * @Date 2021/5/21/14:56
     */
    private Mono<Void> resultExchange(ServerWebExchange exchange) {
        //定义拦截返回状态码
        ResultData resultData = new ResultData();
        resultData.setStatus(false);
        resultData.setCode(HttpStatus.NOT_ACCEPTABLE.value());
        resultData.setMsg(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase());
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(Objects.requireNonNull(
                        JsonUtils.toJson(resultData)).getBytes())));
    }

    public void parseRequestBody(Map<String, String> parameterMap, String parameterString) {
        this.regexParseBodyString(parameterReg, parameterMap, parameterString);
        this.regexParseBodyString(fileParameterReg, parameterMap, parameterString);
    }

    public void parseRequestJson(Map<String, String> parameterMap, String parameterString) {
        Object json = new JSONTokener(parameterString).nextValue();
        if(json instanceof org.json.JSONObject){
            org.json.JSONObject object = (org.json.JSONObject)json;
            for (String key : object.keySet()) {
                parameterMap.put(key, object.getString(key));
            }
        }else if (json instanceof JSONArray){
            JSONArray jsonArray = (JSONArray)json;
            for (Object value : jsonArray) {
                parameterMap.put(null,(String)value);
            }
        }


    }

    protected void parseRequestQuery(Map<String, String> parameterMap, MultiValueMap<String, String> queryParamMap) {
        if (queryParamMap != null && !queryParamMap.isEmpty()) {
            for (String key : queryParamMap.keySet()) {
                final List<String> stringList = queryParamMap.get(key);
                parameterMap.put(key, stringList != null && !stringList.isEmpty() ? StringUtils.join(Arrays.asList(stringList.toArray()), ",") : null);
            }
        }
    }

    protected void parseRequestQuery(Map<String, String> parameterMap, String parameterString) {
        final String[] paramsStr = parameterString.split("&");
        for (String s : paramsStr) {
            log.info("请求名：" + s.split("=")[0]);
            log.info("请求值：" + s.split("=")[1]);
            parameterMap.put(s.split("=")[0], s.split("=")[1]);
        }
    }

    protected void regexParseBodyString(String reg, Map<String, String> parameterMap, String bodyStr) {
        Matcher matcher = Pattern.compile(reg).matcher(bodyStr);
        while (matcher.find()) {
            parameterMap.put(matcher.group(2), matcher.group(3));
            log.info("请求参数编号：" + matcher.group(1));
            log.info("请求名：" + matcher.group(2));
            log.info("请求值：" + matcher.group(3));
        }
    }

    protected ServerHttpRequestDecorator decorate(ServerWebExchange exchange, HttpHeaders headers,
                                                  CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }

    protected Mono<Void> release(ServerWebExchange exchange,
                                 CachedBodyOutputMessage outputMessage, Throwable throwable) {
//        if (outputMessage.isCached()) {
//            return outputMessage.getBody().map(DataBufferUtils::release)
//                    .then(Mono.error(throwable));
//        }
        return Mono.error(throwable);
    }
}


