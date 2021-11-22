/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cinemanetworkapp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * @author Ahmed
 */
public class CinemaNetworkApp {

    /**
     * @param args the command line arguments
     */

    static client myClient;

    public static void main(String[] args) throws ClassNotFoundException, InterruptedException, ParseException {
        Scanner input= new Scanner(System.in);
        InetAddress IPAddress;
        try {
            IPAddress=InetAddress.getByName("DELL-021");
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            System.out.println("Host Not Found Please Restart Application");
            return;
        }

        //creating Client
        myClient = new client(5000,7000,IPAddress);


        Movie dataObject = new Movie(0,"",0,0);
        boolean userValidated = false;
        String currentUser = "";
        while(true){

            while(!userValidated){
                System.out.println("_________________________________________");
                System.out.println("--          USER REGISTRATION          --");
                System.out.println("_________________________________________");

                System.out.println("Enter Username (Already Registered, Enter 0)");
                String username = input.next();

                if(username.equals("0")){
                    System.out.println("\n\n");
                    break;
                }

                System.out.println("Enter Password");
                String password = input.next();

                boolean userRegistered = sendUserData(username,password,"0,U");
                if(userRegistered){
                    System.out.println("\n\nUser Registered\n\n");
                    currentUser = username;
                    break;
                }else{
                    System.out.println("\n\nInvalid Credentials\n\n");
                }
            }

            Thread.sleep(500);

            //----------------LOGIN----------------//

            while(!userValidated){

                System.out.println("_________________________________________");
                System.out.println("--             USER LOGIN             --");
                System.out.println("_________________________________________");
                System.out.println("Enter Username (No Account, Enter 0) ");
                String username = input.next();

                if(username.equals("0")){
                    break;
                }

                System.out.println("Enter Password");
                String password = input.next();

                userValidated = sendUserData(username,password,"1,U");

                if(userValidated){
                    System.out.println("\n\nValid Credentials\n\n");
                    currentUser = username;
                    //move to MAIN MENU GUI
                    break;
                }else{
                    System.out.println("\n\nInvalid Credentials\n\n");
                }
            }

            Thread.sleep(500);

            //--------------MAIN MENU-------------//
            while(userValidated) {
                System.out.println("\n\n");
                System.out.println("_________________________________________");
                System.out.println("--              MAIN MENU              --");
                System.out.println("_________________________________________");
                System.out.println("..............  1. MOVIE   ..............");
                System.out.println("..............  2. TICKET  ..............");
                System.out.println("..............  3. EXIT    ..............");
                System.out.println("_________________________________________");

                System.out.print("Please select your Desired Option: ");
                String mainInput = input.next();
                int userInput = 0;
                boolean check = false;

                if (mainInput.equals("1") || mainInput.equals("2") || mainInput.equals("3")) {
                    userInput = Integer.parseInt(mainInput);
                    check = true;
                } else {
                    System.out.println("Please Enter Valid Option!");
                }

                //--------------MOVIE MENU-------------//

                if (check == true) {
                    if (userInput == 1) {
                        while (true) {
                            //Showing Menu
                            System.out.println("\n\n");
                            System.out.println("_________________________________________");
                            System.out.println("--              MOVIE MENU             --");
                            System.out.println("_________________________________________");

                            if (currentUser.equals("admin")) {
                                System.out.println(".....  1. ADD MOVIE DATA             .....");
                            }

                            System.out.println(".....  2. VIEW MOVIE RECORDS         .....");
                            System.out.println(".....  3. SEARCH MOVIE RECORD by ID  .....");
                            System.out.println(".....  4. BACK                       .....");

                            System.out.print("Enter Your Choice: ");
                            String movieIn = input.next();

                            int userIn = 0;
                            boolean movieChk = false;

                            if (movieIn.equals("1") || movieIn.equals("2") || movieIn.equals("3") || movieIn.equals("4")) {
                                userIn = Integer.parseInt(movieIn);
                                movieChk = true;
                            } else {
                                System.out.println("Please Enter Valid Option!");
                            }


                            //Processing User Input

                            if (movieChk) {
                                if (userIn == 4) {
                                    break;
                                } else if (userIn == 1) {
                                    if (currentUser.equals("admin")) {
                                        System.out.print("Enter Movie Id: ");
                                        int id = input.nextInt();
                                        System.out.print("Enter Movie Name: ");
                                        String name = input.next();
                                        System.out.print("Enter Movie Rating: ");
                                        int rating = input.nextInt();
                                        System.out.print("Enter Movie Year: ");
                                        int year = input.nextInt();

                                        dataObject = new Movie(id, name, rating, year);

                                        dataObject.setOperation(0);
                                    } else {
                                        System.out.println("Only Admin has the Authorization to Add Movies!");
                                        dataObject.setOperation(4);
                                    }

                                } else if (userIn == 2) {
                                    dataObject = new Movie(0, "", 0, 0);

                                    dataObject.setOperation(1);
                                } else {
                                    System.out.println("Enter Movie Id: ");
                                    int id = input.nextInt();
                                    dataObject = new Movie(id, "", 0, 0);

                                    dataObject.setOperation(2);
                                }

                                //Sending Data
                                if (!myClient.sendObject(dataObject)) {
                                    System.out.println("Error Sending Data");
                                    continue;
                                }

                                System.out.println("Waiting For Response");

                                //Waiting For Response
                                Thread.sleep(1000);


                                //Receving Data
                                Object receivedObject = myClient.receiveObject();

                                //Processing Received Data
                                if (receivedObject instanceof Message) {
                                    Message msgObj = (Message) receivedObject;

                                    String msgData = msgObj.getData();

                                    if (msgData.contains("array")) {

                                        String[] msg = msgData.split("-");
                                        int count = Integer.parseInt(msg[1]);
                                        System.out.println("Total Objects: " + count + "\n");

                                        while (count > 0) {

                                            Movie moiveObj = (Movie) myClient.receiveObject();
                                            System.out.println(moiveObj.toString());
                                            count--;
                                        }
                                    } else {
                                        System.out.println("\n" + msgData);
                                    }
                                } else if (receivedObject instanceof Movie) {
                                    Movie movieData = (Movie) receivedObject;
                                    System.out.println(movieData.toString());
                                }


                                System.out.println("\nDo you want to continue (Y\\N)");
                                String temp = input.next();
                                if (temp.equalsIgnoreCase("N")) {
                                    break;
                                }


                            }
                        }

                    } else if (userInput == 2) {

                        while (true) {
                            //Showing Menu
                            System.out.println("\n\n");
                            System.out.println("___________________________________________");
                            System.out.println("--              TICKET MENU              --");
                            System.out.println("___________________________________________");
                            System.out.println(".....  1. BOOK TICKET DATA            .....");
                            System.out.println(".....  2. VIEW TICKET RECORDS         .....");
                            System.out.println(".....  3. SEARCH TICKET RECORD by ID  .....");
                            System.out.println(".....  4. BACK                        .....");

                            System.out.print("Enter Your Choice: ");
                            String ticketIn = input.next();

                            int userIn = 0;
                            boolean ticketChk = false;

                            if (ticketIn.equals("1") || ticketIn.equals("2") || ticketIn.equals("3") || ticketIn.equals("4")) {
                                userIn = Integer.parseInt(ticketIn);
                                ticketChk = true;
                            } else {
                                System.out.println("Please Enter Valid Option!");
                            }


                            Ticket ticketObject;
                            Message operation;
                            //Processing User Input

                            if (ticketChk) {
                                if (userIn == 4) {
                                    break;
                                } else if (userIn == 1) {
                                    System.out.print("Enter Ticket Id: ");
                                    int id = input.nextInt();
                                    System.out.print("Enter User Name: ");
                                    String name = input.next();
                                    System.out.print("Enter Movie Id: ");
                                    int movieId = input.nextInt();
                                    System.out.print("Enter Ticket Date dd/MM/yyyy: ");
                                    Date ticketDate = new SimpleDateFormat("dd/MM/yyyy").parse(input.next());
                                    System.out.print("Enter Time: ");
                                    int time = input.nextInt();

                                    ticketObject = new Ticket(id, name, new Movie(movieId, "", 0, 0), ticketDate, time);
                                    operation = new Message("0,T");

                                    if (!myClient.sendObject(operation)) {
                                        System.out.println("Error Sending Data");
                                        continue;
                                    }

                                    if (!myClient.sendObject(ticketObject)) {
                                        System.out.println("Error Sending Data");
                                        continue;
                                    }


                                } else if (userIn == 2) {
                                    operation = new Message("1,T");

                                    if (!myClient.sendObject(operation)) {
                                        System.out.println("Error Sending Data");
                                        continue;
                                    }

                                } else {
                                    System.out.println("Enter ticket Id: ");
                                    int id = input.nextInt();
                                    ticketObject = new Ticket(id, "", new Movie(0, "", 0, 0), new Date(), 0);
                                    operation = new Message("2,T");

                                    if (!myClient.sendObject(operation)) {
                                        System.out.println("Error Sending Data");
                                        continue;
                                    }

                                    if (!myClient.sendObject(ticketObject)) {
                                        System.out.println("Error Sending Data");
                                        continue;
                                    }

                                }


                                //Sending Data
                                System.out.println("Waiting For Response");

                                //Waiting For Response
                                Thread.sleep(1000);


                                //Receving Data
                                Object receivedObject = myClient.receiveObject();

                                //Processing Received Data
                                if (receivedObject instanceof Message) {
                                    Message msgObj = (Message) receivedObject;

                                    String msgData = msgObj.getData();

                                    if (msgData.contains("array")) {

                                        String[] msg = msgData.split("-");
                                        int count = Integer.parseInt(msg[1]);
                                        System.out.println("Total Objects: " + count + "\n");

                                        while (count > 0) {

                                            Ticket ticketObj = (Ticket) myClient.receiveObject();
                                            System.out.println(ticketObj.toString());
                                            count--;
                                        }
                                    } else {
                                        System.out.println("\n" + msgData);
                                    }
                                } else if (receivedObject instanceof Ticket) {
                                    Ticket ticketData = (Ticket) receivedObject;
                                    System.out.println(ticketData.toString());
                                }


                                System.out.println("\nDo you want to continue (Y\\N)");
                                String temp = input.next();
                                if (temp.equalsIgnoreCase("N")) {
                                    break;
                                }


                            }
                        }
                    } else {
                        return;
                    }

                }
            }
        }
    }

    public static boolean sendUserData(String username, String password, String operation){
        User newUser = new User(username,password);

        if(!myClient.sendObject(new Message(operation))){
            System.out.println("Error Sending Meta Data");
            return false;
        }

        if(!myClient.sendObject(newUser)){
            System.out.println("Error Sending User Data");
            return false;
        }


        Object receivedObject = myClient.receiveObject();

        Message msgObj = (Message) receivedObject;

        String msgData = msgObj.getData();

        if(msgData.equalsIgnoreCase("success")){
            return true;
        }else if(msgData.equalsIgnoreCase("unsuccessful")){
            System.out.println("\nUsername Already EXIST! Please Enter another Username!");
            return false;
        }else{
            return false;
        }
    }
}

