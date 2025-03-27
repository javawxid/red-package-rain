package com.yunxi.user.builder.constant;

import com.yunxi.user.model.po.TbChooseSpouse;
import com.yunxi.user.model.po.TbUser;

import java.util.Date;

/**
 * @author zhiweiLiao
 * @Description
 * @Date create in 2022/9/30 0030 10:32
 */
public class ChooseSpouseConstant {

    public static TbChooseSpouse tbChooseSpouse = new TbChooseSpouse();

    static {
        tbChooseSpouse.setCreateDate(new Date());
        tbChooseSpouse.setUpdateDate(new Date());
        tbChooseSpouse.setIsDelete(0);
    }

}
