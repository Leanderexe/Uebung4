import database.DatabaseOperation;
import de.fau.cs.osr.utils.StringUtils;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;


import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static spark.Spark.*;


public class RestAPI {
    static DatabaseOperation db = new DatabaseOperation();
    public static void main(String[] args) throws IOException, JSONException {
        String[] redner = {"angela Merkel", "Putin", "Markon", "Lauterbach", "Amthor", "trump", "sleepy joe"};
        List key = new ArrayList();
        List value = new ArrayList();
        for(int i = 0; i < 10; i++){
            key.add("key " + i);
            value.add("value " + i);
        }
        //System.out.println(db.findAllDocument("redner"));
        //List rednerlist = new ArrayList();
        //for(Document doc: db.findAllDocument("redner")){
        //    rednerlist.add(doc.toJson());
        //}
        //String output = getsingleJson("redner", rednerlist.toString());
        //System.out.println(rednerlist);


        String rednerstring = "{";
        for (int i = 0; i < redner.length; i++){
            if (i == redner.length-1){
                rednerstring += redner[i] + "}";
            }
            else {
                rednerstring += redner[i] + ", ";
            }
        }

        String json = getJson(key, value);
        System.out.println(rednerstring);

        // url: http://localhost:4567/rest
        init();
        //initExceptionHandler((e) -> System.out.println("Ups! Der Server konnte leider nicht gestartet werden."));
        /*
        post("/rest", ((request, response) ->
                {
                    response.redirect("/bar");
                    request.headers("okay lets go!");
                    return true;
                }
                ));

        get("/rest", (request, response) -> "Hello");

        get("/rest", ((request, response) ->
        {
            String hello = request.headers("i bims ein header");
            response.header(hello, "2");
            return true;
        }));
        //halt(400);

         */
        String finalRednerstring = rednerstring;
        /**
         * Prints out JSON with all rendner at http://localhost:4567/redner
         * @return String with the all Redner in JSON.
         */
        get("/redner", (request, response) -> {
            response.body(json);
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("redner")){
                rednerlist.add(doc.toJson());
            }
            return "{" + "\"redner\": " + rednerlist + "," + "\"success\": true" + "}";
        });


        /**
         * Prints out JSON with redner which contain the search parameter at http://localhost:4567/redner/:searchstr
         * @return String with redner which contain the search parameter in JSON.
         */
        get("/redner/:searchstr", (request, response) -> {
            String searchstr = request.params(":searchstr");
            List rednerlist = new ArrayList();
            for(Document doc: db.findAllDocument("redner")){
                String rednerelement = doc.toJson();
                rednerelement = rednerelement.replace("_id", "");
                rednerelement = rednerelement.replace("vorname", "");
                rednerelement = rednerelement.replace("fraktion", "");
                rednerelement = rednerelement.replace("nachname", "");
                if (rednerelement.contains(searchstr)){
                    rednerlist.add(doc.toJson());
                }
            }
            return "{" + "\"redner\": " + rednerlist + "}";
        });

        /**
         * Prints out JSON with all Tokens at http://localhost:4567/token
         * Exercise b1)
         * @return String with the all Tokens in JSON.
         */
        get("/token", (request, response) -> {
            return json;
        });

        /**
         * Prints out JSON with all POS at http://localhost:4567/pos
         * Exercise b2)
         * @return String with the all POS in JSON.
         */
        get("/pos", (request, response) -> {

            return json;
        });

        /**
         * Prints out JSON with all sentiment at http://localhost:4567/sentiment
         * Exercise b3)
         * @return String with the all sentiment in JSON.
         */
        get("/sentiment", (request, response) -> {

            return json;
        });

        /**
         * Prints out JSON with all named entities at http://localhost:4567/namedentities
         * Exercise b4)
         * @return String with the all named entities in JSON.
         */
        get("/namedentities", (request, response) -> {

            return json;
        });

    }

    private static String getJson(List key, List value) {
        String json = "";
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        for (int i = 0; i < key.size(); i++) {
            builder.add((String) key.get(i), (String) value.get(i));
        }
        json = builder.build().toString();
        return json;
    }

    private static String byname(String name, List key, List value) throws JSONException {
        JSONObject jn = new JSONObject("hello");
        if (key.contains(name)){
            for(int i = 0; i < key.size(); i++){
                if (key.get(i).toString().equals(name)){
                    getsingleJson((String)key.get(i), (String)value.get(i));
                    return getsingleJson((String)key.get(i), (String)value.get(i));
                }
            }
            return "false";
        }
        else{
            return "false";
        }
    }

    private static String getsingleJson(String key, String value) {
        String json = "";
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add(key, value);
        json = builder.build().toString();
        return json;
    }

}
