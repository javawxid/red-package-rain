package com.yunxi.user.builder.examplebuilder;

import com.yunxi.user.builder.abstractbuilder.ChooseSpouseBuilder;
import com.yunxi.user.builder.constant.ChooseSpouseConstant;
import com.yunxi.user.model.po.TbChooseSpouse;
import com.yunxi.user.model.vo.req.user.ChooseSpouseVO;
import org.springframework.beans.BeanUtils;

/**
 * @author zhiweiLiao
 * @Description 具体的构建者
 * @Date create in 2022/9/30 0030 10:26
 */
public class TbChooseSpouseBuilder extends ChooseSpouseBuilder<Object> {

    private TbChooseSpouse tbChooseSpouse = ChooseSpouseConstant.tbChooseSpouse.clone();

    /**
     * 构建tbuser
     * @return
     */
    @Override
    public TbChooseSpouse buildTbChooseSpouse() {
        return tbChooseSpouse;
    }

    /**
     * 可直接构造器赋值
     * @param chooseSpouseVO
     */
    public TbChooseSpouseBuilder(ChooseSpouseVO chooseSpouseVO) {
        BeanUtils.copyProperties(chooseSpouseVO,tbChooseSpouse);
    }


}
