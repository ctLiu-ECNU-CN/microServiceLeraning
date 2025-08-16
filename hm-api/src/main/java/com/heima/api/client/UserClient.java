package com.heima.api.client;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserClient {

    @PutMapping("/users/money/deduct")
    void deductMoney(@RequestParam("pw") String pw, @RequestParam("amount") Integer amount);
}
