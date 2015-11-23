/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tugas;

import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author M. Reza Irvanda
 */
public class TwitterLike {
    private DB db;
    private static final String USER_COLLECTION = "users";
    private static final String FRIENDS_COLLECTION = "friends";
    private static final String FOLLOWERS_COLLECTION = "followers";
    private static final String USERLINE_COLLECTION = "userlines";
    private static final String TIMELINE_COLLECTION = "timelines";
    private static final String TWEETS_COLLECTION = "tweets";
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    
    public TwitterLike(String mongo_server_address,String mongo_server_port, String mongo_database){
        MongoClient mangaClient = new MongoClient(mongo_server_address, Integer.valueOf(mongo_server_port));
        db = mangaClient.getDB(mongo_database);
    }
    
    public boolean login(String username, String password){
        BasicDBObject query = new BasicDBObject("username",username);
        DBCollection coll = db.getCollection(USER_COLLECTION);
        Cursor cursor = coll.find(query);
        if(cursor.hasNext()){            
            return cursor.next().get("password").equals(password);
        }
        return false;
    }
    
    public boolean register(String username, String password){
        try{
            // If already exist, will be replaced by new value
            DBCollection coll = db.getCollection(USER_COLLECTION);
            BasicDBObject doc = new BasicDBObject("username",username).append("password",password);
            coll.save(doc);
            return true;
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }
    
    public boolean followFriend(String username, String friendname){
        try{
            DBCollection coll_friends = db.getCollection(FRIENDS_COLLECTION);
            DBCollection coll_follow = db.getCollection(FOLLOWERS_COLLECTION);
            String since = (new SimpleDateFormat("dd-MM-yyyy")).format(new Date());
            BasicDBObject doc_friends = new BasicDBObject("username",username).append("friend", friendname).append("since", since);
            BasicDBObject doc_follow = new BasicDBObject("username",friendname).append("friend", username).append("since", since);
            coll_friends.save(doc_friends);
            coll_follow.save(doc_follow);
            return true;
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }
    
    public void postTweet(String username, String tweet){
        UUID tweet_id = UUID.randomUUID();
        //session.execute("INSERT INTO tweets (tweet_id, username, body) VALUES ("+tweet_id+", '" + username + "', '" + tweet + "')");
        DBCollection tweets = db.getCollection(TWEETS_COLLECTION);
        DBCollection userlines = db.getCollection(USERLINE_COLLECTION);
        DBCollection timelines = db.getCollection(TIMELINE_COLLECTION);
        BasicDBObject _tweet = new BasicDBObject("username",username).append("time", new Date().toString()).append("tweet_id", tweet_id.toString());
        BasicDBObject _userline = new BasicDBObject()
        session.execute("INSERT INTO userline (username, time, tweet_id) VALUES ('"+
                            username + "', now(), " + tweet_id + ")");
        session.execute("INSERT INTO timeline (username, time, tweet_id) VALUES ('"+
                            username + "', now(), " + tweet_id + ")");
        ResultSet results = session.execute("SELECT * FROM followers where username = '"+username+"'");
        // Because username is not indexed, filter done in application, not in cassandra
        for (Row row : results){
            String follower = row.getString("follower");
                session.execute("INSERT INTO timeline (username, time, tweet_id) VALUES ('"+
                            follower + "', now(), " + tweet_id + ")");
        }
    }
    
    public void getTweetsFromUser(String username){
        ResultSet results = session.execute("SELECT * FROM userline WHERE username = '" + username + "'");
        for(Row row : results){
            Row tweet = session.execute("SELECT * FROM tweets WHERE tweet_id = " + row.getUUID("tweet_id")).one();
            System.out.println(tweet.getString("username") + ": " + tweet.getString("body"));
        }
    }
    public void getTimelineFromUser(String username){
        ResultSet results = session.execute("SELECT * FROM timeline WHERE username = '" + username + "'");
        for(Row row : results){
            Row tweet = session.execute("SELECT * FROM tweets WHERE tweet_id = " + row.getUUID("tweet_id")).one();
            System.out.println(tweet.getString("username") + ": " + tweet.getString("body"));
        }
    }
    
    public void logout(){
        cluster.close();
    }
}
