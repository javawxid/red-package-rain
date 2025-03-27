package com.yunxi.user.model.response;

import lombok.Data;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

/**
 * @author zhiweiLiao
 * @Description
 * @Date create in 2022/10/28 0028 14:59
 */
@Data
public class BaseRequest extends ProductLine{

    private String token;

    //使用spring注入的方式
    private static Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    //手动验证
    public void validate() {
        StringBuilder errorMsgs = new StringBuilder();
        Set<ConstraintViolation<BaseRequest>> violations = VALIDATOR.validate(this, new Class[0]);
        if (violations != null && violations.size() > 0) {
            Iterator var3 = violations.iterator();
            while(var3.hasNext()) {
                ConstraintViolation<BaseRequest> violation = (ConstraintViolation)var3.next();
                errorMsgs.append(violation.getPropertyPath()).append(":").append(violation.getMessage()).append("|");
            }
            throw new IllegalArgumentException(errorMsgs.substring(0, errorMsgs.length() - 1));
        }
    }

}
