package com.yunxi.user.model.vo.rsp.user;


/**
 * @author zhiweiLiao
 * @Description
 * @Date create in 2022/9/29 0029 14:30
 */
public class FollowListRspVO {

    Integer Id;
    String userName;
    String headPortrait;
    String blurb;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }
}
