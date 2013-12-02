package logic;

import gui.ServerWindow;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.RejectedException;

import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class MyServer extends UnicastRemoteObject implements ServerInterface {

    private List<ClientInterface> clientTable = new ArrayList<ClientInterface>();
    private List<Item> itemToSellTable = new ArrayList<Item>();
    private List<WishInterface> wishTable = new ArrayList<WishInterface>();
    private ClientInterface clientInterface;
    private WishInterface wishInterface;
    private MyClient client;
    private boolean connectedToBank = false;
    private BankInterface bank;
    private String port;
    private PreparedStatement createClientStatement;
    private PreparedStatement findClientStatement;
    private PreparedStatement deleteClientStatement;

    public MyServer(String inputPort) throws IOException, RemoteException, ClassNotFoundException, SQLException {
        super();
        setPort(inputPort);
        LocateRegistry.createRegistry(Integer.parseInt(port));
		//String[] command = new String[]{"rmiregistry",port};
        //Runtime.getRuntime().exec(command);
        Naming.rebind("rmi://localhost:" + port + "/server", this);
        Connection connection = createDatasource();
        prepareStatements(connection);
    }

    public List<ClientInterface> getClients() {
        return (clientTable);
    }

    public void registerClient(ClientInterface client, char[] password) throws RemoteException, RejectedException{
        try {
            /* if (clientTable.contains(client)) {
            throw new RemoteException("client already registered");
            }
            clientTable.add(client);
            if (!connectedToBank) {
            setBank(client.getBank());
            connectedToBank = true;
            } */
            ResultSet result = null;
            findClientStatement.setString(1, client.getName());
            result = findClientStatement.executeQuery();
            if (result.next()){
                throw new RejectedException("Rejected: Account for: " + client.getName() + " already exists");
            }
            result.close();
            
            if (password.length < 8){
                throw new RejectedException("Rejected: Password must be 8 characters long");
            }
            
            createClientStatement.setString(1, client.getName());
            createClientStatement.setString(2, password.toString());
            
            System.out.println(createClientStatement.toString());
            
            result = createClientStatement.executeQuery();
            System.out.println(result);
            result.close();
                    
                   
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private BankInterface getBank() {
        return bank;
    }

    public void setBank(BankInterface bank) {
        this.bank = bank;
    }

    public void unregisterClient(ClientInterface client) throws RemoteException {
        if (!clientTable.contains(client)) {
            throw new RemoteException("client not registered");
        }
        clientTable.remove(client);
    }

    public ClientInterface getClient() {
        return client;
    }

    public void setClient(ClientInterface clientInterface) {
        this.clientInterface = clientInterface;
    }

    public void removeItemToSell(Item inputItem) {
        int id = inputItem.getId();
        Iterator<Item> it = getItemToSellTable().iterator();
        Item item;
        while (it.hasNext()) {
            item = it.next();
            if (item.getId() == id) {
                getItemToSellTable().remove(item);
                break;
            }
        }
    }

    public void getItemsToSell() {
        Iterator<Item> it = getItemToSellTable().iterator();
        Item item;
        if (getItemToSellTable().size() == 0) {
        }
        while (it.hasNext()) {
            item = it.next();
        }
    }

    public List<Item> getItemToSellTable() {
        return itemToSellTable;
    }

    public void addItemToSell(Item item) {
        getItemToSellTable().add(item);
    }

    public boolean callBack(ClientInterface buyer, ClientInterface owner, Item item) throws RemoteException {

        if (bank.checkAccount(buyer) < Integer.parseInt(item.getPrice())) {
            buyer.tooExpensive();
            return false;
        } else {
            Iterator<ClientInterface> it = getClients().iterator();
            ClientInterface clients = null;
            while (it.hasNext()) {
                clients = it.next();
                int id = item.getId();
                if (clients.equals(owner)) {
                    bank.creditAccount(clients, Integer.parseInt(item.getPrice()));
                    bank.debitAccount(buyer, Integer.parseInt(item.getPrice()));
                    clients.itemSold(item);
                    clients.removeItemSold(item, id);
                    return true;
                }
            }
        }
        return false;
    }

    public void addWish(WishInterface wish) throws RemoteException {
        if (wishTable.contains(wish)) {
            throw new RemoteException("You have already made this wish");
        }
        wishTable.add(wish);
    }

    public void removeWish(WishInterface wish) throws RemoteException {
        if (!(wishTable.contains(wish))) {
            System.out.println("You don't have this wish");
        }
        wishTable.remove(wish);
    }

    public List<WishInterface> getWishTable() {
        return wishTable;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        try {
            String inputValue = JOptionPane.showInputDialog("Please input a port value");
            new MyServer(inputValue);
            System.out.println("creation d'un server ok");
            new ServerWindow(400, 400, inputValue, "Server");
        } catch (RemoteException re) {
            System.out.println(re);
            System.exit(1);
        } catch (MalformedURLException me) {
            System.out.println(me);
            System.exit(1);
        }
    }

    @Override
    public void callBackWish(Item item) throws RemoteException {
        Iterator<WishInterface> it = getWishTable().iterator();
        WishInterface wish = null;
        while (it.hasNext()) {
            wish = it.next();
            if (wish.getNameItem().equals(item.getName()) && Integer.parseInt(item.getPrice()) <= Integer.parseInt(wish.getPriceItem())) {
                wish.getWisher().wishAvailable(item);
            }
        }
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    private Connection createDatasource() throws ClassNotFoundException, SQLException {
        Connection connection = getConnection();
        boolean exist = false;
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connection.getMetaData();
        for (ResultSet rs = dbm.getTables(null, null, null, null); rs.next();) {
            if (rs.getString(tableNameColumn).equals("LOGIN")) {
                exist = true;
                rs.close();
                break;
            }
        }
        if (!exist) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE " + "LOGIN"
                    + " (name VARCHAR(32) PRIMARY KEY, password VARCHAR(32))");
        }
        return connection;
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
    Class.forName ("org.apache.derby.jdbc.ClientXADataSource");
            return DriverManager.getConnection ("jdbc:derby://localhost:1527/Database;create=true");
    }
    
     private void prepareStatements(Connection connection) throws SQLException {
        createClientStatement = connection.prepareStatement("INSERT INTO LOGIN VALUES (?, ?)");
        findClientStatement = connection.prepareStatement("SELECT * from LOGIN WHERE NAME = ?");
        deleteClientStatement = connection.prepareStatement("DELETE FROM LOGIN WHERE name = ?");
    }
        
}
