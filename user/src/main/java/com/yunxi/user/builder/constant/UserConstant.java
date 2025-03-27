package com.yunxi.user.builder.constant;

import com.yunxi.user.model.po.TbUser;
import java.util.Date;

/**
 * @author zhiweiLiao
 * @Description
 * @Date create in 2022/9/30 0030 10:32
 */
public class UserConstant {

    public static TbUser tbUser = new TbUser();

    static {
        tbUser.setCreateDate(new Date());
        tbUser.setUpdateDate(new Date());
        tbUser.setIsDelete(0);
    }

}
