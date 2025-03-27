package com.example.redpackagerain.util;

import com.example.redpackagerain.model.RedPackageActivityVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 检查 RedisTemplate 是否被正确注入
     * @param redisTemplate
     */
    @Autowired
    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        if (redisTemplate != null) {
            log.info("=====RedisTemplate was injected successfully.");
        } else {
            log.error("========RedisTemplate is null, injection failed.");
        }
    }
    /**
     * 指定缓存失效时间
     * @param key 键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key,long time){
        try {
            if(time>0){
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean expire(String key,long time,TimeUnit timeUnit){
        try {
            if(time>0){
                redisTemplate.expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key){
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String ... key){
        if(key!=null&&key.length>0){
            if(key.length==1){
                redisTemplate.delete(key[0]);
            }else{
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    //============================String=============================
    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key){
        return key==null?null:redisTemplate.opsForValue().get(key);
    }

    public List<String> redPackageIds(String redPackageId) {
        // 从 Redis 获取数据
        Object result = this.get(redPackageId);
        if (result instanceof String) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                // 将 JSON 字符串反序列化为 List<String>
                List<String> redPackageIds = objectMapper.readValue((String) result, List.class);
                return redPackageIds;
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize JSON string to List<String>", e);
            }
        } else {
            throw new IllegalArgumentException("Expected a JSON string, but got: " + result.getClass().getSimpleName());
        }
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key,Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key,Object value,long time){
        try {
            if(time>0){
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            }else{
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     * @param key 键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    //================================Map=================================
    /**
     * HashGet
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key,String item){
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object,Object> hmget(String key){
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String,Object> map){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String,Object> map, long time){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if(time>0){
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key,String item,Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key,String item,Object value,long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if(time>0){
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item){
        redisTemplate.opsForHash().delete(key,item);
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item){
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param key 键
     * @param item 项
     * @param by 要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item,double by){
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item,double by){
        return redisTemplate.opsForHash().increment(key, item,-by);
    }

    //============================set=============================
    /**
     * 根据key获取Set中的所有值
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key){
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据value从一个set中查询,是否存在
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key,Object value){
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存，并设置过期时间
     * @param key 键
     * @param values 值 可以是多个
     * @param ttl 过期时间（秒）
     * @return 成功添加到集合中的元素数量
     */
    public long sSet(String key,long ttl, Object...values) {
        String luaScript =
                "local key = KEYS[1]\n" +
                        "local values = {unpack(ARGV, 1, #ARGV-1)}\n" +
                        "local ttl = ARGV[#ARGV]\n" +
                        "local addedCount = redis.call('SADD', key, unpack(values))\n" +
                        "if addedCount > 0 then\n" +
                        "    redis.call('EXPIRE', key, ttl)\n" +
                        "end\n" +
                        "return addedCount";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(Long.class);
        // 第一个参数是键，其余的是值和过期时间
        List<String> keys = Collections.singletonList(key);
        Object[] allArgs = new Object[values.length + 1];
        System.arraycopy(values, 0, allArgs, 0, values.length);
        allArgs[values.length] = ttl; // 添加过期时间到参数数组末尾
        Long result = redisTemplate.execute(redisScript, keys, allArgs);
        // Lua脚本返回添加到集合中的元素数量
        return result == null ? 0 : result;
    }

    /**
     * 将set数据放入缓存
     * @param key 键
     * @param time 时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key,long time,Object...values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if(time>0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key){
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object ...values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     * @param key 键
     * @param start 开始
     * @param end 结束  0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end){
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取list缓存的长度
     * @param key 键
     * @return
     */
    public long lGetListSize(String key){
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     * @param key 键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key,long index){
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index,Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key,long count,Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 模糊查询获取key值
     * @param pattern
     * @return
     */
    public Set keys(String pattern){
        return redisTemplate.keys(pattern);
    }

    /**
     * 使用Redis的消息队列
     * @param channel
     * @param message 消息内容
     */
    public void convertAndSend(String channel, Object message){
        redisTemplate.convertAndSend(channel,message);
    }



    /**
     * 根据起始结束序号遍历Redis中的list
     * @param listKey
     * @param start  起始序号
     * @param end  结束序号
     * @return
     */
    public List<Object> rangeList(String listKey, long start, long end) {
        //绑定操作
        BoundListOperations<String, Object> boundValueOperations = redisTemplate.boundListOps(listKey);
        //查询数据
        return boundValueOperations.range(start, end);
    }
    /**
     * 弹出右边的值 --- 并且移除这个值
     * @param listKey
     */
    public Object rifhtPop(String listKey){
        //绑定操作
        BoundListOperations<String, Object> boundValueOperations = redisTemplate.boundListOps(listKey);
        return boundValueOperations.rightPop();
    }

    //=========BoundListOperations 用法 End============

    public String leftPop(String key) {
        return (String) redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 使用Redis的管道（pipelining）功能，这允许你一次发送多个命令到Redis服务器，并一次性接收所有响应
     * 注意，尽管使用了管道，但每个LPOP操作仍然是独立的，并且不会保证原子性。此外，如果某个key不存在或列表为空，那么对应的结果将是null。
     * 管道主要用于减少网络往返次数，但并不会减少Redis服务器上执行的命令数量。每个LPOP命令仍然需要在Redis服务器上单独执行。
     * @param keys
     * @return
     */
    public List<String> leftPopBatch(List<String> keys) {
        final List<String> results = new ArrayList<>();
        if (CollectionUtils.isEmpty(keys) || redisTemplate == null) {
            // 如果keys为空或者redisTemplate没有初始化，直接返回空列表
            return results;
        }
        redisTemplate.execute(new RedisCallback<Void>() {
            @Override
            public Void doInRedis(RedisConnection connection) throws DataAccessException {
                for (String key : keys) {
                    if (key != null) {
                        byte[] rawResult = connection.lPop(key.getBytes());
                        if (rawResult != null) {
                            results.add(new String(rawResult));
                        }
                    }
                }
                return null;
            }
        }, true); // true 表示启用管道
        return results;
    }

    public void setRedPackages(String key, List<String> redPackages) {
        // 使用RedisTemplate的opsForList().rightPushAll()方法将列表元素逐个推入Redis的列表中
        // 注意：这里使用了rightPushAll，它会将列表中的所有元素从右边推入Redis的列表中
        // 如果您想要从左边推入，可以使用leftPushAll方法
        redisTemplate.opsForList().rightPushAll(key, redPackages);
    }

    public List<Object> getRedPackages(String key) {
        // 使用RedisTemplate的opsForList().range()方法获取指定范围内的元素
        // 这里使用0作为起始索引，-1作为结束索引，表示获取列表中的所有元素
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    // 如果您之前存储的是字符串列表，并且想要获取字符串列表，可以将Object类型转换为String
    public List<String> getRedPackagesAsStringList(String key) {
        // 获取原始对象列表
        List<Object> objects = getRedPackages(key);
        // 将对象列表转换为字符串列表
        List<String> strings = objects.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        return strings;
    }

    public Boolean multi(String key, String value, long timeout) {
        String script = "local key = KEYS[1]\n" +
                "local value = ARGV[1]\n" +
                "local ttl = ARGV[2]\n" + // 新增这一行来获取过期时间
                "redis.call('LPUSH', key, value)\n" +
                "redis.call('EXPIRE', key, ttl)\n" + // 新增这一行来设置过期时间
                "return true";
        DefaultRedisScript<Boolean> luaScript = new DefaultRedisScript<>();
        luaScript.setScriptText(script);
        luaScript.setResultType(Boolean.class);
        // 注意：这里传递了一个额外的参数给 Lua 脚本，即过期时间 ttl是一个长整型数值，表示过期时间的秒数。
        Boolean result = redisTemplate.execute(luaScript, Collections.singletonList(key), value, timeout);
        // 因为 Lua 脚本中总是返回 true，所以这里的 result 也总是 true，除非执行脚本时发生异常
        if (result != null) {
            // 操作成功
            return true;
        } else {
            // 操作失败或发生异常
            return false;
        }
//        return redisTemplate.opsForList().leftPush(key, value) !=  null ? true : false;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public void leftPushAll(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public Boolean leftPushAll(String key, BigDecimal[] value, long timeout, TimeUnit unit) {
        Boolean result = false;
        // 创建Redis事务对象
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        // 开启事务
        connection.multi();
        try {
            //将多个操作合并为一个操作,减少Redis 操作次数,保证操作的原子性
            // leftPush方法会返回更新后的列表长度：如果返回的结果为 0，这并不是表示执行失败，这通常表示列表在插入操作之前是空的，并且现在列表中只有一个元素，即刚刚插入的元素。
            // 如果 size 是 0，我们知道列表在插入操作之前是空的，现在它有一个元素。
            //执行失败通常意味着由于某种原因（如网络问题、Redis 服务器不可用、键的类型不匹配等）操作没有完成，并且通常会抛出异常。
            Long size =  redisTemplate.opsForList().leftPushAll(key, value);
            //设置过期时间成功返回true
            Boolean expire = redisTemplate.expire(key, timeout, unit);
            // 提交事务
            connection.exec();
            //如果
            if (size != null && expire){
                result = true;
            }
        } catch (Exception e) {
            // 事务回滚
            connection.discard();
        } finally {
            // 关闭连接
            connection.close();
        }
        return result;
    }

    /**
     * 将BigDecimal数组推入Redis列表
     * @param key Redis的key
     * @param value BigDecimal数组
     */
    public void leftPushAll(String key, BigDecimal[] value) {
        ListOperations<String, Object> opsForList = redisTemplate.opsForList();
        opsForList.leftPushAll(key, value);
    }

    /**
     * 从Redis列表中取出所有元素
     * @param key Redis的key
     * @return List<Object> 列表中的所有元素
     */
    public List<Object> getAllFromList(String key) {
        ListOperations<String, Object> opsForList = redisTemplate.opsForList();
        // 使用range方法取出所有元素，从索引0开始到最后一个元素(-1表示最后一个元素的索引)
        return opsForList.range(key, 0, -1);
    }

    /**
     * 从Redis列表中取出所有元素，并转换为BigDecimal数组
     * @param key Redis的key
     * @return BigDecimal[] 列表中的所有元素转换为BigDecimal数组
     */
    public BigDecimal[] getAllFromListAsBigDecimal(String key) {
        ListOperations<String, Object> opsForList = redisTemplate.opsForList();
        // 取出所有元素
        List<Object> elements = opsForList.range(key, 0, -1);
        // 创建一个ArrayList来存储转换后的BigDecimal对象
        List<BigDecimal> bigDecimals = new ArrayList<>(elements.size());
        // 遍历列表，将每个元素转换为BigDecimal
        for (Object element : elements) {
            // 假设存储在Redis中的是BigDecimal的字符串表示形式
            // 如果使用的是其他序列化方式，这里需要相应地调整转换逻辑
            bigDecimals.add(new BigDecimal(element.toString()));
        }
        // 将ArrayList转换为数组并返回
        return bigDecimals.toArray(new BigDecimal[0]);
    }

    private static final int BATCH_SIZE = 1000; // 定义每批处理的数据量

    /**
     * 使用Lua脚本将多个值推送到Redis list的左侧，并设置过期时间。
     * @param key       Redis list的key
     * @param values    要推送的BigDecimal数组
     * @param timeout   过期时间
     * @param unit      过期时间的单位
     * @return          推送的元素数量（注意：这里只是返回values数组的长度）
     */
    public long leftPushAllWithTTL(String key, BigDecimal[] values, long timeout, TimeUnit unit) {
        int batchSize = 1000; // 你可以根据需要调整这个批次大小
        long totalPushed = 0;
        for (int i = 0; i < values.length; i += batchSize) {
            int end = Math.min(i + batchSize, values.length);
            BigDecimal[] batchValues = Arrays.copyOfRange(values, i, end);
            String luaScript =
                    "local key = KEYS[1]\n" +
                            "local values = {unpack(ARGV, 1, #ARGV)}\n" +
                            "for i, v in ipairs(values) do\n" +
                            "    redis.call('LPUSH', key, v)\n" +
                            "end\n" +
                            "return #values";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(luaScript);
            redisScript.setResultType(Long.class);
            Object[] scriptArgs = new Object[batchValues.length];
            for (int j = 0; j < batchValues.length; j++) {
                if (batchValues[j] != null) {//每六十为一个批次，所有存在一个批次里面有空值，需要空判断
                    scriptArgs[j] = batchValues[j].toPlainString();
                }
            }
            Long result = redisTemplate.execute(redisScript, Collections.singletonList(key), scriptArgs);
            totalPushed += result == null ? 0 : result;
        }
        // 设置过期时间
        redisTemplate.expire(key, timeout, unit);
        return totalPushed;
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

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 使用Lua脚本设置Redis的值，并设置过期时间。
     * @param key       Redis的key
     * @param value     要设置的值，应该能够转换为字符串
     * @param timeout   过期时间
     * @param unit      过期时间的单位，不能为null
     */
    public void setWithTTL(String key, Object value, long timeout, TimeUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("TimeUnit cannot be null");
        }
        if (timeout <= 0) {
            throw new IllegalArgumentException("Timeout must be a positive number");
        }

        // 序列化对象到字符串
        String valueString;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            valueString = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize value to string", e);
        }

        // 转换过期时间为秒
        long ttlInSeconds = unit.toSeconds(timeout);
        if (ttlInSeconds <= 0) {
            throw new IllegalArgumentException("TTL in seconds must be a positive number");
        }
        String luaScript =
                "local key = KEYS[1]\n" +
                        "local value = ARGV[1]\n" +
                        "local ttl = tonumber(ARGV[2])\n" +
                        "if ttl == nil or ttl <= 0 then\n" +
                        "    error('TTL must be a positive number')\n" +
                        "end\n" +
                        "redis.call('SET', key, value)\n" +
                        "redis.call('EXPIRE', key, ttl)\n" +
                        "return 'ok'";

        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        // 执行Lua脚本
        try {
            redisTemplate.execute(redisScript, Collections.singletonList(key), valueString, ttlInSeconds);
        } catch (Exception e) {
            // 在这里添加更详细的日志记录，包括key, valueString, ttlInSeconds等参数的值
            log.error("Error executing Lua script for key: {}, value: {}, ttlInSeconds: {}", key, valueString, ttlInSeconds, e);
            throw e;
        }
    }

    /**
     * 删除Redis List中的某一个元素。
     * @param key       Redis的key
     * @param element   要删除的元素
     * @param count     删除的个数，0表示删除所有匹配的元素
     */
    public void removeElementFromList(String key, Object element, long count) {
        String elementString;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            elementString = String.valueOf(element);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize element to string", e);
        }
        // 检查键的类型
        DataType type = redisTemplate.type(key);
        if (DataType.LIST.equals(type)) {
            // 键是列表类型，直接执行LREM命令
            String luaScript =
                    "local key = KEYS[1]\n" +
                            "local element = ARGV[1]\n" +
                            "local count = tonumber(ARGV[2])\n" +
                            "if count == nil then\n" +
                            "    error('Count must be a number')\n" +
                            "end\n" +
                            "return redis.call('LREM', key, count, element)";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(luaScript);
            redisScript.setResultType(Long.class);
            try {
                Long removedCount = redisTemplate.execute(redisScript, Collections.singletonList(key), elementString, count);
                log.info("Removed {} elements from list with key: {}", removedCount, key);
            } catch (Exception e) {
                log.error("Error executing Lua script for key: {}, element: {}, count: {}", key, elementString, count, e);
                throw e;
            }
        } else if (DataType.STRING.equals(type)) {
            // 键是字符串类型，尝试反序列化JSON字符串为列表
            String jsonList = (String) redisTemplate.opsForValue().get(key);
            try {
                List<String> list = objectMapper.readValue(jsonList, List.class);
                // 打印列表内容以调试
                log.debug("Current list: {}", list);
                // 执行删除操作
                long removedCount = list.removeIf(elementString::equals) ? 1 : 0;
                if (removedCount > 0) {
                    // 更新Redis中的值
                    redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(list));
                    log.info("Removed element from list with key: {}", key);
                } else {
                    log.info("No element matched for removal with key: {}", key);
                }
            } catch (Exception e) {
                log.error("Error processing JSON string for key: {}", key, e);
            }
        } else {
            throw new IllegalArgumentException("Key " + key + " is not a list or string type. Actual type: " + type);
        }
    }

    /**
     * 将 Redis 中的 Set 集合对象转换为 RedPackgeActivityVo 对象的集合。
     * @param key Redis 集合的键
     * @return RedPackgeActivityVo 对象的集合
     */
    public Set<RedPackageActivityVo> membersRedPackgeActivity(String key) {
        // 从 Redis 获取 Object 类型的 Set 集合
        Set<Object> objectSet = redisTemplate.opsForSet().members(key);
        // 假设有一个方法可以将 Object 转换为 RedPackgeActivityVo
        // 这里需要您自己实现 convertToObject 方法
        Set<RedPackageActivityVo> redPackgeActivityVoSet = objectSet.stream()
                .map(this::convertToRedPackgeActivityVo)
                .collect(Collectors.toSet());
        return redPackgeActivityVoSet;
    }

    /**
     * 将 Object 转换为 RedPackgeActivityVo 对象。
     * 这里需要根据实际情况实现转换逻辑，比如反序列化 JSON 字符串。
     * @param object 要转换的对象
     * @return 转换后的 RedPackgeActivityVo 对象
     */
    private RedPackageActivityVo convertToRedPackgeActivityVo(Object object) {
        // 这里是转换逻辑的实现，比如：
        // 1. 检查 object 是否为字符串（如果是 JSON 存储）
        // 2. 使用 JSON 反序列化库将字符串转换为 RedPackgeActivityVo 对象
         if (object instanceof String) {
             String json = (String) object;
             try {
                 return new ObjectMapper().readValue(json, RedPackageActivityVo.class);
             } catch (JsonProcessingException e) {
                 e.printStackTrace();
             }
         }
        // 如果没有合适的转换，可以抛出异常或返回 null
        throw new IllegalArgumentException("Cannot convert object to RedPackgeActivityVo: " + object);
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
     * 获取指定区间的整数值。
     * @param key
     * @param start
     * @param end
     * @return 转换后的整数列表
     */
    public List<Integer> range(String key, int start, long end) {
        // 获取 Redis 列表中的对象
        List<Object> objectList = redisTemplate.opsForList().range(key, start, end);
        // 过滤并转换对象列表为整数列表
        List<Integer> integerList = objectList.stream()
                .filter(obj -> obj instanceof String) // 确保对象是字符串类型
                .map(obj -> (String) obj) // 将对象转换为字符串
                .map(str -> {
                    try {
                        return Integer.parseInt(str); // 尝试将字符串转换为整数
                    } catch (NumberFormatException e) {
                        // 如果转换失败，记录错误或采取其他措施，这里我们选择跳过该元素
                        return null;
                    }
                })
                .filter(Objects::nonNull) // 过滤掉转换失败（返回 null）的元素
                .collect(Collectors.toList()); // 收集转换后的整数到列表中
        return integerList;
    }
}
