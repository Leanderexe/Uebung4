package protocolscraping;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import database.DatabaseOperation;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import scala.Int;

public class XmlConversion {

    private String url;
    private List<String> searchValue;
    private String parentURL = "https://www.bundestag.de";

    private final String REDNER_LIST_KEY = "rednerliste";
    private final String REDNER_KEY = "redner";

    private final String REDE_LIST_KEY = "redeliste";
    private final String REDE_KEY = "rede";
    Integer namecounter;
    String speechcontent;
    /*
    * Identifier for database operation
    * */
    private DatabaseOperation databaseOperation;

    public XmlConversion(String url, List<String> searchValue) {
        this.url = url;
        this.searchValue = searchValue;
    }

    public void init() {

        databaseOperation = DatabaseOperation.build();

        //connecting to the website and converting it to html source
        String pageSource = getPageSource(url);
        //Initialize empty list xml-IDs
        List<String> endPointIds = new ArrayList<String>();

        for (String string : searchValue) {

            //Here we get the xml-ID
            String id = getIdByXpath(string, pageSource);
            endPointIds.add(id);
        }

        //Here we are getting the xml-URLs by xml-ID
        Map<String, Map<String,String>> datas = parseXmlUrl(endPointIds);

        System.out.println(datas);
        System.out.println("HALLO");

        extractSpeech(datas);


        xmlToBsonDocument(datas);
    }

    private String getIdByXpath(String searchValue, String pageSource) {
        String id = "";
        XPath xPath = null;

        String xpath = "//*[text()='" + searchValue + "']/following::div[contains(@id,\"bt-collapse\")][1]";

        xPath = XPathFactory.newInstance().newXPath();
        org.w3c.dom.Document doc = null;
        try {

            TagNode tagNode = new HtmlCleaner().clean(pageSource);
            try {
                doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }

            NodeList nodeList = (NodeList) xPath.evaluate(xpath, doc, XPathConstants.NODESET);

            if (nodeList.getLength() > 0) {

                Node node = nodeList.item(0);
                String idValue = node.getAttributes().getNamedItem("id").getNodeValue();

                String[] arr = idValue.split("-");

                id = arr[arr.length - 1];
            }

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return id;
    }

    private String getPageSource(String url) {
        String pageSource = "";

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0").get();

            return doc.html();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return pageSource;
    }

    private Document getDocument(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0").get();

            return doc;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, Map<String,String>> parseXmlUrl(List<String> key) {
        int limit = 10;
        int offset = 0;

        Map<String,String> xmlURLs = new LinkedHashMap<>();

        //String->xml-ID, List-> xml-URL
        //Map<String, List<String>> data = new LinkedHashMap<>();
        Map<String, Map<String,String>> data = new LinkedHashMap<>();

        //going through all the ID's
        for (String string : key) {

            //We are getting all the URL's
            recursiveMethodToGetAllXmlURl(xmlURLs, limit, offset, string);
            data.put(string, xmlURLs);

            xmlURLs = new LinkedHashMap<>();
            offset = 0;
        }

        return data;
    }

    private void recursiveMethodToGetAllXmlURl(Map<String,String> xmlURLs, int limit, int offset, String key) {

        String url = "https://www.bundestag.de/ajax/filterlist/de/services/opendata/" + key + "-" + key + "?limit="
                + limit + "&noFilterSet=false&offset=" + offset;

        Document pageSource = getDocument(url);

        Elements elements = pageSource.getElementsByClass("bt-link-dokument");
        Elements plenarDes = pageSource.getElementsByClass("bt-documents-description");

        Integer counter = 0;


        for (Element element : elements) {
            String xmlUrl = element.attr("href");
            String plenarName = plenarDes.get(counter).getElementsByTag("strong").text();

            System.out.println(plenarName);

            xmlURLs.put(xmlUrl, plenarName);
            counter++;
        }
        offset += limit;

        if (elements.size() == limit) {
            recursiveMethodToGetAllXmlURl(xmlURLs, limit, offset, key);
        }

    }

    private void xmlToBsonDocument(Map<String, Map<String,String>> datas) {

        for (Map.Entry<String, Map<String,String>> data : datas.entrySet()) {

            Map<String,String> xmlURL = data.getValue();


            for (Entry<String, String> string : xmlURL.entrySet()) {

                String xml = getPageSource(parentURL+string.getKey());
                String name = string.getValue();

                JSONObject json = null;
                try {
                    json = XML.toJSONObject(xml);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                org.bson.Document doc = org.bson.Document.parse(json.toString());
                //You can work with this list of docs now
                createCollectionByDoc(doc);
               // System.out.println(doc);
                //System.out.println(name);
            }
        }
    }

    private void createCollectionByDoc(org.bson.Document document) {
        /*
        * Insert or update protocol in database
        * */
        for (Map.Entry<String, Object> e : document.entrySet()) {
                org.bson.Document document1 = (org.bson.Document) e.getValue();
                /*
                * Only insert in db if its protocol
                * */
                if (e.getKey().contains(DatabaseOperation.PROTOKOL_KEY)) {
                    databaseOperation.insertOneDocument(DatabaseOperation.PROTOKOL_KEY, document1);
                }
        }

        /*
        * Fetch all the speaker from collection and save them in separate collection
        *
        * Check if collection already exists or not
        * */
        if (!databaseOperation.exists(REDNER_KEY)) {
            databaseOperation.createNewCollection(REDNER_KEY);
        }
        try {
            /*
             * Insert speakers in separate collection
             * */
            Collection<Object> values = document.values();
            values.forEach(o -> {
                org.bson.Document document1 = (org.bson.Document) o;
               // org.bson.Document document2 = (org.bson.Document) o;

                List<org.bson.Document> data = (ArrayList<org.bson.Document>)
                        (((org.bson.Document) ((org.bson.Document)
                                document1.get(REDNER_LIST_KEY))).get(REDNER_KEY));

               /* org.bson.Document sitzung = (org.bson.Document) document2.get("sitzungsverlauf");
                List<org.bson.Document> speechData = (ArrayList<org.bson.Document>)  sitzung.get("tagesordnungspunkt");


                org.bson.Document vorspann = (org.bson.Document) document1.get("vorspann");
                org.bson.Document kopfdaten = (org.bson.Document) vorspann.get("kopfdaten");
                org.bson.Document veranstaltungsdaten = (org.bson.Document) kopfdaten.get("veranstaltungsdaten");
                org.bson.Document info = (org.bson.Document) veranstaltungsdaten.get("datum");
                String datum = info.getString("date");
                System.out.println(datum + " Hallloo");

                //speechData.forEach(s -> {

                //});

                speechData.forEach(sData -> {

                    List<org.bson.Document> speech = (List<org.bson.Document>) sData.get("rede");

                    namecounter = 0;
                    System.out.println("SPEEEECH" + speech + "HOO");
                    speech.forEach(s -> {
                        if (namecounter == 0) {
                            org.bson.Document name = (org.bson.Document) s.get("name");
                            org.bson.Document id = (org.bson.Document) s.get("id");

                            System.out.println("HAA" + name + "HOO");
                            System.out.println("HAA" + id + "HOO");

                            namecounter += 1;
                        }

                        List<org.bson.Document> p = (List<org.bson.Document>) s.get("p");

                        p.forEach(content -> {
                            org.bson.Document cPart = (org.bson.Document) content.get("name");
                            speechcontent = speechcontent + " " + cPart;
                        });

                    });


                    System.out.println("HII" + speech + "HII");

                });

*/

                data.forEach(d -> {
                    try {
                        org.bson.Document speakerdoc = (org.bson.Document) d.get("name");
                        Integer id = (Integer) d.get("id");

                        /*
                        * Creating custom document for avoid duplicates
                        * */
                        org.bson.Document doc = new org.bson.Document(DatabaseOperation.ID_COL_KEY, id);
                        doc.append(DatabaseOperation.VORNAME_COL_KEY, speakerdoc.get(DatabaseOperation.VORNAME_COL_KEY).toString());
                        doc.append(DatabaseOperation.FRAKTION_COL_KEY, speakerdoc.get(DatabaseOperation.FRAKTION_COL_KEY).toString());
                        doc.append(DatabaseOperation.SURNAME_COL_KEY, speakerdoc.get(DatabaseOperation.SURNAME_COL_KEY).toString());

                        /*
                        * Insert document in database
                        * */
                        databaseOperation.insertOneDocument(REDNER_KEY, doc);
                    }catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                });


            });
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private void extractSpeech(Map<String, Map<String,String>> data) {




    }


}
