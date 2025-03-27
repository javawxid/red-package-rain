package com.yunxi.user.service;

import com.yunxi.user.mapper.p.PUserMapper;
import com.yunxi.user.model.vo.req.followModel.FollowReqVO;
import com.yunxi.user.model.vo.req.followModel.MightKnowReqVO;
import com.yunxi.user.model.vo.req.followModel.RecentVisitsReqVO;
import com.yunxi.user.model.vo.req.followModel.TogetherReqVO;
import com.yunxi.user.model.vo.req.user.GetFollowListReqVO;
import com.yunxi.user.model.vo.rsp.user.FollowListRspVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowModelService {

    @Autowired
    PUserMapper tbUserMapper;

    public List<FollowListRspVO> getFollowList(GetFollowListReqVO reqVO) {
        return null;
    }

    public Object mightKnow(MightKnowReqVO reqVO) {

        return null;
    }

    public Object together(TogetherReqVO reqVO) {
        return null;
    }

    public Object followedFollowed(TogetherReqVO reqVO) {
        return null;
    }

    public Object follow(FollowReqVO reqVO) {
        return null;
    }

    public Object recentVisits(RecentVisitsReqVO reqVO) {
        return null;
    }
}
