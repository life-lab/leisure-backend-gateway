package com.github.hicolors.leisure.backend.gateway.application.client;

import com.github.hicolors.leisure.member.model.persistence.Member;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * UserService
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/11
 */

@Api(tags = "member", description = "对人员的相关操作")
@FeignClient(name = "leisure-member", url = "${url.leisure-member}")
@RequestMapping("member")
public interface UserClient {

    @ApiOperation("人员 - 获取人员信息通过 id")
    @GetMapping("/{id}")
    Member queryOneById(@PathVariable("id") Long id);

    @ApiOperation("人员 - 获取人员信息通过 mobile")
    @GetMapping("/mobile")
    Member queryOneByMobile(@RequestParam("mobile") String mobile);

    @ApiOperation("人员 - 获取人员信息通过 email")
    @GetMapping("/email")
    Member queryOneByEmail(@RequestParam("email") String email);

    @ApiOperation("人员 - 获取人员信息通过 uniquekey（包含用户名，手机号，邮箱） 和 password")
    @GetMapping("/unique-key/password")
    Member queryOneByUniqueKeyAndPassword(@RequestParam("uniquekey") String uniquekey, @RequestParam("password") String password);

}