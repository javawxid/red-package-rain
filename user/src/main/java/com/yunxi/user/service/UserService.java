package com.yunxi.user.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunxi.user.builder.examplebuilder.TbChooseSpouseBuilder;
import com.yunxi.user.builder.examplebuilder.TbUserBuilder;
import com.yunxi.user.constants.UserConstants;
import com.yunxi.user.converter.user.UserConverter;
import com.yunxi.user.mapper.p.PChooseSpouseMapper;
import com.yunxi.user.mapper.p.PUserMapper;
import com.yunxi.user.model.base.BaseResponse;
import com.yunxi.user.model.po.TbChooseSpouse;
import com.yunxi.user.model.po.TbRobRedPackageLog;
import com.yunxi.user.model.po.TbUser;
import com.yunxi.user.model.vo.req.login.LoginReqVO;
import com.yunxi.user.model.vo.req.login.UserPhoneReqVo;
import com.yunxi.user.model.vo.req.register.RegisterReqVO;
import com.yunxi.user.model.vo.req.user.ChooseSpouseVO;
import com.yunxi.user.model.vo.req.user.GetUserInfoReqVO;
import com.yunxi.user.model.vo.req.user.SaveUserInfoReqVO;
import com.yunxi.user.model.vo.rsp.login.UserPhoneRsqVo;
import com.yunxi.user.model.vo.rsp.user.UserRspVO;
import com.yunxi.user.property.Property;
import com.yunxi.user.util.AESUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.yunxi.user.constants.WeChatConstants.*;

/**
 * @author zhiweiLiao
 * @Description
 * @Date create in 2022/9/29 0029 14:20
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    PUserMapper tbUserMapper;

    @Autowired
    PChooseSpouseMapper tbChooseSpouseMapper;

    @Autowired
    Property property;

    @Value("${filepath}")
    private String filePath;

    public static final String KEY = "iA0`bN0&lKJ3{vH0(";

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    public UserRspVO getUserInfo(GetUserInfoReqVO reqVO) {
        TbUser tbUser = tbUserMapper.selectById(reqVO.getUserId());
//        String phone = tbUser.getMobile();
//        解密手机号
//        String decryptHexPhone = AESUtils.decryptHexString(phone);
//        tbUser.setMobile(decryptHexPhone);
        return UserConverter.INSTANCE.tbUserToUserRspVO(tbUser);
    }

    public UserRspVO register(RegisterReqVO reqVO) {
        List<TbUser> userList = tbUserMapper.selectList(new QueryWrapper<TbUser>()
                .eq("username",reqVO.getUsername())
                .eq("is_delete", 0) // 假设数据库字段名是 is_delete
                .eq("user_status", 1)); // 假设数据库字段名是 user_status
        if (CollectionUtils.isEmpty(userList)) {
            TbUser buildTbUser = new TbUserBuilder(reqVO).buildTbUser();
            buildTbUser.setUserStatus(1);
            // 密码需要加密存储
            buildTbUser.setPassword(getPassword(buildTbUser.getPassword()));
            tbUserMapper.insert(buildTbUser);
            return UserConverter.INSTANCE.tbUserToUserRspVO(buildTbUser);
        }
        return null;
    }

    public String getPassword(String password){
        return passwordEncoder.encode(password );
//        return passwordEncoder.encode(password + UserConstants.USER_SECRET);
    }

    /**
     * 如果两个加密密码相匹配，则返回 true，表示认证成功；否则返回 false，表示认证失败。
     * @param password 用户输入的明文密码
     * @param encodedPassword 数据库中存储的加密密码
     * @return
     */
    public boolean verifyPassword(String password, String encodedPassword) {
        // 使用PasswordEncoder的matches方法来比较密码
        return passwordEncoder.matches(password, encodedPassword);
//        return passwordEncoder.matches(password + UserConstants.USER_SECRET, encodedPassword);
    }

    public String userNamePasswordLogin(LoginReqVO reqVO) {
        //先通过用户名查询用户
        TbUser tbUser = tbUserMapper.selectUserByUserName(reqVO.getUsername()); // 假设数据库字段名是 user_status
        if (Objects.isNull(tbUser)) {
            return null;
        }
        //对比数据库存储的加密密码和经过程序加密的密码是否相同
        boolean matches = verifyPassword(reqVO.getPassword(), tbUser.getPassword());
        if (!matches) {
            return null;
        }
        //去鉴权服务获取用户token
        String url = property.getSecurityEnvironment() + "/oauth/token?username=" + reqVO.getUsername() + "&password=" + reqVO.getPassword() + "&grant_type=password&client_id=client_id&client_secret=client_secret&scope=all";
        String json = HttpUtil.get(url);
        JSONObject entries = JSONUtil.parseObj(json);
        String error = (String) entries.get("error");
        if (StringUtils.isEmpty(error)){
            String accessToken = (String) entries.get("access_token");
            return accessToken;
        }
        return entries.get("error_description") == null ? null : (String)entries.get("error_description");
    }

    public UserRspVO saveUserInfo(SaveUserInfoReqVO reqVO) {
        TbUser tbUser = new TbUserBuilder(reqVO).buildTbUser();
        Integer tbUserId = tbUser.getId();
        if (tbUserId == null) {
            String password = tbUser.getPassword();
            if (!StringUtils.isEmpty(password)) {
                //密码不需要查询，使用不可逆运算进行加密
                tbUser.setPassword(DigestUtils.md5(password).toString());
            }
            //手机号要查看原信息，并且对手机号还需要支持模糊查找，使用可逆加解密（AES高级加密标准）
            String phone = tbUser.getMobile();
            if (!StringUtils.isEmpty(phone)) {
                String encryptHexphone = AESUtils.encryptHexString(phone);
                tbUser.setMobile(encryptHexphone);
            }
            return tbUserMapper.insert(tbUser) != 1 ? null : UserConverter.INSTANCE.tbUserToUserRspVO(tbUser);
        }else {
            //给新人练手代码
            LambdaQueryWrapper<TbUser> lambdaQueryWrapper = new LambdaQueryWrapper<TbUser>()
                    .eq(TbUser::getId, tbUserId)
                    .eq(TbUser::getIsDelete, 0);
            //数据重复则不作修改
            TbUser user = tbUserMapper.selectOne(lambdaQueryWrapper);
            if (!Objects.isNull(user)) {
                tbUser.setUpdateDate(new Date());
                BeanUtils.copyProperties(tbUser,user);
                return tbUserMapper.updateById(tbUser) != 1 ? null : UserConverter.INSTANCE.tbUserToUserRspVO(tbUser);
            }
            return null;
        }
    }

    public Object chooseSpouse(ChooseSpouseVO reqVO) {
        TbChooseSpouse tbChooseSpouse = new TbChooseSpouseBuilder(reqVO).buildTbChooseSpouse();
        return tbChooseSpouseMapper.insert(tbChooseSpouse) == 1 ? true : null;
    }


    public BaseResponse wechatAppletLogin(String jsCode) {
        //这个是微信登录的请求地址，文档地址：https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html
        String url = JSCODE2SESSION + "?" + APPID + "=" + property.getAppidValue() + "&" + SECRET + "=" + property.getSecretValue() + "&" + JS_CODE + "=" + jsCode + "&" + GRANT_TYPE + "=" + AUTHORIZATION_CODE;
        String s = HttpUtil.get(url);
        JSONObject entries = JSONUtil.parseObj(s);
        Integer errcode = (Integer) entries.get("errcode");
/*        errcode 的合法值
        -1 	    系统繁忙，此时请开发者稍候再试
        0 	    请求成功
        40029 	code 无效
        45011 	频率限制，每个用户每分钟100次
        40226 	高风险等级用户，小程序登录拦截 。*/
        if (Objects.isNull(errcode)) {
            return BaseResponse.result("success");
        }else {
            String errmsg = (String) entries.get("errmsg");
            return BaseResponse.fail(errmsg);
        }
    }

    /**
     * 前端给的code,文档地址：https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/getPhoneNumber.html
     * 获取accessToken，文档地址:https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/access-token/auth.getAccessToken.html
     * @param userPhoneVo
     * @return
     */
    public BaseResponse getWechatAppletPhone(UserPhoneReqVo userPhoneVo) {
        String code = userPhoneVo.getCode();
        String url = TOKEN + "?" + GRANT_TYPE + "=" + CLIENT_CREDENTIAL + "&" + APPID + "=" + property.getAppidValue() + "&" + SECRET + "=" + property.getSecretValue();
        String s = HttpUtil.get(url);
        JSONObject entries = JSONUtil.parseObj(s);
        Integer errcode = (Integer) entries.get("errcode");
        if (Objects.isNull(errcode)) {//没有错误码，说明获取成功
            String accessToken = (String) entries.get(ACCESS_TOKEN);
            url = GET_USER_PHONENUMBER + "?" + ACCESS_TOKEN + "=" + accessToken;
            Map<String, Object> map = new HashMap<>();
            map.put("code", code);
            s = HttpUtil.post(url, map);
            entries = JSONUtil.parseObj(s);
            errcode = (Integer) entries.get("errcode");
            if (Objects.isNull(errcode)) {//没有错误码，说明获取成功
                JSONObject phoneInfo = (JSONObject) entries.get("phone_info");
                String phoneNumber = (String) phoneInfo.get("phoneNumber");
                String purePhoneNumber = (String) phoneInfo.get("purePhoneNumber");
                String countryCode = (String) phoneInfo.get("countryCode");
                UserPhoneRsqVo userPhoneRsqVo = new UserPhoneRsqVo();
                userPhoneRsqVo.setPhoneNumber(phoneNumber);
                userPhoneRsqVo.setPurePhoneNumber(purePhoneNumber);
                userPhoneRsqVo.setCountryCode(countryCode);
                return BaseResponse.result(userPhoneRsqVo);
            } else {
                String errmsg = (String) entries.get("errmsg");
                return BaseResponse.fail(errmsg);
            }
        }else {
            String errmsg = (String) entries.get("errmsg");
            return BaseResponse.fail( errmsg);
        }
    }



    private void setQueryWrapper(SaveUserInfoReqVO reqVO, LambdaQueryWrapper<TbUser> lambdaQueryWrapper) {
        Integer age = reqVO.getAge();
        if (!Objects.isNull(age)) {
            lambdaQueryWrapper.eq(TbUser::getAge,age);
        }
        Integer height = reqVO.getHeight();
        if (!Objects.isNull(height)) {
            lambdaQueryWrapper.eq(TbUser::getHeight,height);
        }
        Integer industry = reqVO.getIndustry();
        if (!Objects.isNull(industry)) {
            lambdaQueryWrapper.eq(TbUser::getIndustry,industry);
        }
        Integer sex = reqVO.getSex();
        if (!Objects.isNull(sex)) {
            lambdaQueryWrapper.eq(TbUser::getSex,sex);
        }
        Integer maritalStatus = reqVO.getMaritalStatus();
        if (!Objects.isNull(maritalStatus)) {
            lambdaQueryWrapper.eq(TbUser::getMaritalStatus,maritalStatus);
        }
        String nickeName = reqVO.getNickename();
        if (!StringUtils.isEmpty(nickeName)) {
            lambdaQueryWrapper.eq(TbUser::getNickename,nickeName);
        }
        String username = reqVO.getUsername();
        if (!StringUtils.isEmpty(username)) {
            lambdaQueryWrapper.eq(TbUser::getUsername,username);
        }
        
        String occupation = reqVO.getOccupation();
        if (!StringUtils.isEmpty(occupation)) {
            lambdaQueryWrapper.eq(TbUser::getOccupation,occupation);
        }
        String mobile = reqVO.getMobile();
        if (!StringUtils.isEmpty(mobile)) {
            lambdaQueryWrapper.eq(TbUser::getMobile,mobile);
        }
        Integer id = reqVO.getId();
        if (id != null) {
            lambdaQueryWrapper.eq(TbUser::getId,id);
        }
        Integer weight = reqVO.getWeight();
        if (weight != null) {
            lambdaQueryWrapper.eq(TbUser::getWeight,weight);
        }
        String education = reqVO.getEducation();
        if (!StringUtils.isEmpty(education)) {
            lambdaQueryWrapper.eq(TbUser::getEducation,education);
        }
        String departuretarget = reqVO.getDeparturetarget();
        if (!StringUtils.isEmpty(departuretarget)) {
            lambdaQueryWrapper.eq(TbUser::getDeparturetarget,departuretarget);
        }
    }


    public UserRspVO getUserinfoById(String userId) {
        TbUser tbUser = tbUserMapper.selectOne(new LambdaQueryWrapper<TbUser>()
                .eq(TbUser::getId, userId)
                .eq(TbUser::getIsDelete, 0)
        );
        return UserConverter.INSTANCE.tbUserToUserRspVO(tbUser);
    }

    public UserRspVO getUserByUserName(String userName) {
        TbUser user = tbUserMapper.selectUserByUserName(userName);
        return UserConverter.INSTANCE.tbUserToUserRspVO(user);
    }

    public Boolean updateRedPackage(List<TbRobRedPackageLog> robRedPackageLogList) {
        // TODO
        return true;
    }

    public void writeScriptFile(Long offset,Long pageSize) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors() * 2, // 核心线程数: IO类型的任务通常建议设置为CPU核心数的两倍左右
                Runtime.getRuntime().availableProcessors() * 4, // 最大线程数：IO类型的任务建议设置为核心线程数的2-4倍
                60, // 空闲线程存活时间
                TimeUnit.SECONDS, // 时间单位
                new LinkedBlockingQueue<>(10000));
        executor.execute(() -> {
            ConcurrentHashMap<String,JSONObject> map = readFileLineByLine(filePath,"userinfo.txt");
            List<TbUser> userList = tbUserMapper.selectListLimit(offset,pageSize);
            StringBuilder userTokenStr = new StringBuilder(); // 创建字符串构建器，用于存储红包内容
            StringBuilder timestampStr = new StringBuilder(); // 创建字符串构建器，用于存储红包内容
            StringBuilder keyMd5Str = new StringBuilder(); // 创建字符串构建器，用于存储红包内容
            StringBuilder userIdStr = new StringBuilder(); // 创建字符串构建器，用于存储红包内容
            for (TbUser tbUser : userList) {
                JSONObject jsonObject = map.get(tbUser.getUsername());
                if (!Objects.isNull(jsonObject)) {
                    String username = (String) jsonObject.get("username");
                    String password = (String) jsonObject.get("password");
                    LoginReqVO loginReqVO = new LoginReqVO();
                    loginReqVO.setUsername(username);
                    loginReqVO.setPassword(password);
                    String token = userNamePasswordLogin(loginReqVO);
                    userTokenStr.append("bearer " + token).append("\r\n");
                    long timestamp = System.currentTimeMillis();
                    timestampStr.append(timestamp).append("\r\n");
                    String keyMd5 = KEY + timestamp;
                    String generatorVcode = org.springframework.util.DigestUtils.md5DigestAsHex(keyMd5.getBytes());
                    keyMd5Str.append(generatorVcode).append("\r\n");
                    userIdStr.append(tbUser.getId()).append("\r\n");
                }
            }
            //这里本应该从数据库或者缓存中读取在线 参加活动的用户
            writeFile(filePath, "usertoken.txt", userTokenStr.toString()); // 将用户token写入文件
            writeFile(filePath, "timestamp.txt", timestampStr.toString()); // 将用户token写入文件
            writeFile(filePath, "vcode.txt", keyMd5Str.toString()); // 将用户token写入文件
            writeFile(filePath, "userId.txt", userIdStr.toString()); // 将用户token写入文件
            log.info("=========文件写入完毕！！！");
        });
    }

    public static ConcurrentHashMap readFileLineByLine(String directoryName, String fileName) {
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap<String,JSONObject>();
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, fileName);
        BufferedReader reader = null;
        try {
            // 使用FileReader和BufferedReader来读取文件
            reader = new BufferedReader(new FileReader(file));
            String line;
            // 逐行读取，直到读到文件末尾
            while ((line = reader.readLine()) != null) {
                // 输出每一行的内容
                JSONObject jsonObject = JSONUtil.parseObj(line);
                String username = (String) jsonObject.get("username");
                concurrentHashMap.put(username,jsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 确保流被关闭
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return concurrentHashMap;
    }

    public static void writeFile(String directoryName, String fileName, String content) {
        // 创建File对象，表示要写入的目录
        File directory = new File(directoryName);
        // 如果目录不存在，则创建该目录
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // 创建File对象，表示要写入的文件
        File file = new File(directory, fileName);
        // 使用try-with-resources语句，自动关闭流
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ScheduledExecutorService poolExecutor;
    private ScheduledFuture<?> scheduledFuture;

    public void startCreateUserTask(Long num){
        Map<String, String> hashMap = new ConcurrentHashMap<>(1000);
        Map<String, JSONObject> map = new ConcurrentHashMap<>(1000);
        // CPU100%的使用率
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors() * 2, // 核心线程数: IO类型的任务通常建议设置为CPU核心数的两倍左右
                Runtime.getRuntime().availableProcessors() * 4, // 最大线程数：IO类型的任务建议设置为核心线程数的2-4倍
                60, // 空闲线程存活时间
                TimeUnit.SECONDS, // 时间单位
                new LinkedBlockingQueue<>(10000));
        executor.execute(() -> {
            for (int i = 0; i < num; i++) {
                String uid = UUID.randomUUID().toString();
                hashMap.put(String.valueOf(i),uid);
            }
            // 使用 Stream API 对 HashMap 中的值（密码）进行编码，并创建一个新的 Map 来存储编码后的密码
            Map<String, JSONObject> encodedPasswordsMap = hashMap.entrySet().parallelStream()
                    .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), new JSONObject().put("passwordEncoder",passwordEncoder.encode(entry.getValue())).put("password",entry.getValue())))
//                    .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), new JSONObject().put("passwordEncoder",passwordEncoder.encode(entry.getValue() + UserConstants.USER_SECRET)).put("password",entry.getValue())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> newValue, ConcurrentHashMap::new));
            map.putAll(encodedPasswordsMap);
        });
        // 关闭线程池，不再接受新任务，但等待已提交的任务完成
        executor.shutdown();
        try {
            // 等待所有任务在3600秒内完成，或者超时
            if (!executor.awaitTermination(3600, TimeUnit.SECONDS)) {
                // 如果超时，可以选择取消所有未完成的任务
                System.out.println("Not all tasks finished within the timeout period. Cancelling remaining tasks.");
                executor.shutdownNow();
                // 可能需要处理任务被取消的情况map = {ConcurrentHashMap@14251}  size = 2000
            }
            // 初始化线程池
            poolExecutor = new ScheduledThreadPoolExecutor(
                    Runtime.getRuntime().availableProcessors() * 2);
            // 提交定时任务，并保存返回的ScheduledFuture对象
            scheduledFuture = poolExecutor.scheduleAtFixedRate(() -> {
                        List<TbUser> userList = new ArrayList<>(1000);
                        StringBuilder userBuilder = new StringBuilder();
                        for (int i = 0; i < num; i++) {
                            TbUser tbUser = new TbUser();
                            tbUser.setIsDelete(0);
                            tbUser.setUserStatus(1);
                            String username =  UUID.randomUUID().toString();
                            tbUser.setNickename("liao" + username);
                            JSONObject jsonObject = map.get(String.valueOf(i));
                            String passwordEncoder = (String) jsonObject.get("passwordEncoder");
                            tbUser.setUsername(username);
                            jsonObject.put("username",username);
                            jsonObject.remove("passwordEncoder");
                            userBuilder.append(jsonObject).append("\r\n");
                            tbUser.setPassword(passwordEncoder);
                            userList.add(tbUser);
                        }
                        Integer usersave = tbUserMapper.batchInsert(userList);
                        log.info("批量保存用户数量：" + usersave);
                        writeFile(filePath, "userinfo.txt", userBuilder.toString());
                    },
                    0, //这是首次执行任务前的延迟时间。它表示从当前时间开始到首次执行任务所需的等待时间。配置为0，表示无需等待
                    1, TimeUnit.SECONDS);//这是任务执行的周期时间。每次任务执行完毕后，都会等待这个时间段后再次执行任务。
        } catch (InterruptedException ie) {
            // 如果等待期间线程被中断，重新中断当前线程
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


    public void stopCreateUserTask() {
        // 如果scheduledFuture不为null，则取消定时任务
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true); // 传入true表示如果任务正在执行，则中断它
        }
        // 关闭线程池
        if (poolExecutor != null && !poolExecutor.isShutdown()) {
            poolExecutor.shutdown(); // 这不会立即停止所有任务，而是启动关闭过程
        }
    }
}
