/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.time.*;
import rmi.RMIInterface;


/**
 *
 * @author Catman
 */
public class Server extends UnicastRemoteObject implements RMIInterface {

    /**
     * @param args the command line arguments
     */
    String username;
    String password;
    String description;
    Statement st;
    Statement st2;
    int flag;
    //Constructor uses the dbConnect method to connect to database

    public Server() throws RemoteException {
        dbConnect();
    }

    @Override
    public boolean libLogin(String libID, String password) throws RemoteException {
        int newID = Integer.parseInt(libID);
        System.out.println("Username: " + libID + newID);
        System.out.println("Password: " + password);
        try {
            
            //Use select statements to compare the username and password in the database
            ResultSet rec = st.executeQuery("SELECT * FROM librarian WHERE "
                    + "librarianID = '" + newID + "' AND librarianPW = "
                    + "'" + password + "'");
            if (rec.next()) {
                return true;
            }
            return false;
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return false;




    }
    @Override
     public boolean memLogin(String userEmail, String password) throws RemoteException {
      //  System.out.println(username + password);
        try {
            // Check in the database if the username and password exists 
            ResultSet rec = st.executeQuery("SELECT * FROM member WHERE "
                    + "memberEmail = '" + userEmail + "' AND memberPW = "
                    + "'" + password + "'");
            return rec.next();
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return false;
    }
     
   private static final String CONN_STRING = "jdbc:mysql://localhost:3306/library_db";
   private static final String PASSWORD = "root";
   private static final String USERNAME = "root";
    
    //Connect to database with dbConnect()

    private void dbConnect() {
        try {
           
            Connection dbConnect = DriverManager.getConnection(CONN_STRING,USERNAME,PASSWORD);
            //Get a statement
            st = dbConnect.createStatement();
            st2 = dbConnect.createStatement();

   
        } catch (SQLException se) {
            se.printStackTrace();
        }


    }
     public boolean adminLogin(String username, String password) {
        System.out.println(username + password);
        try {
            // Check in the database if the username and password exists 
            ResultSet rec = st.executeQuery("SELECT * FROM librarian WHERE "
                    + "librarianName = '" + username + "' AND librarianPW = "
                    + "'" + password + "'");
            return rec.next();
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return false;
    }
    @Override
      public int insertBook(int bookISBN,String bookName,String bookType,int authID, int pubID,String bTitle,String bDesc) {

        int rowsAdded;
        int rowsAdded1;
        try {
            //Check if it exists
           ResultSet rec = st.executeQuery("SELECT bookISBN FROM book WHERE "
                 + "bookISBN = '" + bookISBN + "'");
         //   ResultSet recAuthCheck = st2.executeQuery("SELECT authorID FROM author WHERE "
         //           + "authorID = '" + authID + "'");
          //  ResultSet rec = st2.executeQuery("SELECT b.bookISBN,a.authorID,pu.publisherID FROM book b,author a,publisher pu WHERE "
          //          + "pu.publisherID = '" + pubID + "' AND b.bookISBN = '"+bookISBN +"' AND a.authorID = '"+authID+"'");
          //  rs = st.executeQuery("SELECT b.bookISBN, a.authorID , pu.publisherID "
           //             + "FROM book_author ba "
            //            + "INNER JOIN book b ON b.bookISBN =ba.fk_bookISBN "
            //            + "INNER JOIN author a ON a.authorID = ba.fk_authorID "
             //           + "INNER JOIN publisher pu ON pu.publisherID =b.publisherID WHERE b.bookISBN ='""' ");
          //  boolean bookExist = rec.getBoolean(1);
            
          //  boolean auth = rec.getBoolean(2);
           // boolean pubCheck = rec.getBoolean(3);
           
            if (rec.next()){
                flag = 2;
            }else  if((checkPubAndAuth(pubID,authID))){
                String queryStr1 = "INSERT INTO book(bookISBN,bookName,bookType,publisherID) VALUES(" + bookISBN + ",'"+ bookName + "','" + bookType +"'," + pubID +")";
                String queryStr2 = "INSERT INTO book_author(fk_bookISBN,fk_authorID,title,description) VALUES("+ bookISBN+","+authID+",'" +bTitle+"','" + bDesc + "')";
                
           
                rowsAdded = st.executeUpdate(queryStr1);
                rowsAdded1 = st.executeUpdate(queryStr2);
                System.out.println(rowsAdded + rowsAdded1 + "");
                flag = 3;
                
                   }else {
                // If it does not exist it is added to the database
             //   String queryStr = "INSERT INTO Movie(movie_name, description, genre_id) VALUES ('" + name + "','" + description + "'," + genreID + ")";
                flag = 5;
            }



        } catch (SQLException sql) {
            sql.printStackTrace();
        }

        return flag;

    }
    @Override
       public int borrowBook(int bookISBN,String duration,String memsID) {

        int rowsAdded;
        int rowsAdded1;
        try {
           
         LocalDate date = LocalDate.now();
            date = date.plusDays(Integer.parseInt(duration));
            System.out.println(date);
            //Check if it exists
            ResultSet rec = st.executeQuery("SELECT bookISBN FROM book WHERE "
                    + "bookISBN = '" + bookISBN + "'");

          int memIDs = returnMemID(memsID);
            if (rec.next() && (memIDs != 0)) {
                String queryStr1 = "INSERT INTO member_book(fk_memberID,fk_bookISBN,loanDuration,loanReturn) VALUES(" + memIDs + ",'"+ bookISBN + "','" + duration +"','" + date +"')";
                rowsAdded = st2.executeUpdate(queryStr1);
                flag = 7;
            }else  {
                flag = 5;
            }



        } catch (SQLException sql) {
            sql.printStackTrace();
        }

        return flag;

    }
      @Override
      public int insertMember(int memID,String memName,String memSName,String memNum, String memEmail,String memBDate,String memPW, int libID) {

        int rowsAdded;
        int rowsAdded1;
        try {
            //Check if it exists
            ResultSet rec = st.executeQuery("SELECT memberID FROM member WHERE "
                    + "memberID = '" + memID + "'");
          //  ResultSet recAuthCheck = st2.executeQuery("SELECT authorID FROM author WHERE "
            //        + "authorID = '" + authID + "'");
          //  ResultSet recPubCheck = st2.executeQuery("SELECT publisherID FROM publisher WHERE "
           //         + "publisherID = '" + pubID + "'");
            if (rec.next()) {
                flag = 2;
                   }else {
                // If it does not exist it is added to the database
            
                String queryStr1 = "INSERT INTO member(memberID,memberName,memberSurname,memberNum,memberEmail,memberBdate,memberPW,librarianID) VALUES(" + memID + ",'"+ memName + "','" + memSName +"','" + memNum +"','"+memEmail+"','"+memBDate+"','"+memPW+"',"+libID+")";
         //       String queryStr2 = "INSERT INTO book_author(fk_bookISBN,fk_authorID,title,description) VALUES("+ bookISBN+","+authID+",'" +bTitle+"','" + bDesc + "')";
                
           
                rowsAdded = st.executeUpdate(queryStr1);
       //         rowsAdded1 = st.executeUpdate(queryStr2);
                System.out.println(rowsAdded + "");
                flag = 3;
            }



        } catch (SQLException sql) {
            sql.printStackTrace();
        }

        return flag;

    }
    @Override
       public int deleteBook(int bisbn) {
        int rowsDeleted;
        int rowsDeleted2;

        try {
            //Checks if the movie exists, if it exists its deleted 
            ResultSet rec = st.executeQuery("SELECT bookISBN FROM book WHERE "
                    + "bookISBN = '" + bisbn + "'");
            if (rec.next()) {
                String queryStr2 = "DELETE FROM book_author WHERE fk_bookISBN = " + bisbn + "";
                String queryStr = "DELETE FROM book WHERE bookISBN = " + bisbn + "";
                rowsDeleted = st2.executeUpdate(queryStr2);
                rowsDeleted2 = st2.executeUpdate(queryStr);
                System.out.print(rowsDeleted);
                flag = 3;
            } else {
                // Otherwise it gets a flag of 1 which throws a dialog that states that that movie does not exist
                flag = 6;
            }
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
        return flag;
    }
    @Override
        public int deleteMember(int memID) {
        int rowsDeleted;

        try {
            //Checks if the movie exists, if it exists its deleted 
            ResultSet rec = st.executeQuery("SELECT memberID FROM member WHERE "
                    + "memberID = '" + memID + "'");
            if (rec.next()) {
                String queryStr = "DELETE FROM member WHERE memberID = " + memID + "";
                rowsDeleted = st2.executeUpdate(queryStr);
                System.out.print(rowsDeleted);
                flag = 3;
            } else {
                // Otherwise it gets a flag of 1 which throws a dialog that states that that movie does not exist
                flag = 6;
            }
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
        return flag;
    }


     
    @Override
     public ArrayList<String[]> search(String bookISBN, String bookTitle) {
        ResultSet rs = null;
        ArrayList<String[]> books = new ArrayList<>();

        try {
            System.out.println(bookISBN+ "" + bookTitle+"");
            if (bookISBN.equals("") && bookTitle.equals("")) {
                rs = st.executeQuery("SELECT b.bookISBN,ba.title,b.bookType, a.authorName , pu.publisherName "
                        + "FROM book_author ba "
                        + "INNER JOIN book b ON b.bookISBN =ba.fk_bookISBN "
                        + "INNER JOIN author a ON a.authorID = ba.fk_authorID "
                        + "INNER JOIN publisher pu ON pu.publisherID =b.publisherID");
            } else if (bookISBN.equals("") && bookTitle.matches(bookTitle)) {
                rs = searchTitle(bookTitle);
            } else if (bookTitle.equals("") && bookISBN.matches(bookISBN)) {
                rs = searchISBN(Integer.parseInt(bookISBN));
            } else {
                rs = searchTitleAndISBN(bookTitle, Integer.parseInt(bookISBN));
            }

            while (rs.next()) {
                String[] bookRow = new String[5];
                for (int i = 1; i <= 5; i++) {
                    bookRow[i - 1] = rs.getString(i);
                }
                // Add rows to animals ArrayList object, resembling a table
                books.add(bookRow);
                // null each row to avoid overwriting the previous row
                bookRow = null;
                System.out.println(books.isEmpty()+"");
            }
        } catch (SQLException se) {
            se.printStackTrace();
            System.out.print(se);
        }
        return books;
    }
    @Override
      public ArrayList<String[]> searchLoaned(String memsID) {
        ResultSet rs = null;
        ArrayList<String[]> LoanedBooks = new ArrayList<>();

        try {
            int actualMemID = returnMemID(memsID);
            System.out.println(actualMemID+ "");
            if (memsID.equals("")) {
                System.out.println("An empty member email to return loaned books");
               
            } else {
                 
             //    String query =("SELECT mb.fk_bookISBN,mb.loanDuration,mb.loanReturn,b.bookName FROM member_book mb INNER JOIN book b ON b.bookISBN =mb.fk_bookISBN WHERE fk_memberID ="+actualMemID+"");
                 rs = st.executeQuery("SELECT mb.fk_bookISBN,mb.loanDuration,mb.loanReturn,b.bookName,mb.expired FROM member_book mb INNER JOIN book b ON b.bookISBN =mb.fk_bookISBN WHERE fk_memberID ="+actualMemID+"");
                 while (rs.next()) {
                String[] bookRow = new String[5];
                for (int i = 1; i <= 5; i++) {
                    bookRow[i - 1] = rs.getString(i);
                }
                // Add rows to animals ArrayList object, resembling a table
                LoanedBooks.add(bookRow);
                // null each row to avoid overwriting the previous row
                bookRow = null;
                System.out.println(LoanedBooks.isEmpty()+"");
            }

            
            }
        } catch (SQLException se) {
            se.printStackTrace();
            System.out.print(se);
        }
        return LoanedBooks;
    }
      private ResultSet searchTitle(String title) throws SQLException {
       
        
        ResultSet rec = st.executeQuery("SELECT b.bookISBN,ba.title,b.bookType, a.authorName , pu.publisherName "
                        + "FROM book_author ba "
                        + "INNER JOIN book b ON b.bookISBN =ba.fk_bookISBN "
                        + "INNER JOIN author a ON a.authorID = ba.fk_authorID "
                        + "INNER JOIN publisher pu ON pu.publisherID =b.publisherID WHERE ba.title LIKE '%" + title +"%'");

        return rec;
    }
    // this method is used in the Search method in this class , its searching only for a genre

    private ResultSet searchISBN(int isbn) throws SQLException {
        ResultSet rec2 = st.executeQuery("SELECT b.bookISBN,ba.title,b.bookType, a.authorName , pu.publisherName "
                        + "FROM book_author ba "
                        + "INNER JOIN book b ON b.bookISBN =ba.fk_bookISBN "
                        + "INNER JOIN author a ON a.authorID = ba.fk_authorID "
                        + "INNER JOIN publisher pu ON pu.publisherID =b.publisherID WHERE b.bookISBN LIKE '%" + isbn +"%'");

        return rec2;
    }
    // this method is used in the Search method in this class , its searching for a name and genre

    private ResultSet searchTitleAndISBN(String title, int isbn) throws SQLException {
        ResultSet rec = st.executeQuery("SELECT b.bookISBN,ba.title,b.bookType, a.authorName , pu.publisherName "
                        + "FROM book_author ba "
                        + "INNER JOIN book b ON b.bookISBN =ba.fk_bookISBN "
                        + "INNER JOIN author a ON a.authorID = ba.fk_authorID "
                        + "INNER JOIN publisher pu ON pu.publisherID =b.publisherID WHERE b.bookISBN LIKE '%" + isbn + "%' "
                        + "AND ba.title LIKE '%" + title + "%'");
        

        return rec;
    }
    private int returnMemID(String memEmail) {
        int mID = 0;
        try{
         ResultSet recs = st2.executeQuery("SELECT memberID FROM member WHERE "
                    + "memberEmail = '" + memEmail + "'");
            if (recs.next()) {
                // if it exists the ID of the genre is assigned
                mID = recs.getInt("memberID");
            } else {
                System.out.println("Could not retrieve the members ID");
            }
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
            return mID;
            
    }
    private boolean checkPubAndAuth(int pubID,int authID){
       try{
        ResultSet rec = st.executeQuery("SELECT authorID FROM author WHERE "
                    + "authorID = '" + authID + "'");
        ResultSet rec2 = st2.executeQuery("SELECT publisherID FROM publisher WHERE "
                    + "publisherID = '" + pubID + "'");
        if (rec.next() && rec2.next()){
            return true;
        }else {
            return false;
        }
        
       }catch(SQLException sqle){
           sqle.printStackTrace();
       }
       return false;
    }
     

    
    
}
