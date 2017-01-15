/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Catman
 */
public interface RMIInterface extends Remote {
    public boolean memLogin(String userEmail, String password) throws RemoteException;
    public boolean libLogin(String libID, String password) throws RemoteException;
    public ArrayList<String[]> search(String bookISBN, String bookTitle) throws RemoteException;
      public ArrayList<String[]> searchLoaned(String memID) throws RemoteException;
    public int insertBook(int bookISBN1,String bookName,String bookType,int authorID,int pubID,String bTitle,String bDesc) throws RemoteException;
    public int insertMember(int memID,String memName,String memSName,String memNum, String memEmail,String memBDate,String memPW, int libID) throws RemoteException;
     public int deleteBook(int bisbn) throws RemoteException;
    public int deleteMember(int memID) throws RemoteException;
     public int borrowBook(int bookISBN,String duration,String memsID) throws RemoteException;
}
