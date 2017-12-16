package org.caohoc23.tktt;

import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.cli.*;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import static java.lang.System.exit;

public class SpringBootConsoleApplication implements CommandLineRunner {

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(SpringBootConsoleApplication.class);
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
                System.out.println("Hello World!!!!!!");
                System.out.println(cmd.getOptionValue("p"));
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar target/tktt-1.0-SNAPSHOT.jar", options);
            }
        } catch (UnrecognizedOptionException e) {

        }

        ProgressBar pb = new ProgressBar("Indexing", 100);
        pb.start();
        for (int i = 0; i < 100; i++) {
            pb.step();
            Thread.sleep(300);
        }
        pb.stop();

        exit(0);
    }

}
