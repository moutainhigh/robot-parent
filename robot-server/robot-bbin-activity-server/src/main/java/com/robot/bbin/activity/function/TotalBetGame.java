package com.robot.bbin.activity.function;

import com.bbin.common.response.ResponseResult;
import com.robot.bbin.activity.dto.TotalBetGameDTO;
import com.robot.bbin.activity.vo.TotalBetGameVO;
import com.robot.center.execute.IActionEnum;
import com.robot.center.execute.IResultParse;
import com.robot.center.function.FunctionBase;
import com.robot.center.function.ParamWrapper;
import com.robot.center.httpclient.CustomHttpMethod;
import com.robot.center.httpclient.StanderHttpResponse;
import com.robot.center.httpclient.UrlCustomEntity;
import com.robot.center.pool.RobotWrapper;
import com.robot.code.entity.TenantRobotAction;
import com.robot.bbin.activity.basic.ActionEnum;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrt on 11/15/2019 12:29 PM
 * 查询投注总金额(游戏)
 */
@Service
public class TotalBetGame extends FunctionBase<TotalBetGameDTO> {

    @Override
    protected ResponseResult doFunctionFinal(ParamWrapper<TotalBetGameDTO> paramWrapper, RobotWrapper robotWrapper, TenantRobotAction action) throws Exception {
        TotalBetGameDTO gameDTO = paramWrapper.getObj();
        action.setActionUrl(action.getActionUrl() + gameDTO.getGameKind());
        // 执行
        StanderHttpResponse response = execute.request(robotWrapper, CustomHttpMethod.GET, action, null,
                createBodyParams(gameDTO), null, ResultParse.INSTANCE);
        ResponseResult responseResult = response.getResponseResult();
        if (!responseResult.isSuccess()) {
            return responseResult;
        }

        List<TotalBetGameVO> list = (List<TotalBetGameVO>) responseResult.getObj();
        if (CollectionUtils.isEmpty(list)) {
            return ResponseResult.FAIL("未查询到投注总金额:gameKind:"+gameDTO.getGameKind()+"dateStart:" + gameDTO.getDateStart() + " dateEnd:" + gameDTO.getDateEnd());
        }
        return responseResult;
    }

    @Override
    public IActionEnum getActionEnum() {
        return ActionEnum.TOTAL_BET_BY_GAME;
    }

    //组装局查询
    private UrlCustomEntity createBodyParams(TotalBetGameDTO gameDTO) throws Exception{
        return UrlCustomEntity.custom()
                .add("SearchData", "MemberBets")
                .add("BarID", gameDTO.getBarId())
                .add("GameKind", gameDTO.getGameKind())
                .add("date_start", gameDTO.getDateStart())
                .add("date_end", gameDTO.getDateEnd())
                .add("GameType", "-1") // -1表示全部
                .add("Limit", "100")
                .add("Sort", "DESC")
                .add("UserID", gameDTO.getUserID());
    }

    /**
     * 响应结果转换类
     */
    private static final class ResultParse implements IResultParse {
        private static final ResultParse INSTANCE = new ResultParse();
        private ResultParse(){}

        @Override
        public ResponseResult parse(String result) {
            Document document = Jsoup.parse(result);
            Elements table = document.select("table[class=table table-hover text-middle table-bordered]");
            Elements trs = table.get(0).select("tbody tr");
            List<TotalBetGameVO> list = new ArrayList<TotalBetGameVO>();
            for (Element tr : trs) {
                Elements tds = tr.select("td");
                TotalBetGameVO gameVO = new TotalBetGameVO(
                        tds.get(0).text(),
                        Integer.parseInt(tds.get(1).text().replaceAll(",","")),
                        new BigDecimal(tds.get(2).text().replaceAll(",","")),
                        new BigDecimal(tds.get(3).text().replaceAll(",","")));
                list.add(gameVO);
            }
            return ResponseResult.SUCCESS(list);
        }
    }
}
