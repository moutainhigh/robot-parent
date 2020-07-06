package com.robot.jiuwu.server;


import com.bbin.common.pojo.ImageCodeDTO;
import com.robot.center.constant.RobotConsts;
import com.robot.code.response.Response;
import com.robot.core.common.TContext;
import com.robot.core.function.base.*;
import com.robot.core.http.response.StanderHttpResponse;
import com.robot.core.robot.manager.RobotWrapper;
import com.robot.jiuwu.base.common.Constant;
import com.robot.jiuwu.base.dto.JWImageCodeDTO;
import com.robot.jiuwu.base.function.ImageCodeFunction;
import com.robot.jiuwu.base.vo.ImageCodeResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


/**
 * Created by mrt on 11/14/2019 8:06 PM
 * 图片验证码
 */
@Slf4j
@Service
public class ImageCodeServer implements IAssemFunction<JWImageCodeDTO> {

    @Autowired
    private ImageCodeFunction ImageCodeFunction;

    @Autowired
    private StringRedisTemplate redis;

    @Override
    public Response doFunction(ParamWrapper<JWImageCodeDTO> paramWrapper, RobotWrapper robotWrapper) throws Exception {
        JWImageCodeDTO imageCodeDTO = paramWrapper.getObj();

        Response<Object>  imageResponse = ImageCodeFunction.doFunction(new ParamWrapper<>(imageCodeDTO),robotWrapper);
        if (!imageResponse.isSuccess()) {
            return imageResponse;
        }

        ImageCodeResultVO entity = (ImageCodeResultVO) imageResponse.getObj();
        if (!Constant.SUCCESS.equals(entity.getCode())) {
            return Response.FAIL(entity.getMsg());
        }
        redis.opsForValue().set(createCacheKeyCaptchaToken(robotWrapper.getId()), entity.getData().getCaptchaToken(), Duration.ofMinutes(5));
        return Response.SUCCESS(entity.getData().getImg());
    }

    // 创建图片验证码的缓存标志
    static String createCacheKeyCaptchaToken(long robotId) {
        return new StringBuilder(50)
                .append(RobotConsts.CAPTCHA_TOKEN)
                .append(TContext.getTenantId()).append(":")
                .append(TContext.getChannelId()).append(":")
                .append(TContext.getPlatformId()).append(":")
                .append(TContext.getFunction()).append(":")
                .append(robotId)
                .toString();
    }


    //获取图片验证码参数组装



}
