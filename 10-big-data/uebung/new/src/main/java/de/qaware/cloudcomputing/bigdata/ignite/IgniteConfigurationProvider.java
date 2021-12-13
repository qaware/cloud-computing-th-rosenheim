package de.qaware.cloudcomputing.bigdata.ignite;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IgniteConfigurationProvider {

    private IgniteConfigurationProvider() {
    }

    public static IgniteConfiguration getIgniteConfiguration() {
        // Preparing IgniteConfiguration using Java APIs
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();

        // The node will be started as a client node.
        igniteConfiguration.setClientMode(true);

        // Classes of custom Java logic will be transferred over the wire from this app.
        igniteConfiguration.setPeerClassLoadingEnabled(true);

        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(List.of("localhost:47500..47509"));
        igniteConfiguration.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        // Setting up an IP Finder to ensure the client can locate the servers.
//        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
//        ipFinder.setAddresses(Arrays.asList("ignite-01:10800", "ignite-02:10801", "ignite-03:10802"));
//        igniteConfiguration.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        return igniteConfiguration;
    }

}
