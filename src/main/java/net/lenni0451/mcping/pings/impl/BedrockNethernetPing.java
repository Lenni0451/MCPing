package net.lenni0451.mcping.pings.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Setter;
import lombok.SneakyThrows;
import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.exception.ConnectTimeoutException;
import net.lenni0451.mcping.exception.ConnectionRefusedException;
import net.lenni0451.mcping.exception.DataReadException;
import net.lenni0451.mcping.exception.ReadTimeoutException;
import net.lenni0451.mcping.pings.APing;
import net.lenni0451.mcping.pings.IStatusListener;
import net.lenni0451.mcping.pings.PingReference;
import net.lenni0451.mcping.responses.BedrockNethernetPingResponse;

import javax.annotation.Nullable;
import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.function.Supplier;

/**
 * The ping implementation for the bedrock edition (using nethernet).<br>
 * Ping response: {@link BedrockNethernetPingResponse}
 */
public class BedrockNethernetPing extends APing {

    private static final SSLSocketFactory TRUST_ALL_SSL_FACTORY = new Supplier<SSLSocketFactory>() {
        @Override
        @SneakyThrows
        public SSLSocketFactory get() {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }}, new SecureRandom());
            return sslContext.getSocketFactory();
        }
    }.get();
    private static final HostnameVerifier TRUST_ALL_HOSTNAME_VERIFIER = (hostname, session) -> true;

    private final int connectTimeout;
    private final int readTimeout;
    private final Mode mode;
    @Setter
    @Nullable
    private SSLSocketFactory sslSocketFactory = TRUST_ALL_SSL_FACTORY;
    @Setter
    @Nullable
    private HostnameVerifier hostnameVerifier = TRUST_ALL_HOSTNAME_VERIFIER;
    private volatile HttpURLConnection currentConnection;

    public BedrockNethernetPing(final int connectTimeout, final int readTimeout) {
        this(connectTimeout, readTimeout, Mode.AUTO);
    }

    public BedrockNethernetPing(final int connectTimeout, final int readTimeout, final Mode mode) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.mode = mode;
    }

    @Override
    public int getDefaultPort() {
        return ServerAddress.DEFAULT_BEDROCK_PORT;
    }

    @Override
    public void ping(ServerAddress serverAddress, IStatusListener statusListener) {
        try {
            if (Mode.HTTPS.equals(this.mode)) {
                this.execute(serverAddress, "https", statusListener);
            } else if (Mode.HTTP.equals(this.mode)) {
                this.execute(serverAddress, "http", statusListener);
            } else {
                try {
                    this.execute(serverAddress, "https", statusListener);
                } catch (SSLException | EOFException | SocketException e) {
                    this.execute(serverAddress, "http", statusListener);
                }
            }
        } catch (ConnectException e) {
            statusListener.onError(new ConnectionRefusedException(serverAddress));
        } catch (SocketTimeoutException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("connect")) {
                statusListener.onError(new ConnectTimeoutException(this.connectTimeout));
            } else {
                statusListener.onError(new ReadTimeoutException(this.readTimeout));
            }
        } catch (Throwable t) {
            statusListener.onError(t);
        }
    }

    private void execute(final ServerAddress serverAddress, final String scheme, final IStatusListener statusListener) throws IOException {
        URL url;
        try {
            url = new URI(scheme, null, serverAddress.getHost(), serverAddress.getPort(), "/v1/join", null, null).toURL();
        } catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        this.currentConnection = connection;
        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) connection;
            if (this.sslSocketFactory != null) httpsConn.setSSLSocketFactory(this.sslSocketFactory);
            if (this.hostnameVerifier != null) httpsConn.setHostnameVerifier(this.hostnameVerifier);
        }
        connection.setRequestProperty("User-Agent", "libhttpclient/1.0.0.0");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setConnectTimeout(this.connectTimeout);
        connection.setReadTimeout(this.readTimeout);
        connection.setInstanceFollowRedirects(false);

        PingReference pingReference = new PingReference();
        pingReference.start();
        connection.connect();
        int responseCode = connection.getResponseCode();

        if (responseCode == 426 && "http".equals(scheme)) {
            this.execute(serverAddress, "https", statusListener);
            return;
        }
        if ((responseCode == 301 || responseCode == 302 || responseCode == 307 || responseCode == 308) && "http".equals(scheme)) {
            String location = connection.getHeaderField("Location");
            if (location != null && location.startsWith("https://")) {
                this.execute(serverAddress, "https", statusListener);
                return;
            }
        }
        if (responseCode != 200) {
            throw new DataReadException("Unexpected response code: " + responseCode);
        }

        statusListener.onConnected();

        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            char[] buffer = new char[1024];
            int read;
            while ((read = reader.read(buffer)) >= 0) {
                body.append(buffer, 0, read);
            }
        }
        pingReference.stop();

        JsonObject response;
        try {
            response = JsonParser.parseString(body.toString()).getAsJsonObject();
        } catch (Throwable t) {
            throw new DataReadException("Failed to parse response JSON: " + t.getMessage());
        }

        int protocol = response.has("protocol") && response.get("protocol").isJsonPrimitive() ? response.get("protocol").getAsInt() : 0;
        this.prepareResponse(serverAddress, response, protocol);
        response.getAsJsonObject("server").addProperty("ping", pingReference.get());

        BedrockNethernetPingResponse pingResponse = this.gson.fromJson(response, BedrockNethernetPingResponse.class);
        statusListener.onResponse(pingResponse);
        statusListener.onPing(pingResponse, pingReference.get());
    }

    @Override
    public void close() {
        HttpURLConnection connection = this.currentConnection;
        if (connection != null) {
            try {
                connection.disconnect();
            } catch (Throwable ignored) {
            }
        }
    }


    public enum Mode {
        AUTO,
        HTTP,
        HTTPS
    }

}
