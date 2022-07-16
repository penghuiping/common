package com.php25.common.ws.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author penghuiping
 * @date 2019/12/21 17:19
 */
interface WsChannelOutput {
    String OUTPUT = "ws_channel_output";

    @Output(WsChannelOutput.OUTPUT)
    MessageChannel output();
}

interface WsChannelInput {
    String INPUT = "ws_channel_input";

    @Input(WsChannelInput.INPUT)
    SubscribableChannel input();
}

public interface WsChannelProcessor extends WsChannelInput, WsChannelOutput {
}


