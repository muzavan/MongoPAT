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

/**
 *
 * @author M. Reza Irvanda
 */
public class TwitterLike {
    private Cluster cluster;
    private Session session;
    
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
        return true;
    }
    
    public boolean followFriend(String friendname){
        return true;
    }
    
    public boolean postTweet(/*Parameter belum tahu mau apa aja*/){
        return true;
    }
    
    public boolean getTweetsFromUser(String username){
        return true;
    }
    public boolean getTimelineFromUser(String username){
        return true;
    }
    
    public void logout(){
        cluster.close();
    }
}
