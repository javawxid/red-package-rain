package com.yunxi.user.builder.examplebuilder;

import com.yunxi.user.builder.abstractbuilder.UserBuilder;
import com.yunxi.user.builder.constant.UserConstant;
import com.yunxi.user.model.po.TbUser;
import com.yunxi.user.model.vo.req.register.RegisterReqVO;
import com.yunxi.user.model.vo.req.user.SaveUserInfoReqVO;
import org.springframework.beans.BeanUtils;

/**
 * @author zhiweiLiao
 * @Description 具体的构建者
 * @Date create in 2022/9/30 0030 10:26
 */
public class TbUserBuilder extends UserBuilder<Object> {

    private TbUser user = UserConstant.tbUser.clone();

    /**
     * 构建tbuser
     * @return
     */
    @Override
    public TbUser buildTbUser() {
        return user;
    }

    /**
     * 可直接构造器赋值
     * @param tbUser
     */
    public TbUserBuilder(TbUser tbUser) {
        BeanUtils.copyProperties(tbUser,user);
    }

    /**
     * 通过构建者模式进行实体赋值，类型转换
     * @param tbUser
     */
    public TbUserBuilder(RegisterReqVO tbUser) {
        BeanUtils.copyProperties(tbUser,user);
    }

    public TbUserBuilder(SaveUserInfoReqVO tbUser) {
        BeanUtils.copyProperties(tbUser,user);
    }
}
