package com.example.redpackagerain.util;


import com.example.redpackagerain.entity.RobRedPackageLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description 操作string类型就是用opsForValue,操作list类型是用listOps, 操作set类型是用setOps等等
 * 这些功能都在这一个类中，使用起来其实并不是很方便，所有一般情况下，我们都是单独封装一个工具类，来把常用的一些方法进行抽象。操作的时候，直接通过工具类来操作。
 */
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
    public  void multiStr(String key,Object value, long timeout, TimeUnit unit) {
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

    public Object leftPopVoid(String key) {
        return redisTemplate.opsForList().leftPop(key);
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

    public static RedisTemplate redis;



    @PostConstruct
    public void getRedisTemplate(){
        redis = this.redisTemplate;
    }

//    RELEASE_LOCK_SCRIPT: 这个 Lua 脚本检查一个键（KEYS[1]）的值是否等于给定的值（ARGV[1]）。如果相等，则删除这个键并返回 1，否则返回 0。
    private static final String GET_LOCK_SCRIPT =
            "if redis.call('set', KEYS[1], ARGV[1], 'NX', 'EX', ARGV[2]) then " +
                    "  return 1 " +
                    "else " +
                    "  return 0 " +
                    "end";

    /**
     * todo 这里留个坑，给读者想想，高并发场景下分布式锁还有优化的空间吗？具体参考架构篇分布式锁章节进行调整。例如用redission
     * @param lockKey
     * @param requestId
     * @param expireTime
     * @return
     */
    public static boolean getLock(String lockKey, String requestId, long expireTime) {
        try {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(GET_LOCK_SCRIPT, Long.class);
            Object result = RedisUtil.redis.execute(redisScript,Collections.singletonList(lockKey), requestId, expireTime); // 直接传递整数expireTime
            // 检查返回值是否为1，如果是，则表示成功获取锁
            // 检查Object是否是Number类型的实例
            if (result != null && result instanceof Long) {
                Long value = (Long) result;
                return value == 1L;
            }
        } catch (Exception e) {
            // 异常处理，可能需要记录日志或执行其他恢复操作
            e.printStackTrace();
            // 在出现异常的情况下，可以尝试释放锁（尽管可能并没有成功获取锁）
            // 释放锁的代码可以在这里调用，但需要谨慎处理以避免误解锁
            return false;
        } finally {
            // 确保连接被正确释放
            RedisConnectionUtils.unbindConnection(RedisUtil.redis.getConnectionFactory());
        }
        return false;
    }

    /**
     * 释放锁
     * @param lockKey
     * @param value
     * @return
     */
    private static final String RELEASE_LOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "  return redis.call('del', KEYS[1]) " +
                    "else " +
                    "  return 0 " +
                    "end";

    public static boolean releaseLock(String lockKey, String requestId) {
        try {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(RELEASE_LOCK_SCRIPT, Long.class);
            Object result = RedisUtil.redis.execute(redisScript, Collections.singletonList(lockKey), requestId);
            // 检查返回值是否为1，如果是，则表示成功释放锁
            if (result != null && result instanceof Long) {
                Long value = (Long) result;
                return value == 1L;
            }
        } catch (Exception e) {
            // 异常处理，可能需要记录日志或执行其他恢复操作
            e.printStackTrace();
            return false;
        } finally {
            // 确保连接被正确释放
            RedisConnectionUtils.unbindConnection(RedisUtil.redis.getConnectionFactory());
        }
        return false;
    }

    //- - - - - - - - - - - - - - - - - - - - -  抢红包业务操作 - - - - - - - - - - - - - - - - - - - -
    //收到多少条消息
    public Long recievedMessage() {
        redisTemplate.opsForValue().increment("counter", 0);
        // 增加计数器值
        return redisTemplate.opsForValue().increment("counter");
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
    //获取红包日志
    public  List<RobRedPackageLog> rangeredPackageLog(String key, long start, long end) {
        return (List<RobRedPackageLog>)redisTemplate.opsForList().range(key, start, end);
    }
}