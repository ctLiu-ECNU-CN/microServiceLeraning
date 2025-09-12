package com.heima.api.client.fallback;

import com.heima.api.client.ItemClient;
import com.heima.api.dto.ItemDTO;
import com.heima.api.dto.OrderDetailDTO;
import com.hmall.common.utils.CollUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;
import java.util.List;
@Slf4j
public class ItemClientFallbackFactory implements FallbackFactory<ItemClient> {

    @Override
    public ItemClient create(Throwable cause) {
        return new ItemClient() {

            @Override
            /*
            * 查询商品业务
            * */
            public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
                log.error("查询商品失败", cause);
                return CollUtils.emptyList();
            }

            /*
            * 减库存业务
            * */
            @Override
            public void deductStock(List<OrderDetailDTO> items) {
                log.error("扣减库存失败", cause);
                throw new RuntimeException(cause);
            }
        };
    }
}
