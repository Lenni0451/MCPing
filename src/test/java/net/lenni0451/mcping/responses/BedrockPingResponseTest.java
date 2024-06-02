package net.lenni0451.mcping.responses;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class BedrockPingResponseTest {

    @Test
    void testNull() {
        BedrockPingResponse response = new BedrockPingResponse();
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
        BedrockPingResponse response = new BedrockPingResponse();
        response.server = new BedrockPingResponse.Server();
        response.server.ip = "127.0.0.1";
        response.server.port = 19132;
        response.descriptionLine2 = "Hello World";
        response.server.ping = 50;
        response.players = new BedrockPingResponse.Players();
        response.players.online = 10;
        response.players.max = 20;
        response.version = new BedrockPingResponse.Version();
        response.version.name = "latest";
        response.version.protocol = 123;

        assertEquals("127.0.0.1", response.getAddress());
        assertEquals(19132, response.getPort());
        assertEquals("Hello World", response.getMotd());
        assertEquals(50, response.getPing());
        assertEquals(10, response.getOnlinePlayers());
        assertEquals(20, response.getMaxPlayers());
        assertEquals("latest", response.getVersionName());
        assertEquals(123, response.getProtocolId());
        assertFalse(response.getSample().isEmpty());
    }

    void testUnimplemented(final BedrockPingResponse response) {
        assertNull(response.getFavicon());
    }

}
