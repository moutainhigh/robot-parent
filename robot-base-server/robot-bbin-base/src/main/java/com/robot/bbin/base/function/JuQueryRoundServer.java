package com.robot.bbin.base.function;

import com.bbin.common.dto.order.OrderNoQueryDTO;
import com.bbin.common.response.ResponseResult;
import com.bbin.utils.project.DateUtils;
import com.robot.bbin.base.basic.PathEnum;
import com.robot.bbin.base.dto.JuQueryDetailDTO;
import com.robot.bbin.base.vo.JuQueryVO;
import com.robot.center.util.MoneyUtil;
import com.robot.code.dto.Response;
import com.robot.code.service.ITenantRobotDictService;
import com.robot.core.function.base.AbstractFunction;
import com.robot.core.function.base.IPathEnum;
import com.robot.core.function.base.IResultHandler;
import com.robot.core.http.request.ICustomEntity;
import com.robot.core.http.request.UrlEntity;
import com.robot.core.http.response.HtmlResponseHandler;
import com.robot.core.http.response.JsonResponseHandler;
import com.robot.core.http.response.StanderHttpResponse;
import com.robot.core.robot.manager.RobotWrapper;
import org.apache.http.client.ResponseHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Created by mrt on 11/15/2019 12:29 PM
 * 局查询：场次查询
 * 现在以下电子使用本类：
 *  申博电子（按场次查询，场次即是申博电子的注单）
 */
@Service
public class JuQueryRoundServer extends AbstractFunction<JuQueryDetailDTO,String,JuQueryVO> {
    @Autowired
    private ITenantRobotDictService dictService;
    // 字典表：获取注单查询的barID的前缀
    private String DICT_BAR_ID = "BBIN:ROUND_QUERY:";

    @Override
    protected IPathEnum getPathEnum() {
        return PathEnum.JU_QUERY;
    }

    @Override
    protected ICustomEntity getEntity(JuQueryDetailDTO queryDTO, RobotWrapper robotWrapper) {
        return UrlEntity.custom(6)
                .add("SearchData", "BetQuery")
                .add("BarID", queryDTO.getBarId())
                .add("GameKind", queryDTO.getGameCode()) // 平台编码
                .add("RoundNo", queryDTO.getOrderNo()) // 注单号
                .add("Limit", "50") // 每页大小
                .add("Sort", "DESC"); // 时间倒排
    }

    @Override
    protected IResultHandler<String, JuQueryVO> getResultHandler() {
        return null;
    }

    @Override
    protected ResponseHandler<StanderHttpResponse> getResponseHandler(){
        return HtmlResponseHandler.HTML_RESPONSE_HANDLER;
    }

    /**
     * 响应结果转换类
     */
    private static final class JuQueryParse implements IResultHandler<String, JuQueryVO> {
        private static final JuQueryParse INSTANCE = new JuQueryParse();
        private JuQueryParse() {}

        @Override
        public Response parse2Obj(StanderHttpResponse<String, JuQueryVO> shr) {
            String result = shr.getOriginalEntity();
            Document doc = Jsoup.parse(result);
            // tbody为空表示没有
            Elements tds = doc.select("table[class=table table-hover text-middle table-bordered] tbody tr td");
            if (CollectionUtils.isEmpty(tds)) {
                return Response.FAIL("记录不存在");
            }

            // 获取显示值table table-hover text-middle table-bordered
            JuQueryVO juQueryVO = new JuQueryVO();
            juQueryVO.setOrderTime(DateUtils.format(tds.get(0).text()));
            juQueryVO.setPlatFormOrderNo(tds.get(1).text());
            juQueryVO.setGameName(tds.get(2).text());
            juQueryVO.setHall(tds.get(3).text());
            juQueryVO.setUserName(tds.get(4).text());
            juQueryVO.setResult(tds.get(5).text());
            juQueryVO.setRebateAmount(MoneyUtil.formatYuan(tds.get(6).text()));
            juQueryVO.setSendAmount(MoneyUtil.formatYuan(tds.get(7).text()));
            juQueryVO.setRound(tds.get(8).text());
            return Response.SUCCESS(juQueryVO);
        }
    }

}
