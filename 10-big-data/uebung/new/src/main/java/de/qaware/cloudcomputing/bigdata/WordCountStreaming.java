package de.qaware.cloudcomputing.bigdata;

import de.qaware.cloudcomputing.bigdata.ignite.IgniteConfigurationProvider;
import de.qaware.cloudcomputing.bigdata.util.FileUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.affinity.AffinityUuid;
import org.apache.ignite.configuration.IgniteConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class WordCountStreaming {

    static {
        System.getProperties().setProperty("java.net.preferIPv4Stack", "true");
    }

    private static final String FILENAME = "ulysses.txt";

    public static void main(String[] args) throws IOException {
        IgniteConfiguration igniteConfiguration = IgniteConfigurationProvider.getIgniteConfiguration();

        // Starting the node
        Ignite ignite = Ignition.start(igniteConfiguration);

        IgniteCache<AffinityUuid, String> streamCache = ignite.getOrCreateCache(IgniteConfigurationProvider.getCacheConfiguration());

        try (IgniteDataStreamer<AffinityUuid, String> streamer = ignite.dataStreamer(streamCache.getName())) {
            while (true) {
                InputStream in = FileUtil.readFileFromResourcesAsStream(FILENAME);

                try (LineNumberReader rdr = new LineNumberReader(new InputStreamReader(in))) {
                    for (String line = rdr.readLine(); line != null; line = rdr.readLine()) {
                        for (String word : line.split(" "))
                            if (!word.isEmpty())
                                // Stream words into Ignite.
                                // By using AffinityUuid we ensure that identical
                                // words are processed on the same cluster node.
                                streamer.addData(new AffinityUuid(word), word);
                    }
                }
            }
        }




    }

}
