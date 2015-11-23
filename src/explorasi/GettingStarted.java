/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package explorasi;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.util.Date;

/**
 *
 * @author M. Reza Irvanda
 */
public class GettingStarted {
    public static void main(String args[]){
        MongoClient mongoClient = new MongoClient("localhost",27017);
        DB db = mongoClient.getDB("pat_13512042");
        DBCollection coll = db.getCollection("users");
        
        BasicDBObject doc = new BasicDBObject("name","joshua").append("kelas","01").append("timestamp", new Date());
        coll.insert(doc);
        
        DBCursor cursor = coll.find(doc);
        
        while(cursor.hasNext()){
            DBObject tmp = cursor.next();
            System.out.println(tmp.get("name"));
            System.out.println(tmp.get("kelas"));
            System.out.println(tmp.get("timestamp"));
            System.out.println(tmp.get("_id"));
        }
        
        
    }
}
