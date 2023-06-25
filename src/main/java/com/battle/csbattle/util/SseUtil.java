package com.battle.csbattle.util;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

public class SseUtil {
    public static boolean sendToClient(SseEmitter emitter, String eventName, Object data) {    // TODO: 추후 emitter 의 id 도 지정해야 할 경우라면 => id 인자 추가하기
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
            return true;
        } catch (Exception e) {
            System.out.println("!!! Exception while sendToClient: " + e);
            return false;
        }
    }
}
