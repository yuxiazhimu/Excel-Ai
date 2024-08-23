package com.yupi.springbootinit.bizMq;


import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.utils.AiManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;


@Component
@Slf4j
public class BiMessageConsumer {
    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME},ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        log.info("receiveMessage message = {}", message);
        if (StringUtils.isBlank(message)) {
            // 如果更新失败，拒绝当前消息，让消息重新进入队列
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            // 如果图表为空，拒绝消息并抛出业务异常
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图表为空");
        }



        Chart updateChart=new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus("running");
        boolean result2 = chartService.updateById(updateChart);
        ThrowUtils.throwIf(!result2, ErrorCode.PARAMS_ERROR,"更新图标状态失败");
        String strData = aiManager.doChart(buildUserInput(chart));
        String[] resultData = strData.split("【【【【【");
        ThrowUtils.throwIf(resultData.length>3, ErrorCode.OPERATION_ERROR);
        String resultCode = resultData[1].trim();
        String resultText = resultData[2].trim();

        Chart updateResultChart=new Chart();
        updateResultChart.setId(chart.getId());
        updateResultChart.setStatus("succeed");
        updateResultChart.setGenChart(resultCode);
        updateResultChart.setGenResult(resultText);
        boolean updateResult = chartService.updateById(updateResultChart);
        if (!updateResult) {
            // 如果更新图表成功状态失败，拒绝消息并处理图表更新错误
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新图表状态失败");
        }
        // 消息确认
        channel.basicAck(deliveryTag, false);
    }


    /***
     * 这个方法应该放到service层
     * @param chart
     * @return
     */
    private String buildUserInput(Chart chart) {
        // 获取图表的目标、类型和数据
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();

        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        userInput.append(csvData).append("\n");
        // 将StringBuilder转换为String并返回
        return userInput.toString();
    }
}
