package logic;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;
import javax.swing.JPasswordField;

public interface ServerInterface extends Remote {

    void registerClient(ClientInterface client, String password) throws RemoteException, RejectedException;

    public void unregisterClient(ClientInterface client) throws RemoteException;

    List<ClientInterface> getClients() throws RemoteException;

    void addItemToSell(Item item) throws RemoteException;

    void removeItemToSell(String name) throws RemoteException;

    int getItemToSellTable() throws RemoteException;

    public ClientInterface getClient() throws RemoteException;

    public boolean callBack(ClientInterface buyer, String owner, String price, String id) throws RemoteException;

    void setBank(BankInterface bank) throws RemoteException;

    void addWish(WishInterface wish) throws RemoteException;

    List<WishInterface> getWishTable() throws RemoteException;

    void removeWish(WishInterface wish) throws RemoteException;

    void callBackWish(Item item) throws RemoteException;
    //BankInterface getBank() throws RemoteException;

    boolean checkClient(ClientInterface client, String inputPwd) throws RemoteException;
    
    void logOutClient(ClientInterface client) throws RemoteException;
    
    String[][] readLineFromItemTable(int size) throws RemoteException;
    
    public boolean checkAccount(ClientInterface buyer, String price) throws RemoteException;
    
    public void udpateAccounts(ClientInterface buyer, String owner, String price) throws RemoteException;

    public void updateLogTable(ClientInterface client, String owner) throws RemoteException;
    
    public int[] getLog(ClientInterface client) throws RemoteException;
}
