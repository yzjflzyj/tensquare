package com.tensquare.friend.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(value = "tensquare-user")
public interface UserClient {

    @PutMapping("/user/updateFans/{userid}/{x}")
    void updateFans(@PathVariable("userid") String userid, @PathVariable("x") int x);

    @PutMapping("/user/updateFollow/{userid}/{x}")
    void updateFollow(@PathVariable("userid") String userid, @PathVariable("x") int x);
}
