package net.lenni0451.mcping;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Hashtable;
import java.util.regex.Pattern;

public class ServerAddress {

    private static final Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
    private static final Pattern IPV6_PATTERN = Pattern.compile("^(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))$");

    /**
     * Get a server address from the given host, port and default port.
     *
     * @param host        The server host
     * @param port        The server port
     * @param defaultPort The default port of the ping protocol
     * @return The server address
     */
    public static ServerAddress of(final String host, final int port, final int defaultPort) {
        return new ServerAddress(host, port, defaultPort);
    }

    /**
     * Wrap a {@link SocketAddress} into a server address.<br>
     * If the socket address is an {@link InetSocketAddress} the host and port will be resolved.
     *
     * @param socketAddress The socket address to wrap
     * @param defaultPort   The default port of the ping protocol
     * @return The server address
     */
    public static ServerAddress wrap(final SocketAddress socketAddress, final int defaultPort) {
        if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
            return of(inetSocketAddress.getHostString(), inetSocketAddress.getPort(), defaultPort);
        } else {
            return new ServerAddress(socketAddress, defaultPort);
        }
    }

    /**
     * Parse a server address from the given string.<br>
     * The string must be in the format of {@code host:port} or just {@code host}.
     *
     * @param address     The string to parse
     * @param defaultPort The default port of the ping protocol
     * @return The server address
     * @throws IllegalArgumentException If the string is not in the correct format
     */
    public static ServerAddress parse(final String address, final int defaultPort) {
        String[] split = address.split(":");
        if (split.length != 1 && split.length != 2) throw new IllegalArgumentException("Invalid host and/or port: " + address);

        String host = split[0];
        int port = defaultPort;
        if (split.length == 2) port = tryPort(split[1], defaultPort);
        return of(host, port, defaultPort);
    }

    private static int tryPort(final String s, final int defaultPort) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultPort;
        }
    }


    private final String unresolvedHost;
    private final int unresolvedPort;
    private String host;
    private int port;
    private final int defaultPort;
    private boolean resolved;
    private SocketAddress socketAddress;

    private ServerAddress(final String host, final int port, final int defaultPort) {
        this.unresolvedHost = host;
        this.unresolvedPort = port;
        this.host = host;
        this.port = port;
        this.defaultPort = defaultPort;
    }

    private ServerAddress(final SocketAddress socketAddress, final int defaultPort) {
        this.socketAddress = socketAddress;
        this.unresolvedHost = this.host = socketAddress.toString();
        this.unresolvedPort = this.port = 0;
        this.defaultPort = defaultPort;
        this.resolved = true;
    }

    /**
     * @return The unresolved host of the server
     */
    public String getUnresolvedHost() {
        return this.unresolvedHost;
    }

    /**
     * @return The unresolved port of the server
     */
    public int getUnresolvedPort() {
        return this.unresolvedPort;
    }

    /**
     * @return The host of the server
     */
    public String getHost() {
        return this.host;
    }

    /**
     * @return The port of the server
     */
    public int getPort() {
        return this.port;
    }

    /**
     * @return The default port of the ping protocol
     */
    public int getDefaultPort() {
        return this.defaultPort;
    }

    /**
     * @return Whether the server address is resolved
     */
    public boolean isResolved() {
        return this.resolved;
    }

    /**
     * Get a {@link SocketAddress} from this server address.<br>
     * Returns the wrapped socket address if it is not null.
     *
     * @return The SocketAddress
     */
    public SocketAddress getSocketAddress() {
        if (this.socketAddress != null) return this.socketAddress;
        else return new InetSocketAddress(this.host, this.port);
    }

    /**
     * Resolve the server address using the {@code _minecraft._tcp.} SRV record.
     *
     * @throws IllegalStateException If the dns context could not be initialized
     */
    public void resolve() {
        if (this.resolved) return;
        if (this.port != this.defaultPort) return;
        if (IPV4_PATTERN.matcher(this.host).matches()) return;
        if (IPV6_PATTERN.matcher(this.host).matches()) return;

        DirContext dirContext;
        try {
            Class.forName("com.sun.jndi.dns.DnsContextFactory");
            Hashtable<String, String> hashtable = new Hashtable<>();
            hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            hashtable.put("java.naming.provider.url", "dns:");
            hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
            dirContext = new InitialDirContext(hashtable);
        } catch (Throwable t) {
            throw new IllegalStateException("Failed to initialize dns context", t);
        }
        try {
            Attributes attributes = dirContext.getAttributes("_minecraft._tcp." + this.host, new String[]{"SRV"});
            Attribute attribute = attributes.get("srv");
            if (attribute != null) {
                String[] parts = attribute.get().toString().split(" ", 4);

                this.host = parts[3];
                this.port = tryPort(parts[2], this.defaultPort);
                this.resolved = true;
            }
        } catch (Throwable ignored) {
        }
    }

}
