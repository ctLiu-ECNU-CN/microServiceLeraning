package com.heima.api.client;


import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

@FeignClient("cart-service")
public interface CartClient {

    /**
     * 删除购物车中的物品
     * @param ids 购物车中的物品ID
     */
    @DeleteMapping("/carts")
    void deleteCartItemsByIds(@RequestParam("ids") Collection<Long> ids);
}
