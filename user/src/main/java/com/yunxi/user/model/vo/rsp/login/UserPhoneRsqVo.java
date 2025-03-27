package com.yunxi.user.model.vo.rsp.login;

import io.swagger.annotations.ApiModel;

@ApiModel("用户手机号响应")
public class UserPhoneRsqVo {

    String phoneNumber;

    String purePhoneNumber;

    String countryCode;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPurePhoneNumber() {
        return purePhoneNumber;
    }

    public void setPurePhoneNumber(String purePhoneNumber) {
        this.purePhoneNumber = purePhoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
