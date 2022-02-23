package runner;

import protocolscraping.FilesDownloader;
import protocolscraping.XmlConversion;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AppRunner {

    public static void main(String[] args) throws IOException {
        /*
        * Download xml files and store in database in bson form
        * */

        //FilesDownloader filesDownloader = FilesDownloader.build();
        //filesDownloader.downloadAndSaveTheFilesContentInDbInBson();

        //Searching keywords
        List<String> searchList = Arrays.asList("Plenarprotokolle der 20. Wahlperiode",
                "Plenarprotokolle der 19. Wahlperiode");
        String url = "https://www.bundestag.de/services/opendata";

        XmlConversion conversion = new XmlConversion(url, searchList);
        conversion.init();
    }
}
