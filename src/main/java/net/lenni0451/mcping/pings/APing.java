package net.lenni0451.mcping.pings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.lenni0451.mcping.ServerAddress;

/**
 * The abstract class used to implement a ping.
 */
public abstract class APing {

    protected final Gson gson = new GsonBuilder().create();

    /**
     * @return The default port of this ping protocol
     */
    public abstract int getDefaultPort();

    /**
     * Ping the server and call the status listener.
     *
     * @param serverAddress  The server address
     * @param statusListener The status listener
     */
    public abstract void ping(final ServerAddress serverAddress, final IStatusListener statusListener);


    /**
     * Prepare the response by adding default server information like the host, port, protocol versions and the description.<br>
     * The description is converted to a string if it is a json object. You can use my library <a href="https://github.com/Lenni0451/MCStructs">MCStructs</a> to parse the description component if required.
     *
     * @param serverAddress   The server address
     * @param response        The response
     * @param protocolVersion The protocol version
     */
    protected final void prepareResponse(final ServerAddress serverAddress, final JsonObject response, final int protocolVersion) {
        JsonObject server = new JsonObject();
        server.addProperty("ip", serverAddress.getHost());
        server.addProperty("port", serverAddress.getPort());
        server.addProperty("protocol", protocolVersion);
        response.add("server", server);

        if (response.has("description") && response.get("description").isJsonObject()) {
            JsonObject description = response.get("description").getAsJsonObject();
            response.addProperty("description", this.gson.toJson(description));
        }
    }

}
