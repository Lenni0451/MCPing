package net.lenni0451.mcping.responses;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class QueryPingResponseTest {

    @Test
    void testNull() {
        QueryPingResponse response = new QueryPingResponse();
        assertEquals("Unknown", response.getAddress());
        assertEquals(-1, response.getPort());
        assertEquals("Unknown", response.getMotd());
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
        QueryPingResponse response = new QueryPingResponse();
        response.server = new QueryPingResponse.Server();
        response.server.ip = "127.0.0.1";
        response.server.port = 25565;
        response.description = "Hello World";
        response.server.ping = 50;
        response.players = new QueryPingResponse.Players();
        response.players.online = 10;
        response.players.max = 20;
        response.version = "latest";
        response.server.protocol = 123;
        response.players.sample = new String[]{"Player1"};

        assertEquals("127.0.0.1", response.getAddress());
        assertEquals(25565, response.getPort());
        assertEquals("Hello World", response.getMotd());
        assertEquals(50, response.getPing());
        assertEquals(10, response.getOnlinePlayers());
        assertEquals(20, response.getMaxPlayers());
        assertEquals("latest", response.getVersionName());
        assertEquals(123, response.getProtocolId());
        assertEquals(Collections.singletonList("Player1"), response.getSample());
    }

    void testUnimplemented(final QueryPingResponse response) {
        assertNull(response.getFavicon());
    }

}
