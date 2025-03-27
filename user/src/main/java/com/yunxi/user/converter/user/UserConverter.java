package com.yunxi.user.converter.user;


import com.yunxi.user.model.po.TbUser;
import com.yunxi.user.model.vo.rsp.user.UserRspVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @Description 用户对象转换器: 对应mapper的编写：因为字段相同，我们无需用到@Mapping，关于属性不同的，其自动进行类型转换即可
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper( UserConverter.class );

    UserRspVO tbUserToUserRspVO(TbUser user);

//    TbUser registerReqVOToTbUser(RegisterReqVO reqVO);

}