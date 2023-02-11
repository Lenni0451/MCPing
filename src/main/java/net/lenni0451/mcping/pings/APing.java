package net.lenni0451.mcping.pings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.lenni0451.mcping.ServerAddress;

public abstract class APing {

    protected final Gson gson = new GsonBuilder().create();

    public abstract int getDefaultPort();

    public abstract void ping(final ServerAddress serverAddress, final IStatusListener statusListener);


    protected final void prepareResponse(final ServerAddress serverAddress, final JsonObject response, final int protocolVersion) {
        JsonObject server = new JsonObject();
        server.addProperty("ip", serverAddress.getIp());
        server.addProperty("port", serverAddress.getPort());
        server.addProperty("protocol", protocolVersion);
        response.add("server", server);

        if (response.has("description") && response.get("description").isJsonObject()) {
            JsonObject description = response.get("description").getAsJsonObject();
            response.addProperty("description", this.gson.toJson(description));
        }
    }

}
