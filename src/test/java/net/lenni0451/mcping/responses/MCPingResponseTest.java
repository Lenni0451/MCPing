package net.lenni0451.mcping.responses;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MCPingResponseTest {

    @Test
    void testNull() {
        MCPingResponse response = new MCPingResponse();
        assertEquals("Unknown", response.getAddress());
        assertEquals(-1, response.getPort());
        assertEquals("Unknown", response.getMotd());
        assertNull(response.getFavicon());
        assertEquals(-1, response.getPing());
        assertEquals(-1, response.getOnlinePlayers());
        assertEquals(-1, response.getMaxPlayers());
        assertEquals("Unknown", response.getVersionName());
        assertEquals(-1, response.getProtocolId());
        assertEquals(Collections.emptyList(), response.getSample());
    }

    @Test
    void testNonNull() {
        MCPingResponse response = new MCPingResponse();
        response.server = new MCPingResponse.Server();
        response.server.ip = "127.0.0.1";
        response.server.port = 25565;
        response.description = "Hello World";
        response.favicon = "data:image/png;base64,...";
        response.server.ping = 50;
        response.players = new MCPingResponse.Players();
        response.players.online = 10;
        response.players.max = 20;
        response.version = new MCPingResponse.Version();
        response.version.name = "latest";
        response.version.protocol = 123;
        response.players.sample = new MCPingResponse.Players.Player[]{new MCPingResponse.Players.Player()};
        response.players.sample[0].name = "Player1";

        assertEquals("127.0.0.1", response.getAddress());
        assertEquals(25565, response.getPort());
        assertEquals("Hello World", response.getMotd());
        assertEquals("data:image/png;base64,...", response.getFavicon());
        assertEquals(50, response.getPing());
        assertEquals(10, response.getOnlinePlayers());
        assertEquals(20, response.getMaxPlayers());
        assertEquals("latest", response.getVersionName());
        assertEquals(123, response.getProtocolId());
        assertEquals(Collections.singletonList("Player1"), response.getSample());
    }

}
