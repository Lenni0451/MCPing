package net.lenni0451.mcping.responses;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClassicPingResponseTest {

    @Test
    void testNull() {
        ClassicPingResponse response = new ClassicPingResponse();
        assertEquals("Unknown", response.getAddress());
        assertEquals(-1, response.getPort());
        assertEquals("Unknown", response.getMotd());
        assertEquals(-1, response.getPing());
        assertEquals(-1, response.getProtocolId());
        this.testUnimplemented(response);
    }

    @Test
    void testNonNull() {
        ClassicPingResponse response = new ClassicPingResponse();
        response.server = new ClassicPingResponse.Server();
        response.server.ip = "127.0.0.1";
        response.server.port = 25565;
        response.server.protocol = 47;
        response.server.ping = 50;
        response.motd = "Hello World!";

        assertEquals("127.0.0.1", response.getAddress());
        assertEquals(25565, response.getPort());
        assertEquals("Hello World!", response.getMotd());
        assertEquals(50, response.getPing());
        assertEquals(47, response.getProtocolId());
        this.testUnimplemented(response);
    }

    void testUnimplemented(final ClassicPingResponse response) {
        assertNull(response.getFavicon());
        assertEquals(-1, response.getOnlinePlayers());
        assertEquals(-1, response.getMaxPlayers());
        assertEquals("Unknown", response.getVersionName());
        assertEquals(Collections.emptyList(), response.getSample());
    }

}
