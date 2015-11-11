/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package explorasi;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 *
 * @author M. Reza Irvanda
 */
public class GettingStarted {
    public static void main(String args[]){
        Cluster cluster;
        Session session;
        
        //connect to the cluster and keyspace "demo"
        cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
        session = cluster.connect("joshuaeja");
        
        //adding one instance
        session.execute("INSERT INTO users(user_id, fname, lname) values (1111,'Riva','Syafri');");
        
        ResultSet results = session.execute("SELECT * FROM users where fname='Riva';");
        
        //iterate from the resulted row from query
        for(Row row : results){
            System.out.format("%s %s %d\n", row.getString("fname"),row.getString("lname"),row.getInt("user_id"));
        }
    }
}
