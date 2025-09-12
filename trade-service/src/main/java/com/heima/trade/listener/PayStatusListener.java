package com.heima.trade.listener;

import com.heima.trade.domain.po.Order;
import com.heima.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PayStatusListener {

    private final IOrderService orderService;

    /**
     * 消息接受
     * 标记顶端状态已支付
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "trade.pay.success.queue", durable = "true"),
                    exchange = @Exchange(name = "pay.direct"),
                    key = "pay.success"
            )
    )
    public void listenPaySuccess(Long orderId) {
        log.info("监听到" + orderId);
        //幂等性判断
        //防止重发的消息覆盖掉更新的状态

        //1.查询订单
        Order order = orderService.getById(orderId);
        //判断订单状态是否已经支付
        if(order == null || order.getStatus() != 1){
            // 是过期的消息
            // 无需处理
            return;
        }

        //标记订单状态已支付
        orderService.markOrderPaySuccess(orderId);
    }
}
