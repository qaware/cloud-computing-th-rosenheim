package de.qaware.cloudcomputing.bigdata;

import de.qaware.cloudcomputing.bigdata.adapter.WordCountSplitAdapter;
import de.qaware.cloudcomputing.bigdata.ignite.IgniteConfigurationProvider;
import org.apache.commons.io.IOUtils;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class WordCount {

    private static final String FILENAME = "ulysses.txt";

    public static void main(String[] args) throws IgniteException {
        IgniteConfiguration igniteConfiguration = IgniteConfigurationProvider.getIgniteConfiguration();

        // Starting the node
        Ignite ignite = Ignition.start(igniteConfiguration);

        String book = readFile();

        WordCountSplitAdapter wordCountSplitAdapter = new WordCountSplitAdapter();

        Map<String, Integer> taskResult = ignite.compute().execute(wordCountSplitAdapter, book);
        Map<String, Integer> sortedTaskResult = sortMapByValue(taskResult);

        for (Map.Entry<String, Integer> entry : sortedTaskResult.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // Disconnect from the cluster.
        ignite.close();
    }

    private static String readFile() {
        try {
            URL url = WordCount.class.getClassLoader().getResource(FILENAME);
            return IOUtils.toString(url, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Integer> sortMapByValue(Map<String, Integer> map) {
        return map.entrySet().stream()
                .filter(word -> word.getKey().length() > 10)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(20)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
