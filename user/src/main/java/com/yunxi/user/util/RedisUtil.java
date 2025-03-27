package com.yunxi.user.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunxi.user.model.po.TbRobRedPackageLog;
import com.yunxi.user.model.po.TbUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description 操作string类型就是用opsForValue,操作list类型是用listOps, 操作set类型是用setOps等等
 * 这些功能都在这一个类中，使用起来其实并不是很方便，所有一般情况下，我们都是单独封装一个工具类，来把常用的一些方法进行抽象。操作的时候，直接通过工具类来操作。
 */
@Slf4j
@Component
public class RedisUtil {

    @Autowired
    private  RedisTemplate redisTemplate;
    /**
     * 给一个指定的 key 值附加过期时间
     *
     * @param key
     * @param time
     * @return
     */
    public boolean expire(String key, long time) {
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }
    /**
     * 根据key 获取过期时间
     *
     * @param key
     * @return
     */
    public long getTime(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
    /**
     * 根据key 获取过期时间
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
    /**
     * 移除指定key 的过期时间
     *
     * @param key
     * @return
     */
    public boolean persist(String key) {
        return redisTemplate.boundValueOps(key).persist();
    }

    //- - - - - - - - - - - - - - - - - - - - -  String类型 - - - - - - - - - - - - - - - - - - - -

    /**
     * 根据key获取值
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        System.out.println("Key exists: " + exists);
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 将值放入缓存
     *
     * @param key   键
     * @param value 值
     * @return true成功 false 失败
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 将值放入缓存并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) -1为无期限
     * @return true成功 false 失败
     */
    public void set(String key, String value, long time) {
        if (time > 0) {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    /**
     * 批量添加 key (重复的键会覆盖)
     *
     * @param keyAndValue
     */
    public void batchSet(Map<String, String> keyAndValue) {
        redisTemplate.opsForValue().multiSet(keyAndValue);
    }

    /**
     * 批量添加 key-value 只有在键不存在时,才添加
     * map 中只要有一个key存在,则全部不添加
     *
     * @param keyAndValue
     */
    public void batchSetIfAbsent(Map<String, String> keyAndValue) {
        redisTemplate.opsForValue().multiSetIfAbsent(keyAndValue);
    }

    /**
     * 对一个 key-value 的值进行加减操作,
     * 如果该 key 不存在 将创建一个key 并赋值该 number
     * 如果 key 存在,但 value 不是长整型 ,将报错
     *
     * @param key
     * @param number
     */
    public Long increment(String key, long number) {
        return redisTemplate.opsForValue().increment(key, number);
    }

    /**
     * 对一个 key-value 的值进行加减操作,
     * 如果该 key 不存在 将创建一个key 并赋值该 number
     * 如果 key 存在,但 value 不是 纯数字 ,将报错
     *
     * @param key
     * @param number
     */
    public Double increment(String key, double number) {
        return redisTemplate.opsForValue().increment(key, number);
    }


    /**
     * 将多个操作合并为一个操作，减少 Redis 操作次数，保证原子性，事务操作，设置过期时间
     * @param key
     * @param value
     */
    public  void multiStr(String key,String value, long timeout, TimeUnit unit) {
        // 创建Redis事务对象
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        // 开启事务
        connection.multi();
        try {
            //将多个操作合并为一个操作,减少Redis 操作次数,保证操作的原子性
            redisTemplate.opsForList().leftPush(key, value);
            redisTemplate.expire(key, timeout,unit);
            // 提交事务
            connection.exec();
        } catch (Exception e) {
            // 事务回滚
            connection.discard();
        } finally {
            // 关闭连接
            connection.close();
        }
    }
    /**
     * 将多个操作合并为一个操作，减少 Redis 操作次数，保证原子性，事务操作，设置过期时间
     * @param key
     * @param value
     */
    public void multiStr(String key,Object value, long timeout, TimeUnit unit) {
        // 创建Redis事务对象
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        // 开启事务
        connection.multi();
        try {
            //将多个操作合并为一个操作,减少Redis 操作次数,保证操作的原子性
            redisTemplate.opsForList().leftPush(key, value);
            redisTemplate.expire(key, timeout,unit);
            // 提交事务
            connection.exec();
        } catch (Exception e) {
            // 事务回滚
            connection.discard();
        } finally {
            // 关闭连接
            connection.close();
        }
    }

    //- - - - - - - - - - - - - - - - - - - - -  set类型 - - - - - - - - - - - - - - - - - - - -

    /**
     * 将数据放入set缓存
     *
     * @param key 键
     * @return
     */
    public void sSet(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    /**
     * 获取变量中的值
     *
     * @param key 键
     * @return
     */
    public Set<Object> members(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 随机获取变量中指定个数的元素
     *
     * @param key   键
     * @param count 值
     * @return
     */
    public void randomMembers(String key, long count) {
        redisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     * 随机获取变量中的元素
     *
     * @param key 键
     * @return
     */
    public Object randomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }

    /**
     * 弹出变量中的元素
     *
     * @param key 键
     * @return
     */
    public Object pop(String key) {
        return redisTemplate.opsForSet().pop("setValue");
    }

    /**
     * 获取变量中值的长度
     *
     * @param key 键
     * @return
     */
    public long size(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 检查给定的元素是否在变量中。
     *
     * @param key 键
     * @param obj 元素对象
     * @return
     */
    public boolean isMember(String key, Object obj) {
        return redisTemplate.opsForSet().isMember(key, obj);
    }

    /**
     * 转移变量的元素值到目的变量。
     *
     * @param key     键
     * @param value   元素对象
     * @param destKey 元素对象
     * @return
     */
    public boolean move(String key, String value, String destKey) {
        return redisTemplate.opsForSet().move(key, value, destKey);
    }

    /**
     * 批量移除set缓存中元素
     *
     * @param key    键
     * @param values 值
     * @return
     */
    public void remove(String key, Object... values) {
        redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 通过给定的key求2个set变量的差值
     *
     * @param key     键
     * @param destKey 键
     * @return
     */
    public Set<Set> difference(String key, String destKey) {
        return redisTemplate.opsForSet().difference(key, destKey);
    }


    //- - - - - - - - - - - - - - - - - - - - -  hash类型 - - - - - - - - - - - - - - - - - - - -

    /**
     * 加入缓存
     *
     * @param key 键
     * @param map 键
     * @return
     */
    public void add(String key, Map<String, String> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 获取 key 下的 所有  hashkey 和 value
     *
     * @param key 键
     * @return
     */
    public Map<Object, Object> getHashEntries(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 验证指定 key 下 有没有指定的 hashkey
     *
     * @param key
     * @param hashKey
     * @return
     */
    public boolean hashKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 获取指定key的值string
     *
     * @param key  键
     * @param key2 键
     * @return
     */
    public String getMapString(String key, String key2) {
        return redisTemplate.opsForHash().get("map1", "key1").toString();
    }

    /**
     * 获取指定的值Int
     *
     * @param key  键
     * @param key2 键
     * @return
     */
    public Integer getMapInt(String key, String key2) {
        return (Integer) redisTemplate.opsForHash().get("map1", "key1");
    }

    /**
     * 弹出元素并删除
     *
     * @param key 键
     * @return
     */
    public String popValue(String key) {
        return redisTemplate.opsForSet().pop(key).toString();
    }

    /**
     * 删除指定 hash 的 HashKey
     *
     * @param key
     * @param hashKeys
     * @return 删除成功的 数量
     */
    public Long delete(String key, String... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 删除key
     * @param key
     * @return
     */
    public  Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 给指定 hash 的 hashkey 做增减操作
     *
     * @param key
     * @param hashKey
     * @param number
     * @return
     */
    public Long increment(String key, String hashKey, long number) {
        return redisTemplate.opsForHash().increment(key, hashKey, number);
    }

    /**
     * 给指定 hash 的 hashkey 做增减操作
     *
     * @param key
     * @param hashKey
     * @param number
     * @return
     */
    public Double increment(String key, String hashKey, Double number) {
        return redisTemplate.opsForHash().increment(key, hashKey, number);
    }

    /**
     * 获取 key 下的 所有 hashkey 字段
     *
     * @param key
     * @return
     */
    public Set<Object> hashKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取指定 hash 下面的 键值对 数量
     *
     * @param key
     * @return
     */
    public Long hashSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    //- - - - - - - - - - - - - - - - - - - - -  list类型 - - - - - - - - - - - - - - - - - - - -

    /**
     * 在变量左边添加元素值
     *
     * @param key
     * @param value
     * @return
     */
    public void leftPush(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 获取集合指定位置的值。
     *
     * @param key
     * @param index
     * @return
     */
    public Object index(String key, long index) {
        return redisTemplate.opsForList().index("list", 1);
    }

    /**
     * 获取指定区间的值。
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public  List<Object> range(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public  List<TbUser> rangeTbUser(String key, long start, long end) {
        List<Object> list = new ArrayList<>();
        try{
            list =  redisTemplate.opsForList().range(key, start, end);
        }catch (Exception e){
            log.error(e.getMessage() + "|" + key);
        }
        return list.stream().map(this::convertToAppUserinfoEntity).collect(Collectors.toList());
    }

    /**
     * 将 Object 转换为 TbUser 对象。
     * 这里需要根据实际情况实现转换逻辑，比如反序列化 JSON 字符串。
     * @param object 要转换的对象
     * @return 转换后的 TbUser 对象
     */
    private TbUser convertToAppUserinfoEntity(Object object) {
        // 这里是转换逻辑的实现，比如：
        // 1. 检查 object 是否为字符串（如果是 JSON 存储）
        // 2. 使用 JSON 反序列化库将字符串转换为 RedPackgeActivityVo 对象
        if (object instanceof String) {
            String json = (String) object;
            try {
                return new ObjectMapper().readValue(json, TbUser.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        // 如果没有合适的转换，可以抛出异常或返回 null
        throw new IllegalArgumentException("Cannot convert object to TbUser: " + object);
    }
    /**
     * 把最后一个参数值放到指定集合的第一个出现中间参数的前面，
     * 如果中间参数值存在的话。
     *
     * @param key
     * @param pivot
     * @param value
     * @return
     */
    public void leftPush(String key, String pivot, String value) {
        redisTemplate.opsForList().leftPush(key, pivot, value);
    }

    /**
     * 向左边批量添加参数元素。
     *
     * @param key
     * @param values
     * @return
     */
    public void leftPushAll(String key, String... values) {
//        redisTemplate.opsForList().leftPushAll(key,"w","x","y");
        redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 向集合最右边添加元素。
     *
     * @param key
     * @param value
     * @return
     */
    public void leftPushAll(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 向左边批量添加参数元素。
     *
     * @param key
     * @param values
     * @return
     */
    public void rightPushAll(String key, String... values) {
        //redisTemplate.opsForList().leftPushAll(key,"w","x","y");
        redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 向已存在的集合中添加元素。
     *
     * @param key
     * @param value
     * @return
     */
    public void rightPushIfPresent(String key, Object value) {
        redisTemplate.opsForList().rightPushIfPresent(key, value);
    }

    /**
     * 向已存在的集合中添加元素。
     *
     * @param key
     * @return
     */
    public long listLength(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 移除集合中的左边第一个元素。
     *
     * @param key
     * @return
     */
    public  String leftPop(String key) {
        return (String) redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 移除集合中左边的元素在等待的时间里，如果超过等待的时间仍没有元素则退出。
     *
     * @param key
     * @return
     */
    public  void leftPop(String key, long timeout, TimeUnit unit) {
        redisTemplate.opsForList().leftPop(key, timeout, unit);
    }

    /**
     * 移除集合中右边的元素。
     *
     * @param key
     * @return
     */
    public void rightPop(String key) {
        redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 移除集合中右边的元素在等待的时间里，如果超过等待的时间仍没有元素则退出。
     *
     * @param key
     * @return
     */
    public void rightPop(String key, long timeout, TimeUnit unit) {
        redisTemplate.opsForList().rightPop(key, timeout, unit);
    }

    //- - - - - - - - - - - - - - - - - - - - -  抢红包业务操作 - - - - - - - - - - - - - - - - - - - -
    //收到多少条消息
    public Long recievedMessage() {
        redisTemplate.opsForValue().increment("recieved", 0);
        // 增加计数器值
        return redisTemplate.opsForValue().increment("recieved");
    }
    //异步处理多少个任务
    public Long asynchronously() {
        //首先通过increment("counter", 0)方法获取计数器的初始值（如果计数器不存在，则创建一个初始值为0的计数器）
        redisTemplate.opsForValue().increment("counter", 0);
        // 增加计数器值
        return redisTemplate.opsForValue().increment("counter");
    }
    //获取数量
    public  Long getInitialCount() {
        return redisTemplate.opsForValue().increment("counter", 0);
    }

    // 获取红包日志
    public TbRobRedPackageLog rangeredPackageLog(String key) {
        DefaultRedisScript<TbRobRedPackageLog> script = new DefaultRedisScript<>();
        script.setScriptText("local log = redis.call('LPOP', KEYS[1])\n" +
                "if log then\n" +
                "    redis.call('DEL', KEYS[1])\n" +
                "end\n" +
                "return log");
        script.setResultType(TbRobRedPackageLog.class);
        // 执行 Lua 脚本
        String robRedPackageLog = (String) redisTemplate.execute(script, Collections.singletonList(key));
        return StringUtils.isEmpty(robRedPackageLog) ? null : stringToTbRobRedPackageLog(robRedPackageLog);
    }

    public TbRobRedPackageLog stringToTbRobRedPackageLog(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 将JSON字符串反序列化为TbRobRedPackageLog对象
            TbRobRedPackageLog robRedPackageLog = objectMapper.readValue(jsonString, TbRobRedPackageLog.class);
            return robRedPackageLog;
        } catch (IOException e) {
            // 处理反序列化异常
            e.printStackTrace();
            return null; // 或者抛出一个异常
        }
    }

    public static RedisTemplate redis;

    //    GET_LOCK_SCRIPT: 这个 Lua 脚本尝试在 Redis 中为一个特定的键（KEYS[1]）设置一个值（ARGV[1]）。如果设置成功（即该键之前不存在），则它会进一步检查这个键的值是否确实为设置的值，并且为这个键设置一个过期时间（ARGV[2]）。如果所有步骤都成功，它会返回 1，否则返回 0。
//    RELEASE_LOCK_SCRIPT: 这个 Lua 脚本检查一个键（KEYS[1]）的值是否等于给定的值（ARGV[1]）。如果相等，则删除这个键并返回 1，否则返回 0。
    private static final String GET_LOCK_SCRIPT = "if redis.call('setNx',KEYS[1],ARGV[1]) then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";

    private static final String RELEASE_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @PostConstruct
    public void getRedisTemplate(){
        redis = this.redisTemplate;
    }

    /**
     * 加锁 lockKey（锁的键），value（锁的值），和expireTime（锁的过期时间，以秒为单位）
     这个方法尝试获取一个分布式锁。它接受三个参数：锁的键（lockKey）、锁的值（value）和锁的过期时间（expireTime）。
     使用 GET_LOCK_SCRIPT Lua 脚本尝试在 Redis 中设置锁。如果成功（即返回值为 "1"），则返回 true，表示成功获取锁。
     如果在设置锁的过程中发生异常，它会捕获异常并打印堆栈跟踪，但无论如何都会返回之前设置的 ret 值。
     在 finally 块中，它尝试解绑与 Redis 的连接，这通常是为了确保资源的正确释放。
     锁的值（value）通常用于确保只有特定的请求或实例可以释放锁。这防止了其他实例或请求错误地释放锁。
     过期时间（expireTime）是为了防止死锁。如果持有锁的实例崩溃或因为某种原因没有释放锁，这个过期时间可以确保锁在一段时间后自动释放。
     这个实现使用了 Lua 脚本来确保在设置锁和检查值的过程中，Redis 的操作是原子的。这是非常重要的，因为它防止了在设置锁和检查值之间，其他请求可能插入并更改了 Redis 的状态。
     * @param lockKey
     * @param value
     * @param expireTime  默认是秒
     * @return
     */
    public static boolean getLock(String lockKey, String value, int expireTime){
        boolean ret = false;
        try{
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(GET_LOCK_SCRIPT, Long.class);
            Object result = RedisUtil.redis.execute(redisScript,new StringRedisSerializer(),
                    new StringRedisSerializer(), Collections.singletonList(lockKey),value,String.valueOf(expireTime));
            //检查返回值是否为"1"，如果是，则设置ret为true，表示成功获取锁
            ret = "1".equals(result.toString()) ;
            return ret;
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            RedisConnectionUtils.unbindConnection(RedisUtil.redis.getConnectionFactory());
        }
        return ret;
    }

    /**
     * 释放锁
     * @param lockKey
     * @param value
     * @return
     */
    public static boolean releaseLock(String lockKey, String value) {
        boolean ret = false;
        try{
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(RELEASE_LOCK_SCRIPT, Long.class);
            Object result = RedisUtil.redis.execute(redisScript, new StringRedisSerializer(), new StringRedisSerializer(),
                    Collections.singletonList(lockKey), value);
            ret = "1".equals(result.toString()) ;
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            RedisConnectionUtils.unbindConnection(RedisUtil.redis.getConnectionFactory());
        }
        return ret;
    }

}