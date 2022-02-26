package protocolscraping;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.neo4j.string.UTF8;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class PictureScrap {

		String parentUrl = "https://bilddatenbank.bundestag.de";
		URIBuilder ub;
		//String speakerName;


		public URL run(String speakerName) {

			try {

				ub = new URIBuilder(parentUrl + "/search/picture-result");
				ub.addParameter("query", speakerName);
				String url = ub.toString();

				Scarper scarper = new Scarper(url);
				String img = scarper.init();

				URL speakerIMG = scarper.getImageByName(img);
				return speakerIMG;

			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			return null;
		}
}

class Scarper {

	private String url;

	public Scarper(String url) {
		this.url = url;
	}

	public String init() {
		String imageURL = null;
		Document pageSource = getPageSource(url);

		Elements documents = pageSource.getElementsByClass("item");

		if (!documents.isEmpty()) {
			Element element = documents.get(1).getElementsByTag("img").get(0);

			imageURL = element.attr("src");
			System.out.println(imageURL);
		}

		return imageURL;

	}

	private Document getPageSource(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0").get();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	public URL getImageByName(String name) throws URISyntaxException {

		String parentUrl = "https://bilddatenbank.bundestag.de";
		byte[] fileContent = null;
		try {
			//fileContent = IOUtils.toByteArray(new URL(parentUrl + name));
			URL uriella = new URL(parentUrl + name);
			fileContent = IOUtils.toByteArray(uriella);
			ByteArrayInputStream inStreambj = new ByteArrayInputStream(fileContent);
			//BufferedImage newImage = ImageIO.read(inStreambj);
			//ImageIO.write(newImage, "jpg", new File(name ));
			System.out.println("Image generated from the byte array.");
			System.out.println("In Sreamy " + inStreambj);
			System.out.println("Uriella " + uriella);
			//return newImage;
			return uriella;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
