package org.caohoc23.tktt;

import org.apache.commons.cli.*;
import org.caohoc23.tktt.services.IndexBuilder;
import org.caohoc23.tktt.services.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.exit;

public class SpringBootConsoleApplication implements CommandLineRunner {

    @Autowired
    private IndexBuilder indexBuilder;

    @Autowired
    private Search search;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(AppConfig.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {

        try {
            Options options = new Options();
            options.addOption("p", true, "Document Path");
            options.addOption("s", true, "Search keyword");
            options.addOption("k", true, "TOP K");
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("p")) {
                String path = cmd.getOptionValue("p");
                indexBuilder.run(path);
                System.out.println(cmd.getOptionValue("p"));
            } else if (cmd.hasOption("s")) {
                String keyword = cmd.getOptionValue("s");
                int k = 10;
                if (cmd.hasOption("k")) {
                    String topK = cmd.getOptionValue("k");
                    k = Integer.valueOf(topK);
                }

                Map<String, Double> keywordW = search.search(keyword);
                int count = 0;
                System.out.println("+----+---------------+---------------+");
                System.out.println("| No |  Document     |   Score       |");
                System.out.println("+----+---------------+---------------+");
                for (Map.Entry entry: keywordW.entrySet()) {
                    System.out.println(String.format("| %2d | %-14s|%14f |", count + 1, entry.getKey(), entry.getValue()));
                    System.out.println("+----+---------------+---------------+");
                    count++;
                    if (count == k) {
                        break;
                    }
                }

            } else {

                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar target/tktt-1.0-SNAPSHOT.jar", options);
            }
        } catch (UnrecognizedOptionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        exit(0);
    }

    private String getFileString(String file) throws IOException {
        FileReader f = new FileReader(file);
        BufferedReader reader = new BufferedReader(f);

        return reader.readLine();
    }

}
