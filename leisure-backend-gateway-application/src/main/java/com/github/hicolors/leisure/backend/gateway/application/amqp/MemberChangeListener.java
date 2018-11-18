package com.github.hicolors.leisure.backend.gateway.application.amqp;

import com.github.hicolors.leisure.backend.gateway.application.service.SignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MemberListener
 *
 * @author weichao.li (liweichao0102@gmail.com)
 * @date 2018/11/18
 */
@Component
@Slf4j
public class MemberChangeListener {

    @Autowired
    private SignService signService;

    @RabbitListener(queues = "rabbit_queue_leisure_member_member")
    public void consumeMessage(Long memberId) {
        log.info("member [{}] info change!", memberId);
        signService.memberListener(memberId);
    }
}