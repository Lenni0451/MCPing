package net.lenni0451.mcping.responses;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class BedrockNethernetPingResponseTest {

    @Test
    void testNull() {
        BedrockNethernetPingResponse response = new BedrockNethernetPingResponse();
        assertEquals("Unknown", response.getAddress());
        assertEquals(-1, response.getPort());
        assertEquals("", response.getMotd());
        assertEquals(-1, response.getPing());
        assertEquals(-1, response.getOnlinePlayers());
        assertEquals(-1, response.getMaxPlayers());
        assertEquals("Unknown", response.getVersionName());
        assertEquals(-1, response.getProtocolId());
        assertEquals(Collections.emptyList(), response.getSample());
        this.testUnimplemented(response);
    }

    @Test
    void testNonNull() {
        BedrockNethernetPingResponse response = new BedrockNethernetPingResponse();
        response.server = new BedrockNethernetPingResponse.Server();
        response.server.ip = "127.0.0.1";
        response.server.port = 19132;
        response.name = "Hello World";
        response.server.ping = 50;
        response.players = 10;
        response.maxPlayers = 20;
        response.version = "1.26.50";
        response.protocol = 123;
        response.level = "Survival World";
        response.gameType = 1;

        assertEquals("127.0.0.1", response.getAddress());
        assertEquals(19132, response.getPort());
        assertEquals("Hello World", response.getMotd());
        assertEquals(50, response.getPing());
        assertEquals(10, response.getOnlinePlayers());
        assertEquals(20, response.getMaxPlayers());
        assertEquals("1.26.50", response.getVersionName());
        assertEquals(123, response.getProtocolId());
        assertFalse(response.getSample().isEmpty());
    }

    void testUnimplemented(final BedrockNethernetPingResponse response) {
        assertNull(response.getFavicon());
    }

}
