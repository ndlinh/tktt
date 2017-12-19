package org.caohoc23.tktt;

import org.apache.commons.cli.*;
import org.caohoc23.tktt.services.IndexBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import static java.lang.System.exit;

public class SpringBootConsoleApplication implements CommandLineRunner {

    @Autowired
    private IndexBuilder indexBuilder;

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
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("p")) {
                String path = cmd.getOptionValue("p");
                indexBuilder.run(path);
                System.out.println(cmd.getOptionValue("p"));

            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar target/tktt-1.0-SNAPSHOT.jar", options);
            }
        } catch (UnrecognizedOptionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


//        ProgressBar pb = new ProgressBar("Indexing", 100);
//        pb.start();
//        for (int i = 0; i < 100; i++) {
//            pb.step();
//            Thread.sleep(300);
//        }
//        pb.stop();

        exit(0);
    }

}
