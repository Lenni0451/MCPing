package net.lenni0451.mcping.pings.impl;

import net.lenni0451.mcping.pings.sockets.factories.IUDPSocketFactory;
import net.lenni0451.mcping.responses.BedrockPingResponse;
import net.lenni0451.mcping.responses.BedrockRaknetPingResponse;
import org.jetbrains.annotations.ApiStatus;

/**
 * The ping implementation for the bedrock edition.<br>
 * Ping response: {@link BedrockPingResponse}
 *
 * @deprecated Use {@link BedrockRaknetPing} instead
 */
@Deprecated
@ApiStatus.ScheduledForRemoval
public class BedrockPing extends BedrockRaknetPing {

    public BedrockPing(final IUDPSocketFactory socketFactory, final int readTimeout) {
        super(socketFactory, readTimeout);
    }

    @Override
    protected Class<? extends BedrockRaknetPingResponse> getResponseClass() {
        return BedrockPingResponse.class;
    }

}
