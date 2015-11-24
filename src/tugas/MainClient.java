/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tugas;

import java.util.Scanner;

/**
 *
 * @author M. Reza Irvanda
 */
public class MainClient {
    public static void main (String args[]){
        if(args.length < 3){
            System.out.println("Args : <server_address>  <server_port> <collection_name>");
            return;
        }
        
        TwitterLike twitter = new TwitterLike(args[0], args[1], args[2]); //cluster_address, keyspace
        String username,password;
        Scanner scan = new Scanner(System.in);
        System.out.println("Pilih:\n1.Login\n2.Register");
        int pilihan = scan.nextInt();
        if(pilihan==1){
            System.out.println("Login as: ");
            username = scan.next();
            System.out.println("Password: ");
            password = scan.next();
            while(!twitter.login(username,password)){
                System.out.println("Username dan Password tidak cocok");
                System.out.println("Login as: ");
                username = scan.next();
                System.out.println("Password: ");
                password = scan.next();
            }
        }
        else{
            //register, wrong code assumed as register
            System.out.println("Register as: ");
            username = scan.next();
            System.out.println("Password: ");
            password = scan.next();
            while(!twitter.register(username,password)){
                System.out.println("Username sudah ada");
                System.out.println("Register as: ");
                username = scan.next();
                System.out.println("Password: ");
                password = scan.next();
            }
        }
        
        //main loop
        while(true){
            System.out.println("Pilih aksi:\n1. Follow a Friend \n2. Post a Tweet \n3. See Your Tweets \n4. See Timeline\n5. Logout");
            pilihan = scan.nextInt();
            if(pilihan<1 || pilihan >5){
                System.out.println("Kode aksi salah.");
            }
            else if(pilihan<=4){
                String parameter;
                switch(pilihan){
                    case 1:
                        System.out.println("Masukkan username dari friend anda : ");
                        parameter = scan.next();
                
                        if (!twitter.followFriend(username, parameter))
                            System.out.println("User `"+parameter+"` tidak ada.");
                        else
                            System.out.println("Anda berhasil follow `"+parameter+"`");
                        break;
                    case 2:
                        System.out.println("Masukkan twit anda : ");
                        parameter = scan.next();
                        twitter.postTweet(username, parameter);
                        System.out.println("Tweet anda berhasil di-post.");
                        break;
                    case 3:
                    	System.out.println("TWEETS FROM `"+username+"` :");
                        twitter.getTweetsFromUser(username);
                        break;
                    case 4:
                        twitter.getTimelineFromUser(username);
                        break;
                }
            }
            else{
            	// case 5
            	twitter.logout();
                System.out.println("Terima kasih!");
                break;
            }
        }
    }
}
