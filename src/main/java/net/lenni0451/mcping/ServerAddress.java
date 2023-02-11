package net.lenni0451.mcping;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.util.regex.Pattern;

public class ServerAddress {

    private static final Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
    private static final Pattern IPV6_PATTERN = Pattern.compile("^(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))$");

    public static ServerAddress of(final String ip, final int port, final int defaultPort) {
        return new ServerAddress(ip, port, defaultPort);
    }

    public static ServerAddress parse(final String address, final int defaultPort) {
        String[] split = address.split(":");
        if (split.length != 1 && split.length != 2) throw new IllegalArgumentException("Invalid IP: " + address);

        String ip = split[0];
        int port = defaultPort;
        if (split.length == 2) port = tryPort(split[1], defaultPort);
        return of(ip, port, defaultPort);
    }

    private static int tryPort(final String s, final int defaultPort) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultPort;
        }
    }


    private String ip;
    private int port;
    private final int defaultPort;
    private boolean resolved;

    private ServerAddress(final String ip, final int port, final int defaultPort) {
        this.ip = ip;
        this.port = port;
        this.defaultPort = defaultPort;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public int getDefaultPort() {
        return this.defaultPort;
    }

    public boolean isResolved() {
        return this.resolved;
    }

    public InetSocketAddress toInetSocketAddress() {
        return new InetSocketAddress(this.ip, this.port);
    }

    public ServerAddress resolve() {
        if (this.resolved) return this;
        if (this.port != this.defaultPort) return this;
        if (IPV4_PATTERN.matcher(this.ip).matches()) return this;
        if (IPV6_PATTERN.matcher(this.ip).matches()) return this;

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
            Attributes attributes = dirContext.getAttributes("_minecraft._tcp." + this.ip, new String[]{"SRV"});
            Attribute attribute = attributes.get("srv");
            if (attribute != null) {
                String[] parts = attribute.get().toString().split(" ", 4);

                this.ip = parts[3];
                this.port = tryPort(parts[2], this.defaultPort);
                this.resolved = true;
            }
        } catch (Throwable ignored) {
        }
        return this;
    }

}
