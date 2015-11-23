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
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
        String time = new Date().toString();
        BasicDBObject _tweet = new BasicDBObject("username",username).append("body", tweet).append("tweet_id", tweet_id.toString());
        BasicDBObject _userline = new BasicDBObject("username",username).append("time", time).append("tweet_id", tweet_id.toString());
        BasicDBObject _timeline = new BasicDBObject("username",username).append("time", time).append("tweet_id", tweet_id.toString());
        
        tweets.save(_tweet);
        userlines.save(_userline);
        timelines.save(_timeline);
        //search all followers of username
        BasicDBObject query = new BasicDBObject("username", username);
        List<String> followers = new ArrayList<String>();
        Cursor results = db.getCollection(FOLLOWERS_COLLECTION).find(query);
        while(results.hasNext()){
            followers.add(String.valueOf(results.next().get("follower")));
        }
        for (String follower : followers){
            _timeline = new BasicDBObject("username",follower).append("time", time).append("tweet_id", tweet_id.toString());
            timelines.save(_timeline);
        }
    }
    
    public void getTweetsFromUser(String username){
        DBCollection tweets = db.getCollection(TWEETS_COLLECTION);
        DBCollection userlines = db.getCollection(USERLINE_COLLECTION);
        BasicDBObject userline_query = new BasicDBObject("username", username);
        Cursor users = userlines.find(userline_query);
        while(users.hasNext()){
            String tweet_id = String.valueOf(users.next().get("tweet_id"));
            BasicDBObject query = new BasicDBObject("tweet_id",tweet_id);
            Cursor _tweets = tweets.find(query);
            while(_tweets.hasNext()){
                DBObject tmp = _tweets.next();
                System.out.printf("%s : %s", String.valueOf(tmp.get("username")), String.valueOf(tmp.get("body")));
            }
        }
    }
    public void getTimelineFromUser(String username){
        DBCollection tweets = db.getCollection(TWEETS_COLLECTION);
        DBCollection timelines = db.getCollection(TIMELINE_COLLECTION);
        BasicDBObject timeline_query = new BasicDBObject("username",username);
        Cursor results = timelines.find(timeline_query);
        while(results.hasNext()){
            String tweet_id = String.valueOf(results.next().get("tweet_id"));
            BasicDBObject query = new BasicDBObject("tweet_id",tweet_id);
            Cursor _tweets = tweets.find(query);
            while(_tweets.hasNext()){
                DBObject tmp = _tweets.next();
                System.out.printf("%s : %s", String.valueOf(tmp.get("username")), String.valueOf(tmp.get("body")));
            }
        }
    }
    
    public void logout(){
        // do nothing?
    }
}
