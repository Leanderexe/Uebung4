package database;

import com.mongodb.client.FindIterable;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class DatabaseOperation implements Operation {
    private final MongoDBConnectionHandler mongoDBConnectionHandler;
    /*
    * Redner collection columns
    * */
    public static final String REDNER_COLLECTION_NAME = "redner";
    public static final String SURNAME_COL_KEY = "nachname";
    public static final String FRAKTION_COL_KEY = "fraktion";
    public static final String VORNAME_COL_KEY = "vorname";
    public static final String TITLE_COL_KEY = "title";
    public static final String ID_COL_KEY = "_id";

    public static final String REDNER_KEY = "redner";
    public static final String PROTOKOL_KEY = "dbtplenarprotokoll";



    private static DatabaseOperation databaseOperation;

    public DatabaseOperation() {
        mongoDBConnectionHandler = new MongoDBConnectionHandler();
    }

    public static DatabaseOperation build() {
        if (databaseOperation == null) {
            databaseOperation = new DatabaseOperation();
        }
        return databaseOperation;
    }

    @Override
    public Boolean exists(String collectionName) {
        return mongoDBConnectionHandler.getDatabase().listCollectionNames().into(new ArrayList<String>()).contains(collectionName);
    }
    /*
    * Find all documents from database
    * */
    @Override
    public List<Document> findAll() {
        List<Document> collectionList = new ArrayList<>();
        ListCollectionsIterable<Document> findIterable = mongoDBConnectionHandler.getDatabase().listCollections();
        for (Document document : findIterable) {
            collectionList.add(document);
        }
        return collectionList;
    }

    @Override
    public void createNewCollection(String collectionName) {
        mongoDBConnectionHandler.getDatabase().createCollection(collectionName);
        System.out.println("Collection is created successfully");
    }

    /*
    * Read all documents from a collection
    * */
    @Override
    public List<Document> findAllDocument(String collectionName) {
        List<Document> collectionList = new ArrayList<>();
        FindIterable<Document> findIterable = mongoDBConnectionHandler.getDatabase().getCollection(collectionName).find();
        for (Document document : findIterable) {
            collectionList.add(document);
        }
        return collectionList;
    }

    @Override
    public void insertAll(String collectionName, List<Document> docList) {
        mongoDBConnectionHandler.getDatabase().getCollection(collectionName).insertMany(docList);
        System.out.println("Documents inserted successfully in database");
    }

    @Override
    public void insertOneDocument(String collection, Document document) {
        mongoDBConnectionHandler.getDatabase().getCollection(collection).insertOne(document);
        System.out.println("Document inserted successfully in database");
    }
    /*
     * Find a document by key and value from a collection
     * */
    @Override
    public Document findDocument(String collection, String key, String value) {
        return mongoDBConnectionHandler.getDatabase().getCollection(collection).find(eq(key, value)).first();
    }

    @Override
    public Document findDocumentById(String collection, Integer id) {
        return mongoDBConnectionHandler.getDatabase().getCollection(collection).find(eq(ID_COL_KEY, id)).first();
    }

    @Override
    public void printAllCollections() {

        MongoIterable<String> list = mongoDBConnectionHandler.getDatabase().listCollectionNames();

        System.out.println("\n=============List of collections===============\n");

        for (String name : list) {
            System.out.println(name);
        }
    }

    @Override
    public void deleteCollection(String collection) {
        mongoDBConnectionHandler.getDatabase().getCollection(collection).drop();
        System.out.println(collection + " deleted successfully!");
    }

    @Override
    public void deleteDocument(String collection, String key, String value) {
        mongoDBConnectionHandler.getDatabase().getCollection(collection).deleteOne(Filters.eq(key, value));
        System.out.println(key + " is deleted successfully!");
    }


}
