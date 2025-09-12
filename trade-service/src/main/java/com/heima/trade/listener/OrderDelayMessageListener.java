package com.heima.trade.listener;


import com.heima.api.client.PayClient;
import com.heima.api.dto.PayOrderDTO;
import com.heima.trade.constants.MQConstants;
import com.heima.trade.domain.po.Order;
import com.heima.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.jcajce.provider.digest.Skein;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDelayMessageListener {

    private final IOrderService orderService;
    private final PayClient payClient;
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(),
                    exchange = @Exchange(name = MQConstants.DELAY_EXCHANGE_NAME,delayed = "true"),
                    key = MQConstants.DELAY_ORDER_KEY
            )
    )
    public void listenOrderDelayMessage(Long orderId){

//        1.查询订单
        Order order = orderService.getById(orderId);
//        2.检测订单状态(判断是否已支付)
        if(order == null || order.getStatus() != 1){
            return;
        }
//        3. 如果是未支付,需要查询支付流水状态
        PayOrderDTO payOrderDTO = payClient.queryPayOrderByBizOrderNo(orderId);

//        4判断是否支付
        if(payOrderDTO!= null && payOrderDTO.getStatus() == 3){
//        4.1已支付，标记订单已经支付
            orderService.markOrderPaySuccess(orderId);
        }else{
//        4.2未支付,回复库存,取消订单
            orderService.cancelOrder(orderId);
        }

    }
}
