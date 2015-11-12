/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tugas;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

/**
 *
 * @author M. Reza Irvanda
 */
public class TwitterLike {
    private Cluster cluster;
    private Session session;
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    
    public TwitterLike(String cluster_address,String keyspace_name){
        cluster = Cluster.builder().addContactPoint(cluster_address).build();
        session = cluster.connect(keyspace_name);
    }
    
    public boolean login(String username, String password){
        String cql_query = "SELECT * FROM users WHERE username='"+username+"'";
        ResultSet results = session.execute(cql_query);
        
        //check if password is same
        Row result = results.one();
        return result.getString("password").equals(password);
    }
    
    public boolean register(String username, String password){
        try{
            // If already exist, will be replaced by new value
            session.execute("INSERT INTO users (username, password) VALUES ('" + username + "', '" + password + "');");
            return true;
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }
    
    public boolean followFriend(String username, String friendname){
        try{
            session.execute("INSERT INTO friends (username, friend, since) VALUES ('" + 
                                username + "', '" + friendname + "',dateof(now()))");
            session.execute("INSERT INTO followers (username, follower, since) VALUES ('" + 
                                friendname + "', '" + username + "', dateof(now()))");
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    
    public void postTweet(String username, String tweet){
        UUID tweet_id = UUID.randomUUID();
        session.execute("INSERT INTO tweets (tweet_id, username, body) VALUES ("+tweet_id+", '" + username + "', '" + tweet + "')");
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
