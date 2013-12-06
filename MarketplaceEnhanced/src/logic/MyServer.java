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
import java.util.UUID;

import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class MyServer extends UnicastRemoteObject implements ServerInterface {

    private List<ClientInterface> connectedClientsTable = new ArrayList<ClientInterface>();
    private List<Item> itemToSellTable = new ArrayList<Item>();
    private ClientInterface clientInterface;
    private WishInterface wishInterface;
    private MyClient client;
    private boolean connectedToBank = false;
    private BankInterface bank;
    private String port;
    private String pwd;
    private PreparedStatement createClientStatement;
    private PreparedStatement createClientLogStatement;
    private PreparedStatement findClientStatement;
    private PreparedStatement findWishStatement;
    private PreparedStatement findWishStatement2;
    private PreparedStatement findItemsStatement;
    private PreparedStatement createNotifsStatement;
    private PreparedStatement findNotifsStatement;
    private PreparedStatement createWishStatement;
    private PreparedStatement findWisherStatement;
    private PreparedStatement findWishStatement3;
    private Statement deleteClientStatement;
    private Statement deleteItemStatement;
    private Statement deleteNotifsStatement;
    private Statement getPasswordStatement;
    private Statement getItemsStatement;
    private Statement updateLogStatement;
    private Statement getLogStatement;
    private Statement deleteWishStatement;
    private PreparedStatement createItemStatement;
    public int size = 0;
    public int counter = 0;

    public MyServer(String inputPort) throws IOException, RemoteException, ClassNotFoundException, SQLException {
        super();
        setPort(inputPort);
        LocateRegistry.createRegistry(Integer.parseInt(port));
        //String[] command = new String[]{"rmiregistry",port};
        //Runtime.getRuntime().exec(command);
        Naming.rebind("rmi://localhost:" + port + "/server", this);
        Connection connectionLogin = createLoginTable();
        Connection connectionItems = createItemsTable();
        Connection connectionLog = createLogTable();
        Connection connectionNotifs = createNotifsTable();
        Connection connectionWish = createWishTable();
        prepareStatements(connectionLogin);
        prepareStatementsItems(connectionItems);
        prepareStatementsLog(connectionLog);
        prepareStatementsNotifs(connectionNotifs);
        prepareStatementsWish(connectionWish);

    }

    public List<ClientInterface> getClients() {
        return (connectedClientsTable);
    }

    public void registerClient(ClientInterface client, String password) throws RemoteException, RejectedException {
        try {
            ResultSet result = null;
            findClientStatement.setString(1, client.getName());
            result = findClientStatement.executeQuery();
            if (result.next()) {
                client.alreadyExistsPopUp();       
            } else if (password.length() < 8) {
                client.passwordInvalidPopUp();
            } else {
                createClientStatement.setString(1, client.getName());
                createClientStatement.setString(2, password);
                createClientStatement.executeUpdate();
                createClientLogStatement.setString(1, client.getName());
                createClientLogStatement.setInt(2, 0);
                createClientLogStatement.setInt(3, 0);
                createClientLogStatement.executeUpdate();
                client.registrationOKPopUp();
            }
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateLogTable(ClientInterface client, String owner) throws RemoteException {
        try {
            ResultSet result = null;
            String lsSQL;
            findClientStatement.setString(1, client.getName());
            result = findClientStatement.executeQuery();
            if (result.next()) {
                lsSQL = ("UPDATE LOG SET items_bougth = items_bougth + 1 WHERE NAME = '" + client.getName() + "'");
                updateLogStatement.executeUpdate(lsSQL);
                result.next();
                result.close();
            }
            findClientStatement.setString(1, owner);
            result = findClientStatement.executeQuery();
            if (result.next()) {
                lsSQL = ("UPDATE LOG SET items_sold = items_sold + 1 WHERE NAME = '" + owner + "'");
                updateLogStatement.executeUpdate(lsSQL);
                result.next();
                result.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean checkClient(ClientInterface client, String inputPwd) throws RemoteException {
        boolean checkValid = false;
        ResultSet result = null;
        try {
            findClientStatement.setString(1, client.getName());
            result = findClientStatement.executeQuery();
            if (result.next()) {
                String lsSQL = "SELECT password FROM LOGIN_SERVER WHERE NAME = '" + client.getName() + "'";
                String pwd = "";
                result = getPasswordStatement.executeQuery(lsSQL);
                result.next();
                pwd = result.getString(1);
                result.close();
                
                if (pwd.equals(inputPwd)) {
                    checkValid = true;
                    if (!(getClients().contains(client)))
                        getClients().add(client);
                    checkNotifs(client);
                    if (!connectedToBank) {
                        setBank(client.getBank());
                        connectedToBank = true;
                    }
                } else {
                    client.wrongPasswordPopUp();
                    checkValid = false;
                }
            } else {
                client.clientNotRegisteredPopUp();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return checkValid;
    }

    public void checkNotifs(ClientInterface client) throws RemoteException, SQLException {
        ResultSet result = null;
        String lsSQL;
        String operation;
        String id;
        String nameClient;
        String idWish;
        findNotifsStatement.setString(1, client.getName());
        result = findNotifsStatement.executeQuery();
        while (result.next()) {
            operation = result.getString(2);
            id = result.getString("idItem");
            nameClient = result.getString("name");
            if (operation.equals("Item sold")){
                client.itemSold();
                client.removeItemSold(id);
                lsSQL = "DELETE FROM NOTIFS WHERE idItem = '" + id + "'";
                deleteClientStatement.executeUpdate(lsSQL);
            } else if (operation.equals("Wish available")) {
                client.wishAvailable();
                lsSQL = "DELETE FROM NOTIFS WHERE idItem = '" + id + "'";
                deleteClientStatement.executeUpdate(lsSQL);
            }
            
            //result.next();
        }
    }

    private BankInterface getBank() {
        return bank;
    }

    public void setBank(BankInterface bank) {
        this.bank = bank;
    }

    public void logOutClient(ClientInterface client) throws RemoteException {
        getClients().remove(client);
    }

    public void unregisterClient(ClientInterface client) throws RemoteException {
        ResultSet result = null;
        try {
            logOutClient(client);
            findClientStatement.setString(1, client.getName());
            result = findClientStatement.executeQuery();
            if (result.next()) {
                String lsSQL = "DELETE FROM LOGIN_SERVER WHERE name = '" + client.getName() + "'";
                deleteClientStatement.executeUpdate(lsSQL);
                result.next();
                String lsSql2 = "DELETE FROM ITEMS where owner = '" + client.getName() + "'";
                deleteClientStatement.executeUpdate(lsSql2);
                result.next();
                result.close();

            }
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ClientInterface getClient() {
        return client;
    }

    public void setClient(ClientInterface clientInterface) {
        this.clientInterface = clientInterface;
    }

    public void removeItemToSell(String id) {
        
        ResultSet result = null;
        try {
            findItemsStatement.setString(1, id);
            result = findClientStatement.executeQuery();
            if (result.next()) {
                String lsSQL = "DELETE FROM ITEMS WHERE id = '" + id + "'";
                deleteItemStatement.executeUpdate(lsSQL);
                result.next();
                result.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int getItemToSellTable() throws RemoteException {
        ResultSet result2 = null;
        try {
            result2 = getItemsStatement.executeQuery("SELECT COUNT(*) FROM ITEMS");
            result2.next();
            size = result2.getInt(1);
            System.out.println("Size : " + size);
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return size;
    }

    public String[][] readLineFromItemTable(int size) throws RemoteException {
        String[][] tab = new String[size][6];
        ResultSet result = null;
        String lsSQL = "SELECT * FROM ITEMS";
        try {
            result = getItemsStatement.executeQuery(lsSQL);
            result.next();
            for (int i = 0; i < size; i++) {
                tab[i][0] = result.getString("name");
                tab[i][1] = result.getString("owner");
                tab[i][2] = result.getString("description");
                tab[i][3] = result.getString("price");
                tab[i][4] = result.getString("id");
                String s = new Boolean(result.getBoolean("onSale")).toString();
                tab[i][5] = s;
                result.next();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }  
        return tab;
    }

    public int[] getLog(ClientInterface client) throws RemoteException {
        int[] tab = new int[2];
        ResultSet result = null;
        String lsSQL = "SELECT * FROM LOG WHERE name = '" + client.getName() + "'";
        try {
            result = getLogStatement.executeQuery(lsSQL);
            result.next();
            tab[0] = result.getInt("items_sold");
            tab[1] = result.getInt("items_bougth");
            System.out.println(tab[0] + " " + tab[1]);
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tab;
    }

    public void addItemToSell(Item item) throws RemoteException {
        try {
            createItemStatement.setString(1, item.getName());
            createItemStatement.setString(2, item.getOwner().getName());
            createItemStatement.setString(3, item.getDescription());
            createItemStatement.setString(4, item.getPrice());
            UUID uuid = item.getId();
            createItemStatement.setString(5, uuid.toString());
            createItemStatement.setBoolean(6, item.getonSale());
            createItemStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public boolean callBack(ClientInterface buyer, String owner, String price, String id) throws RemoteException {
        Iterator<ClientInterface> it = getClients().iterator();
        ClientInterface clients = null;
        
        while (it.hasNext()) {
            clients = it.next();
            if ((clients.getName()).equals(owner)) {
                clients.itemSold();
                clients.removeItemSold(id);
                return true;
            } else {
                try {
                    createNotifsStatement.setString(1, owner);
                    createNotifsStatement.setString(2, "Item sold");
                    createNotifsStatement.setString(3, id);
                    createNotifsStatement.executeUpdate();
                } catch (SQLException ex) {
                    Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
                }

                return false;
            }
        }
        return false;
    }

    public void udpateAccounts(ClientInterface buyer, String owner, String price) throws RemoteException {
        bank.creditAccount(owner, Integer.parseInt(price));
        bank.debitAccount(buyer.getName(), Integer.parseInt(price));
    }

    public boolean checkAccount(ClientInterface buyer, String price) throws RemoteException {
        if (Integer.parseInt(bank.checkAccount(buyer.getName())) < Integer.parseInt(price)) {
            buyer.tooExpensive();
            return false;
        } else {
            return true;
        }
    }

    public void addWish(WishInterface wish) throws RemoteException {
        try {
            ResultSet result = null;
            String lsSQL;
            findWishStatement3.setString(1, wish.getWisher().getName());
            findWishStatement3.setString(2, wish.getNameItem());
            findWishStatement3.setString(3, wish.getPriceItem());
            result = findWishStatement3.executeQuery();
            if (result.next()) {
                System.out.println("avant la popup already exists");
                wish.getWisher().wishAlreadyExistsPopUp();
            } else {
                UUID uuid = wish.getUUID();
                createWishStatement.setString(1, uuid.toString());
                createWishStatement.setString(2, wish.getWisher().getName());
                createWishStatement.setString(3, wish.getNameItem());
                createWishStatement.setString(4, wish.getPriceItem());
                createWishStatement.executeUpdate();
                wish.getWisher().wishRegistered();
            }
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeWish(WishInterface wish) throws RemoteException {

    }

    public List<WishInterface> getWishTable() {
        return null;
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

    public void callBackWish(Item item) throws RemoteException {
        ResultSet result = null;
        String lsSQL;
        Iterator<ClientInterface> it = getClients().iterator();
        ClientInterface clients = null;
        String wisher;
        String id;
        try {
            findWishStatement2.setString(1, item.getName());
            findWishStatement2.setString(2, item.getPrice());
            result = findWishStatement2.executeQuery();
            while (result.next()) {
                wisher = result.getString("nameClient");
                id = result.getString("wishId");
                while (it.hasNext()) {
                    clients = it.next();
                    if ((clients.getName()).equals(wisher)) {
                        clients.wishAvailable();                
                        lsSQL = "DELETE FROM WISH WHERE wishId = '" + id + "'";
                        deleteWishStatement.executeUpdate(lsSQL);
                        //result.next();
                        
                    } else {                       
                        createNotifsStatement.setString(1, wisher);
                        createNotifsStatement.setString(2, "Wish available");
                        createNotifsStatement.setString(3, Integer.toString(counter));
                        createNotifsStatement.executeUpdate();
                        lsSQL = "DELETE FROM WISH WHERE wishId = '" + id + "'";
                        deleteWishStatement.executeUpdate(lsSQL);
                        counter++;
                        //result.next();
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    private Connection createLoginTable() throws ClassNotFoundException, SQLException {
        Connection connectionLogin = getConnection();
        boolean exist = false;
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connectionLogin.getMetaData();
        for (ResultSet rs = dbm.getTables(null, null, null, null); rs.next();) {
            if (rs.getString(tableNameColumn).equals("LOGIN_SERVER")) {
                exist = true;
                rs.close();
                break;
            }
        }
        if (!exist) {
            Statement statement = connectionLogin.createStatement();
            statement.executeUpdate("CREATE TABLE " + "LOGIN_SERVER"
                    + " (name VARCHAR(32) PRIMARY KEY, password VARCHAR(32))");
        }
        return connectionLogin;
    }

    private Connection createItemsTable() throws ClassNotFoundException, SQLException {
        Connection connectionItems = getConnection();
        boolean exist = false;
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connectionItems.getMetaData();
        for (ResultSet rs = dbm.getTables(null, null, null, null); rs.next();) {
            if (rs.getString(tableNameColumn).equals("ITEMS")) {
                exist = true;
                rs.close();
                break;
            }
        }
        if (!exist) {
            Statement statement = connectionItems.createStatement();
            statement.executeUpdate("CREATE TABLE " + "ITEMS"
                    + " (name VARCHAR(32), owner VARCHAR(32), description VARCHAR(32), price VARCHAR(32), id VARCHAR(64) PRIMARY KEY, onSale BOOLEAN)");
        }
        return connectionItems;
    }

    private Connection createLogTable() throws ClassNotFoundException, SQLException {
        Connection connectionLog = getConnection();
        boolean exist = false;
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connectionLog.getMetaData();
        for (ResultSet rs = dbm.getTables(null, null, null, null); rs.next();) {
            if (rs.getString(tableNameColumn).equals("LOG")) {
                exist = true;
                rs.close();
                break;
            }
        }
        if (!exist) {
            Statement statement = connectionLog.createStatement();
            statement.executeUpdate("CREATE TABLE LOG (name VARCHAR(32) PRIMARY KEY, items_sold INT, items_bougth INT)");

        }
        return connectionLog;
    }

    private Connection createWishTable() throws ClassNotFoundException, SQLException {
        Connection connectionWish = getConnection();
        boolean exist = false;
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connectionWish.getMetaData();
        for (ResultSet rs = dbm.getTables(null, null, null, null); rs.next();) {
            if (rs.getString(tableNameColumn).equals("WISH")) {
                exist = true;
                rs.close();
                break;
            }
        }
        if (!exist) {
            Statement statement = connectionWish.createStatement();
            statement.executeUpdate("CREATE TABLE WISH (wishId VARCHAR(64) PRIMARY KEY, nameClient VARCHAR(32), nameItem VARCHAR(32), price VARCHAR(32))");
        }
        return connectionWish;
    }

    private Connection createNotifsTable() throws ClassNotFoundException, SQLException {
        Connection connectionNotifs = getConnection();
        boolean exist = false;
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connectionNotifs.getMetaData();
        for (ResultSet rs = dbm.getTables(null, null, null, null); rs.next();) {
            if (rs.getString(tableNameColumn).equals("NOTIFS")) {
                exist = true;
                rs.close();
                break;
            }
        }
        if (!exist) {
            Statement statement = connectionNotifs.createStatement();
            statement.executeUpdate("CREATE TABLE NOTIFS (name VARCHAR(32), operation VARCHAR(32), idItem VARCHAR(64) PRIMARY KEY)");

        }
        return connectionNotifs;
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.derby.jdbc.ClientXADataSource");
        return DriverManager.getConnection("jdbc:derby://localhost:1527/Database;create=true");
    }

    private void prepareStatementsWish(Connection connectionWish) throws SQLException {
        createWishStatement = connectionWish.prepareStatement("INSERT INTO WISH VALUES (?, ?, ?, ?)");
        findWishStatement = connectionWish.prepareStatement("SELECT * from WISH WHERE nameItem = ? AND price = ?");
        findWisherStatement = connectionWish.prepareStatement("SELECT nameClient from WISH");
        findWishStatement2 = connectionWish.prepareStatement("SELECT * from WISH WHERE nameItem = ? AND price >= ?");
        findWishStatement3 = connectionWish.prepareStatement("SELECT * from WISH WHERE nameClient = ? AND nameItem = ? AND price = ?");
        //findWishStatement4 = connectionWish.prepareStatement("SELECT * from WISH WHERE nameClient = ? AND nameItem = ? AND price = ?");
        deleteWishStatement = connectionWish.createStatement();
    }

    private void prepareStatementsNotifs(Connection connectionNotifs) throws SQLException {
        createNotifsStatement = connectionNotifs.prepareStatement("INSERT INTO NOTIFS VALUES (?, ?, ?)");
        findNotifsStatement = connectionNotifs.prepareStatement("SELECT * from NOTIFS WHERE NAME = ?");
        deleteNotifsStatement = connectionNotifs.createStatement();
    }

    private void prepareStatementsLog(Connection connectionLog) throws SQLException {
        createClientLogStatement = connectionLog.prepareStatement("INSERT INTO LOG VALUES (?, ?, ?)");
        updateLogStatement = connectionLog.createStatement();
        getLogStatement = connectionLog.createStatement();
    }

    private void prepareStatementsItems(Connection connectionItems) throws SQLException {
        createItemStatement = connectionItems.prepareStatement("INSERT INTO ITEMS VALUES (?, ?, ?, ?, ?, ?)");
        getItemsStatement = connectionItems.createStatement();
        findItemsStatement = connectionItems.prepareStatement("SELECT * from ITEMS WHERE NAME = ?");
        deleteItemStatement = connectionItems.createStatement();
    }

    private void prepareStatements(Connection connectionLogin) throws SQLException {
        createClientStatement = connectionLogin.prepareStatement("INSERT INTO LOGIN_SERVER VALUES (?, ?)");
        findClientStatement = connectionLogin.prepareStatement("SELECT * from LOGIN_SERVER WHERE NAME = ?");
        deleteClientStatement = connectionLogin.createStatement();
        getPasswordStatement = connectionLogin.createStatement();

    }

}
