/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cinemanetworkapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

/**
 *
 * @author Ahmed
 */
public class server {
    static DatagramSocket serverSocket;
    static ArrayList<Movie> mylist = new ArrayList<>();
    static ArrayList<Ticket> myTickets = new ArrayList<>();
    static ArrayList<User> myUsers = new ArrayList<>();
    private InetAddress packetIP;
    private int packetPort;
    
    public server(int localPort){
        try {
            serverSocket = new DatagramSocket(7000);
            
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }
    
    public Object receiveObject(){
        try{
            byte[] receiveData = new byte[1024];
        
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            serverSocket.receive(receivePacket);
            byte[] recData = receivePacket.getData();
            packetIP = receivePacket.getAddress();
            packetPort = receivePacket.getPort();

            ByteArrayInputStream bais = new ByteArrayInputStream(recData);

            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
        
    }
    
    public void sendResponse(Object sendObject, InetAddress IPAddress, int port) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(sendObject);

        byte[] data = baos.toByteArray();


        DatagramPacket packet = new DatagramPacket(data, data.length, IPAddress, port);

        serverSocket.send(packet);
    }
    
    public static void main(String[] args) throws SocketException, IOException, ClassNotFoundException {
        //Reading Data from file
        readDataFromFile();
        
        //creating server
        server myServer = new server(7000);
        
        System.out.println("Server Started at port 7000");
      
        while(true){
            //Receiving Data
            Object receivedObject = myServer.receiveObject();
            
           
                if(receivedObject instanceof Message){
                    System.out.println("Received Message");
                    Message msgbj = (Message) receivedObject;

                    String[] operation = msgbj.getData().split(",");



//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------      U S E R      -----------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------

                
                if(operation[1].trim().equals("U")){
                    
                    User userObject = (User) myServer.receiveObject();
                    
                    if(operation[0].trim().equals("0")){
                        System.out.println("Request to add User Data");
                        System.out.println(userObject.toString());

                        String userName = userObject.username;
                        System.out.println(userName);
                        boolean userChk = false;

                        for (User usr : myUsers) {
                            if (usr.username.equals(userName)) {
                                System.out.println(usr.username);

                                //sending response if found
                                myServer.sendResponse(new Message("unsuccessful"), myServer.packetIP, myServer.packetPort);
                                System.out.println("Response Sent");
                                userChk = true;
                                break;
                            }
                        }
                        if (!userChk){
                            //Saving Data to file
                            myUsers.add(userObject);
                            writeDataToFile("U");

                            //Sending Response
                            myServer.sendResponse(new Message("success"), myServer.packetIP, myServer.packetPort);
                            System.out.println("Response Sent");
                        }


                        
                    }else if(operation[0].trim().equals("1")){
                        System.out.println("Request to Search by id");
                        String searchUsnername = userObject.username;
                        String searchPassword = userObject.password;
                        boolean isFound=false;

                        //Searching for username and password in list
                        for (User usr: myUsers){
                            if(usr.username.equalsIgnoreCase(searchUsnername) && usr.password.equalsIgnoreCase(searchPassword)){
                                

                            //sending response if found
                            myServer.sendResponse(new Message("success"), myServer.packetIP, myServer.packetPort);
                            System.out.println("Sending an Object\n");

                            isFound=true;
                            break;
                            }

                        }
                        if(!isFound){
                            myServer.sendResponse(new Message("Not Found\n"), myServer.packetIP, myServer.packetPort);
                        }
                    }
                    
                    
                }else if(operation[1].trim().equals("M")){



                    if(operation[0].trim().equals("0")){
                        Movie movieObject = (Movie) myServer.receiveObject();

                        System.out.println("Request to add Movie Data");

                        System.out.println(movieObject.toString());

                        int movId = movieObject.id;
                        System.out.println(movId);
                        boolean movieChk = false;


                        for (Movie mov : mylist) {
                            if (mov.id == movId) {

                                System.out.println(mov.id);

                                //sending response if found
                                myServer.sendResponse(new Message("Id already Exists! Please TRY AGAIN!"), myServer.packetIP, myServer.packetPort);
                                System.out.println("Response Sent");
                                movieChk = true;
                                break;
                            }
                        }
                        if (!movieChk){
                            //Saving Data to file
                            mylist.add(movieObject);
                            writeDataToFile("M");

                            //Sending Response
                            myServer.sendResponse(new Message("MOVIE ADDED SUCCESSFULLY"), myServer.packetIP, myServer.packetPort);
                            System.out.println("Response Sent\n");
                        }


                    }else if(operation[0].trim().equals("1")){
                        System.out.println("Request to View All Movies");

                        //Calculating size of data to be sent
                        int movieCount = mylist.size();
                        if(movieCount<1){
                            myServer.sendResponse(new Message("No Movie record found!"), myServer.packetIP, myServer.packetPort);
                        }else{
                            //Sending All data as Multiple Responses
                            myServer.sendResponse(new Message("array-"+movieCount), myServer.packetIP, myServer.packetPort);
                            for (Movie movie: mylist){
                                myServer.sendResponse(movie, myServer.packetIP, myServer.packetPort);
                                System.out.println("Sending an Object\n");
                            }
                        }
                    }else if(operation[0].trim().equals("2")){
                        Movie movieObject = (Movie) myServer.receiveObject();
                        System.out.println("Request to Search by id");
                        int searchid = movieObject.id;
                        boolean isFound=false;

                        //Searching for username and password in list
                        for (Movie mov: mylist){
                            if(mov.id==searchid){


                                //sending response if found
                                myServer.sendResponse(mov, myServer.packetIP, myServer.packetPort);
                                System.out.println("Sending an Object\n");

                                isFound=true;
                                break;
                            }

                        }
                        if(!isFound){
                            myServer.sendResponse(new Message("Not Found"), myServer.packetIP, myServer.packetPort);
                        }
                    }else if(operation[0].trim().equals("4")){
                        Movie movieObject = (Movie) myServer.receiveObject();
                        System.out.println("Request to Delete Movie by id");
                        int searchid = movieObject.id;
                        boolean isFound=false;

                        //Searching for username and password in list
                        for (Movie mov: mylist){
                            if(mov.id==searchid){
                                
                                mylist.remove(mov);
                                writeDataToFile("M");
                                


                                //sending response if found
                                myServer.sendResponse(new Message("Movie with Id "+searchid+" successfully Deleted"), myServer.packetIP, myServer.packetPort);
                                System.out.println("Sending an reply\n");

                                isFound=true;
                                break;
                            }

                        }
                        if(!isFound){
                            myServer.sendResponse(new Message("Not Found"), myServer.packetIP, myServer.packetPort);
                        }
                    }
                }
                else if(operation[1].trim().equals("T")){
                    
                    
                    
                    if(operation[0].trim().equals("0")){
                        Ticket ticketObject = (Ticket) myServer.receiveObject();
                        
                        System.out.println("Request to add Ticket Data");

                        System.out.println(ticketObject.toString());

                        int ticketId = ticketObject.id;
                        int movieId = ticketObject.bookedMovie.id;
                        String userName = ticketObject.username;
                        System.out.println(ticketId);
                        boolean ticketChk = true;


                        for (Ticket tkt : myTickets) {
                            if (tkt.id == ticketId) {

                                System.out.println(tkt.id);

                                //sending response if found
                                myServer.sendResponse(new Message("Id already Exists! Please TRY AGAIN!"), myServer.packetIP, myServer.packetPort);
                                System.out.println("Response Sent");
                                ticketChk = false;
                                break;
                            }
                        }
                        boolean movieCheck = false;

                        for(Movie mov : mylist){
                            if(mov.id == movieId){
                                System.out.println("Movie with Id Found "+movieId);
                                ticketObject.bookedMovie.name = mov.name;
                                ticketObject.bookedMovie.rating = mov.rating;
                                ticketObject.bookedMovie.year = mov.year;
                                movieCheck = true;
                                break;
                            }
                        }

                        if(!movieCheck){
                            myServer.sendResponse(new Message("Movie Id Does not Exists"), myServer.packetIP, myServer.packetPort);
                            System.out.println("Response Sent");
                        }

                        boolean userCheck = false;

                        for(User usr : myUsers){
                            if (usr.username.equals(userName)){
                                userCheck = true;
                                System.out.println(userName+" exists");
                                break;
                            }
                        }

                        if(!userCheck){
                            myServer.sendResponse(new Message("Username Does not Exists"), myServer.packetIP, myServer.packetPort);
                            System.out.println("Response Sent");
                        }

                        if (ticketChk && movieCheck && userCheck){
                            //Saving Data to file
                            myTickets.add(ticketObject);
                            writeDataToFile("T");
                            

                            //Sending Response
                            myServer.sendResponse(new Message("TICKET ADDED SUCCESSFULLY"), myServer.packetIP, myServer.packetPort);
                            System.out.println("Response Sent\n");
                        }


                    }else if(operation[0].trim().equals("1")){
                        System.out.println("Request to View All Tickets");
                        String username = operation[2].trim();
                        System.out.println(username+" requested to view Tickets");
                        if(username.equals("admin")){

                            int ticketCount = myTickets.size();
                            if(ticketCount<1){
                                myServer.sendResponse(new Message("No Ticket record found!"), myServer.packetIP, myServer.packetPort);
                            }else{
                                //Sending All data as Multiple Responses
                                myServer.sendResponse(new Message("array-"+ticketCount), myServer.packetIP, myServer.packetPort);
                                for (Ticket ticket: myTickets){
                                    myServer.sendResponse(ticket, myServer.packetIP, myServer.packetPort);
                                    System.out.println("Sending an Object\n");
                                }
                            }
                        }else{

                            ArrayList<Ticket> userTickets = new ArrayList<>();


                            for (Ticket tkt : myTickets){
                                if (tkt.username.equals(username)){
                                    userTickets.add(tkt);
                                }
                            }




                            int ticketCount = userTickets.size();
                            if(ticketCount<1){
                                myServer.sendResponse(new Message("No Ticket record found!"), myServer.packetIP, myServer.packetPort);
                            }else{
                                //Sending All data as Multiple Responses
                                myServer.sendResponse(new Message("array-"+ticketCount), myServer.packetIP, myServer.packetPort);
                                for (Ticket ticket: userTickets){
                                    myServer.sendResponse(ticket, myServer.packetIP, myServer.packetPort);
                                    System.out.println("Sending an Object\n");
                                }
                            }

                        }
                            

                    }else if(operation[0].trim().equals("2")){
                        Ticket ticketObject = (Ticket) myServer.receiveObject();
                        System.out.println("Request to Search by id");
                        int searchid = ticketObject.id;
                        String username = operation[2].trim();

                        boolean isFound=false;

                        //Searching for username and password in list
                        for (Ticket tkt: myTickets){
                            if(username.equals("admin") && tkt.id==searchid){
                                myServer.sendResponse(tkt, myServer.packetIP, myServer.packetPort);
                                System.out.println("Sending an Object\n");

                                isFound=true;
                                break;
                            }
                            else if(tkt.username.equals(username) && tkt.id==searchid){
                                //sending response if found
                                myServer.sendResponse(tkt, myServer.packetIP, myServer.packetPort);
                                System.out.println("Sending an Object\n");

                                isFound=true;
                                break;
                            }

                        }
                        if(!isFound){
                            myServer.sendResponse(new Message("Not Found"), myServer.packetIP, myServer.packetPort);
                        }
                    }
                }
                
            }
            
        }
        
    }
    
    
    
    public static void writeDataToFile(String data) throws FileNotFoundException, IOException{
        System.out.println("Writing Data\n");
        if(data.equals("M")){
            FileOutputStream fos = new FileOutputStream("movies.txt");
            ObjectOutputStream oos = new ObjectOutputStream(fos);   
            oos.writeObject(mylist);
            oos.flush();
            oos.close();
        }
        
        else if(data.equals("T")){
            System.out.println("Writing Data to Tickets File");
            FileOutputStream fos = new FileOutputStream("tickets.txt");
            ObjectOutputStream oos = new ObjectOutputStream(fos);   
            oos.writeObject(myTickets);
            oos.flush();
            oos.close();
        }
        
        else if(data.equals("U")){
            System.out.println("Writing Data to Users File");
            FileOutputStream fos = new FileOutputStream("users.txt");
            ObjectOutputStream oos = new ObjectOutputStream(fos);   
            oos.writeObject(myUsers);
            oos.flush();
            oos.close();
        }
        
    }
    
    public static void readDataFromFile() throws FileNotFoundException, IOException, ClassNotFoundException{
        
        File movieFile = new File("movies.txt");
        File ticketFile = new File("tickets.txt");
        File userFile = new File("users.txt");
        
        if(movieFile.exists() || movieFile.length() != 0){
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("movies.txt"));
            mylist = (ArrayList<Movie>) ois.readObject();

        }
        if(ticketFile.exists() || ticketFile.length() != 0){
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("tickets.txt"));
            myTickets = (ArrayList<Ticket>) ois.readObject();
        
        }
        if(userFile.exists() || userFile.length() != 0){
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.txt"));
            myUsers = (ArrayList<User>) ois.readObject();
        }
        
    }
}
