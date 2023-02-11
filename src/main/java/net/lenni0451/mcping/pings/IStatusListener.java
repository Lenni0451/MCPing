package net.lenni0451.mcping.pings;

import net.lenni0451.mcping.responses.IPingResponse;

public interface IStatusListener {

    void onError(final Throwable throwable);

    void onResponse(final IPingResponse pingResponse);

    void onPing(final IPingResponse pingResponse, final long ping);

}