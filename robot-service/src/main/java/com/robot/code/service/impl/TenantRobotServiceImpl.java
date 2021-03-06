package com.robot.code.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbin.common.response.ResponseResult;
import com.bbin.utils.project.MyBeanUtil;
import com.robot.code.dto.LoginDTO;
import com.robot.code.entity.TenantChannel;
import com.robot.code.entity.TenantRobot;
import com.robot.code.mapper.TenantRobotMapper;
import com.robot.code.service.ITenantChannelService;
import com.robot.code.service.ITenantRobotService;
import com.robot.code.vo.TenantRobotVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 机器人表 服务实现类
 * </p>
 * @author admin
 * @since 2019-10-21
 */
@Slf4j
public abstract class TenantRobotServiceImpl extends ServiceImpl<TenantRobotMapper, TenantRobot> implements ITenantRobotService {
    @Autowired
    private ITenantChannelService channelService;

    @Transactional
    @Override
    public ResponseResult deleteRobot(long robotId) {
        // DB:强制下线
        forcedOfflineByDB(robotId);
        // 删除
        boolean isRemove = removeById(robotId);
        if (!isRemove) {
            throw new IllegalStateException("表:删除机器人失败");
        }
        // Cache:强制下线
        forcedOfflineByCache(robotId);
        return ResponseResult.SUCCESS();
    }

    @Transactional
    @Override
    public ResponseResult updateRobot(LoginDTO robotDTO) {
        // 是否重复添加
        if (isRobotRepetByUpdate(robotDTO.getId(),robotDTO.getPlatformAccount())) {
            return ResponseResult.FAIL("账号重复");
        }
        // DB：强制下线
        forcedOfflineByDB(robotDTO.getId());
        // 修改
        TenantRobot robot = MyBeanUtil.copyProperties(robotDTO, TenantRobot.class);
        boolean isUpdate = updateById(robot);
        if (!isUpdate) {
            throw new IllegalStateException("表:修改机器人失败");
        }
        // Cache:强制下线
        forcedOfflineByCache(robotDTO.getId());
        return ResponseResult.SUCCESS();
    }
    @Override
    public ResponseResult addRobot(LoginDTO robotDTO) {
        //是否重复添加
        if (isRobotRepetByAdd(robotDTO.getPlatformAccount())) {
            return ResponseResult.FAIL("账号已存在");
        }
        TenantRobot robot = MyBeanUtil.copyProperties(robotDTO, TenantRobot.class);
        return save(robot) ? ResponseResult.SUCCESS() : ResponseResult.FAIL();
    }

    @Override
    public ResponseResult pageRobot(LoginDTO robotDTO){
        IPage page = page(robotDTO, new LambdaQueryWrapper<TenantRobot>().orderByDesc(TenantRobot::getGmtCreateTime));
        Page<TenantRobotVO> voPage = MyBeanUtil.copyPageToPage(page, TenantRobotVO.class);
        for (TenantRobotVO robotVO : voPage.getRecords()) {
            TenantChannel channel = channelService.getById(robotVO.getChannelId());
            if (null != channel) {
                robotVO.setChannelName(channel.getChannelName());
            }
        }
        return ResponseResult.SUCCESS(voPage);
    }

    @Override
    public ResponseResult getRobotById(long robotId) {
        TenantRobot robot = getById(robotId);
        if (null == robot) {
            return ResponseResult.FAIL("检查id是否正确");
        }
        return ResponseResult.SUCCESS(robot);
    }

    @Transactional
    @Override
    public ResponseResult closeRobot(long robotId){
        // DB：强制下线
        forcedOfflineByDB(robotId);
        // CACHE：强制下线
        forcedOfflineByCache(robotId);
        return  ResponseResult.SUCCESS();
    }

    // 新增机器人：查看机器人是否重复添加
    private boolean isRobotRepetByAdd(String platformAccount) {
        return null != getOne(new LambdaQueryWrapper<TenantRobot>().eq(TenantRobot::getPlatformAccount, platformAccount));
    }

    // 更新机器人：查看机器人是否重复添加
    protected boolean isRobotRepetByUpdate(long robotId,String platformAccount){
        TenantRobot robot = getOne(new LambdaQueryWrapper<TenantRobot>().eq(TenantRobot::getPlatformAccount, platformAccount));
        return null != robot && robotId != robot.getId();
    }

    protected void forcedOfflineByDB(long robotId) {
        // 数据库：下线机器人
        boolean isUpdate = update(new LambdaUpdateWrapper<TenantRobot>()
                .eq(TenantRobot::getId, robotId)
                .set(TenantRobot::getIsOnline, false)
                .set(TenantRobot::getInfo, "已离线..."));
        if (!isUpdate) {
            throw new IllegalStateException("DB：强制下线机器人失败");
        }
    }

    public void onlineRobotByDB(long robotId) {
        // 数据库：下线机器人
        boolean isUpdate = update(new LambdaUpdateWrapper<TenantRobot>()
                .eq(TenantRobot::getId, robotId)
                .set(TenantRobot::getIsOnline, true)
                .set(TenantRobot::getInfo, "运行中..."));
        if (!isUpdate) {
            throw new IllegalStateException("DB：在线机器人失败");
        }
    }

    /**
     * 强制
     * @param robotId
     */
    protected abstract void forcedOfflineByCache(long robotId);
}
