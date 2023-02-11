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

    private String ip;
    private int port = -1;
    private boolean resolved;

    public ServerAddress(final String ip) {
        String[] split = ip.split(":");
        if (split.length != 1 && split.length != 2) throw new IllegalArgumentException("Invalid IP: " + ip);

        this.ip = split[0];
        if (split.length == 2) this.port = this.tryPort(split[1]);
    }

    public ServerAddress(final String ip, final int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort(final int defaultPort) {
        return this.hasPort() ? this.port : defaultPort;
    }

    public boolean hasPort() {
        return this.port != -1;
    }

    public InetSocketAddress toInetSocketAddress(final int defaultPort) {
        return new InetSocketAddress(this.ip, this.hasPort() ? this.port : defaultPort);
    }

    public ServerAddress resolve() {
        if (this.resolved) return this;
        if (this.port != -1) return this;
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
            throw new IllegalStateException("Failed to initialize SRV redirect resolved, some servers might not work", t);
        }
        try {
            Attributes attributes = dirContext.getAttributes("_minecraft._tcp." + this.ip, new String[]{"SRV"});
            Attribute attribute = attributes.get("srv");
            if (attribute != null) {
                String[] strings = attribute.get().toString().split(" ", 4);

                this.ip = strings[3];
                this.port = this.tryPort(strings[2]);
                this.resolved = true;
            }
        } catch (Throwable ignored) {
        }
        return this;
    }


    private int tryPort(final String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}
