package database;

import org.bson.Document;

import java.util.List;

/*
* Mongo database operations
* */
public interface Operation {

    Boolean exists(String collectionName);

    List<Document> findAll();

    void createNewCollection(String collectionName);

    List<Document> findAllDocument(String collectionName);

    void insertAll(String collectionName, List<Document> docList);

    void insertOneDocument(String collection, Document document);

    /*
    * Find a document by key and value from a collection
    * */
    Document findDocument(String collection, String key, String value);

    /*
    * Find a document by document id from a collection
    * */
    Document findDocumentById(String collection, Integer id);

    void printAllCollections();

    void deleteCollection(String collection);

    void deleteDocument(String collection, String key, String value);
}
