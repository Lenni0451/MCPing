package net.lenni0451.mcping.responses;

import lombok.ToString;
import net.lenni0451.mcping.pings.impl.BedrockPing;
import org.jetbrains.annotations.ApiStatus;

/**
 * The response of a {@link BedrockPing}.
 *
 * @deprecated Use {@link BedrockRaknetPingResponse} instead
 */
@Deprecated
@ToString(callSuper = true)
@ApiStatus.ScheduledForRemoval
public class BedrockPingResponse extends BedrockRaknetPingResponse {
}
