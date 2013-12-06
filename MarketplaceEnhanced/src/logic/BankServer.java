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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;


import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class BankServer extends UnicastRemoteObject implements BankInterface {

    private List<ClientInterface> clientTable = new ArrayList<ClientInterface>();
    private List<Integer> accountsTable = new ArrayList<Integer>();
    private ClientInterface clientInterface;
    private MyClient client;
    private PreparedStatement createClientStatement;
    private PreparedStatement findClientStatement;
    private Statement deleteClientStatement;
    private Statement checkAccountStatement;
    private String amount = "";

    public BankServer(String port, String serverValue) throws IOException, RemoteException {
        super();
        System.out.println("Bank console");
        LocateRegistry.createRegistry(Integer.parseInt(port));
        Naming.rebind("rmi://localhost:" + serverValue + "/bank", this);
        Connection connection = null;
        try {
            connection = createDatasource();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(BankServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            prepareStatements(connection);
        } catch (SQLException ex) {
            Logger.getLogger(BankServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<ClientInterface> getClients() {
        return (clientTable);
    }

    public void registerClient(ClientInterface client) throws RemoteException {
        try {
            ResultSet result = null;
            findClientStatement.setString(1, client.getName());
            result = findClientStatement.executeQuery();
            if (result.next()) {
                System.out.println("Client already registered in the Bank");
            } else {
                createClientStatement.setString(1, client.getName());
                createClientStatement.setString(2, "10000");
                createClientStatement.executeUpdate();
            }
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void unregisterClient(ClientInterface client) throws RemoteException {
        ResultSet result = null;
        try {
            findClientStatement.setString(1, client.getName()); 
            result = findClientStatement.executeQuery();
            if (result.next()) {
                String lsSQL = "DELETE FROM LOGIN_BANK WHERE name = '" + client.getName() + "'";
                deleteClientStatement.executeUpdate(lsSQL);
                result.next();
                result.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws IOException {
        try {
            String portValue = JOptionPane.showInputDialog("Please input a port value");
            String serverValue = JOptionPane.showInputDialog("Please input the Server port value");
            new BankServer(portValue, serverValue);
            System.out.println("creation of the bank ok");
            new ServerWindow(400, 400, portValue, "Bank Server");
        } catch (RemoteException re) {
            System.out.println(re);
            System.exit(1);
        } catch (MalformedURLException me) {
            System.out.println(me);
            System.exit(1);
        }
    }

    public void creditAccount(String owner, int sum) throws RemoteException {
        ResultSet result = null;
        String amount = null;
        String total = null;
        try {
            findClientStatement.setString(1, owner);
            result = findClientStatement.executeQuery();
            if (result.next()) {              
                amount = checkAccount(owner);
                System.out.println(amount);
                total = Integer.toString(sum + Integer.parseInt(amount));
                String lsSQL2 = ("UPDATE LOGIN_BANK SET amount = '" + total + "' WHERE NAME = '" + owner + "'");
                checkAccountStatement.executeUpdate(lsSQL2);
                result.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void debitAccount(String buyer, int sum) throws RemoteException {
        ResultSet result = null;
        String amount = null;
        String total = null;
        try {
            findClientStatement.setString(1, buyer);
            result = findClientStatement.executeQuery();
            if (result.next()) { 
                amount = checkAccount(buyer);
                System.out.println(amount);
                total = Integer.toString(Integer.parseInt(amount) - sum);
                String lsSQL2 = ("UPDATE LOGIN_BANK SET amount = '" + total + "' WHERE NAME = '" + buyer + "'");
                checkAccountStatement.executeUpdate(lsSQL2);
                result.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String checkAccount(String name) throws RemoteException {
        ResultSet result = null;
        try {
            findClientStatement.setString(1, name);
            result = findClientStatement.executeQuery();
            if (result.next()) {
                String lsSQL = "SELECT AMOUNT FROM LOGIN_BANK WHERE NAME = '" + name + "'";
                result = checkAccountStatement.executeQuery(lsSQL);
                result.next();
                amount = result.getString(1);
                result.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return amount;
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.derby.jdbc.ClientXADataSource");
        return DriverManager.getConnection("jdbc:derby://localhost:1527/Database;create=true");
    }

    private void prepareStatements(Connection connection) throws SQLException {
        createClientStatement = connection.prepareStatement("INSERT INTO LOGIN_BANK VALUES (?, ?)");
        findClientStatement = connection.prepareStatement("SELECT * from LOGIN_BANK WHERE NAME = ?");
        deleteClientStatement = connection.createStatement();
        checkAccountStatement = connection.createStatement();
    }

    private Connection createDatasource() throws ClassNotFoundException, SQLException {
        Connection connection = getConnection();
        boolean exist = false;
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connection.getMetaData();
        for (ResultSet rs = dbm.getTables(null, null, null, null); rs.next();) {
            if (rs.getString(tableNameColumn).equals("LOGIN_BANK")) {
                exist = true;
                rs.close();
                break;
            }
        }
        if (!exist) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE " + "LOGIN_BANK"
                    + " (name VARCHAR(32) PRIMARY KEY, amount VARCHAR(32))");
        }
        return connection;
    }

}
