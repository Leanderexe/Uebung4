package database;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.io.*;
import java.util.Arrays;
import java.util.Properties;
/*
 * Mongodb connection class
 * */
public class MongoDBConnectionHandler {

    private MongoClient clientWithURI;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;
    /*
     * Database properties values
     * */
    private final String hostName =  "prg2021.texttechnologylab.org";
    private final String remoteDatabase = "PRG_WiSe21_Gruppe_5_1";
    private final String remoteUser = "PRG_WiSe21_Gruppe_5_1";
    private final String remotePassword = "hUSANarm";
    private final String remotePort = "27020";
    private final String remoteCollection = "speeches";


    /*
     * Database properties keys
     * */
    private final String hostNameKey =  "remote_host";
    private final String remoteDatabaseKey = "remote_database";
    private final String remoteUserKey = "remote_user";
    private final String remotePasswordKey = "remote_password";
    private final String remotePortKey = "remote_port";
    private final String remoteCollectionKey = "remote_collection";

    private final String CONFIG_FILE = "databaseConfi";

    private String remoteCollectionValue = "";

    private String remoteDatabaseValue = "";

    private Properties properties;

    public MongoDBConnectionHandler() {
        createProperties();
    }
    /*
     * Create properties for database connection
     * */
    private void createProperties() {

        //Create property file
        properties = new Properties();
        OutputStream outputStream = null;

        try {
            /*
             * Read file using output stream
             * */
            outputStream = new FileOutputStream(CONFIG_FILE);

            /*
             * Load properties in memory
             * */
            properties.setProperty(hostNameKey, hostName);
            properties.setProperty(remoteDatabaseKey, remoteDatabase);
            properties.setProperty(remoteUserKey, remoteUser);
            properties.setProperty(remotePasswordKey, remotePassword);
            properties.setProperty(remotePortKey, remotePort);
            properties.setProperty(remoteCollectionKey, remoteCollection);

            properties.store(outputStream, null);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String dbCredentials() {
        //Fetch property file
        try {
            InputStream is = new FileInputStream(CONFIG_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String remoteName = properties.getProperty(hostNameKey);
        String remoteDatabase = properties.getProperty(remoteDatabaseKey);
        String remoteUser = properties.getProperty(remoteUserKey);
        String remotePassword = properties.getProperty(remotePasswordKey);
        String remotePort = properties.getProperty(remotePortKey);
        remoteCollectionValue = properties.getProperty(remoteCollectionKey);

        remoteDatabaseValue = remoteDatabase;

        //"mongodb://" + remote_user + ":" + remote_password + "@" + remote_host + ":" + remote_port + "/" + remote_database
        //return String.format("mongodb://%s:%s@%s:%s/%s", remoteUser, remotePassword, remoteName, remotePort, remoteDatabase);
        //System.out.println("mongodb://" + remoteUser + ":" + remotePassword + "@" + remoteName + ":" + remotePort + "/" + remoteDatabase);
        return "mongodb://" + remoteUser + ":" + remotePassword + "@" + remoteName + ":" + remotePort + "/" + remoteDatabase;
    }
    /*
     * Get remote MongoDB database
     * */
    public MongoDatabase getDatabase() {

        if (mongoDatabase == null) {
            mongoDatabase = mongoClient().getDatabase(remoteDatabase);
        }
        return mongoDatabase;
    }

    /*
     * Get remote MongoDB collection
     * */
    public MongoCollection<Document> getCollection() {
        if (mongoCollection == null) {
            mongoCollection = getDatabase().getCollection(remoteCollectionValue);
        }
        return mongoCollection;
    }

    /*
     * Create MongoDB client
     * */
    public MongoClient mongoClient(){
        if (clientWithURI == null) {
            clientWithURI = new MongoClient(new MongoClientURI(dbCredentials()));
        }
        MongoCredential credential = MongoCredential.createCredential("xyz", "admin", "password".toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 1235), Arrays.asList(credential), MongoClientOptions.builder().build());
        return clientWithURI;
    }

    public String remoteCollection() {
        if (StringUtils.isEmpty(remoteCollectionValue)) {
            throw new NullPointerException("Remote collection value is empty");
        }
        return remoteCollectionValue;
    }

    public String remoteDatabase() {
        return remoteDatabaseValue;
    }

}

