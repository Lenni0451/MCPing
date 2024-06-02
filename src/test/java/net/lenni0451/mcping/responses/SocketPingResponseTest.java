package net.lenni0451.mcping.responses;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SocketPingResponseTest {

    @Test
    void testNull() {
        SocketPingResponse response = new SocketPingResponse();
        assertEquals("Unknown", response.getAddress());
        assertEquals(-1, response.getPort());
        assertEquals(0, response.getPing());
        this.testUnimplemented(response);
    }

    @Test
    void testNonNull() {
        SocketPingResponse response = new SocketPingResponse();
        response.server = new SocketPingResponse.Server();
        response.server.ip = "127.0.0.1";
        response.server.port = 25565;
        response.latency = 50;

        assertEquals("127.0.0.1", response.getAddress());
        assertEquals(25565, response.getPort());
        assertEquals(50, response.getPing());
        this.testUnimplemented(response);
    }

    void testUnimplemented(final SocketPingResponse response) {
        assertEquals("Unknown", response.getMotd());
        assertNull(response.getFavicon());
        assertEquals(-1, response.getOnlinePlayers());
        assertEquals(-1, response.getMaxPlayers());
        assertEquals("Unknown", response.getVersionName());
        assertEquals(-1, response.getProtocolId());
        assertEquals(Collections.emptyList(), response.getSample());
    }

}
