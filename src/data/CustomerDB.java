
package data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import business.Customers;
import java.sql.ResultSetMetaData;
import javax.swing.table.DefaultTableModel;

public class CustomerDB {
    public Connection getConnection(){
        Connection con = null;
        try{
            String url = "jdbc:sqlserver://localhost; database = CRM";
            String user = "sa";
            String pass = "123456";
            con = DriverManager.getConnection(url, user, pass);
        }catch(SQLException e){
            System.out.println(e);
        }
        return con;
    }
    
    public boolean addCustomer(Customers c){
        boolean success = false;
        Connection con = getConnection();
        String sqlString = "insert into Customers(CustomerID,CustomerName,DateOfJoin,Revenue)"
                          +"values(?,?,?,?)";
        try{
            PreparedStatement ps = con.prepareStatement(sqlString);
            ps.setInt(1, c.getCustomerID());
            ps.setString(2, c.getCustomerName());
            ps.setString(3, c.getDateOfJoin());
            ps.setDouble(4, c.getRevenue());
            ps.executeUpdate();
            success = true;
            con.close();
        }catch(SQLException e){
            System.out.println(e);
        }
        
        return success;
    }
    
    public boolean updateCustomer(Customers c,int oldID){
        boolean success = false;
        Connection con = getConnection();
        String sqlString = "update Customers set CustomerID = ?,CustomerName = ?,"
                          +"DateOfJoin = ?,Revenue = ?  where CustomerID =?";
        try{
            PreparedStatement ps = con.prepareStatement(sqlString);         
            ps.setInt(1, c.getCustomerID());
            ps.setString(2, c.getCustomerName());
            ps.setString(3, c.getDateOfJoin());
            ps.setDouble(4, c.getRevenue());
            ps.setInt(5, oldID);
            ps.executeUpdate();
            success = true;
            con.close();
        }catch(SQLException e){
            System.out.println(e);
        }                      
        return success;
    }
    public boolean deleteCustomer(int deleteID){
        boolean success = false;
        Connection con = getConnection();
        String sqlString = "delete from Customers where CustomerID =?";
        try{
            PreparedStatement ps = con.prepareStatement(sqlString);
            ps.setInt(1, deleteID);
            ps.executeUpdate();
            success = true;
            con.close();
        }catch(SQLException e){
            System.out.println(e);
        }                        
        return success;
    }
    
    public Customers getFirstCustomer(){
        Connection con = getConnection();
        Customers c = new Customers();         
        String sqlString = "select * from Customers";
        try{
            PreparedStatement ps = con.prepareStatement(sqlString, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);                
            ResultSet rs = ps.executeQuery();
            // move cursor to first customer
                rs.first();
                c.setCustomerID(rs.getInt("CustomerID"));
                c.setCustomerName(rs.getString("CustomerName"));
                c.setDateOfJoin(rs.getString("DateOfJoin"));
                c.setRevenue(rs.getDouble("Revenue"));
                rs.close();
                con.close();           
        }catch(SQLException e){
            System.out.println(e);
        }
        return c;
    }
    public Customers getLastCustomer(){
        Connection con = getConnection();
        Customers c = new Customers();         
        String sqlString = "select * from Customers";
        try{
            PreparedStatement ps = con.prepareStatement(sqlString, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY); 
            ResultSet rs = ps.executeQuery();
            // move cursor to last customer
                rs.last();
                c.setCustomerID(rs.getInt("CustomerID"));
                c.setCustomerName(rs.getString("CustomerName"));
                c.setDateOfJoin(rs.getString("DateOfJoin"));
                c.setRevenue(rs.getDouble("Revenue"));
                rs.close();
                con.close();           
        }catch(SQLException e){
            System.out.println(e);
        }
        return c;
    }
    public Customers getNextCustomer(String currentID){
        Customers lastCustomer = getLastCustomer();
        int id = lastCustomer.getCustomerID();
        String lastID = Integer.toString(id);
        Connection con = getConnection();
        Customers c = new Customers();         
        String sqlString = "select * from Customers";
        try{
            PreparedStatement ps = con.prepareStatement(sqlString, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = ps.executeQuery(); 
            //move cursor to first customer
            rs.first();
            // use loop to move cursor to current customer
            while(!rs.getString("CustomerID").equalsIgnoreCase(currentID)){
                rs.next();  
                // break loop if cursor is last customer
                if(rs.getString("CustomerID").equalsIgnoreCase(lastID)){                   
                    break;
                }
            }
            //move to first if cursor is last customer
            if(rs.getString("CustomerID").equalsIgnoreCase(lastID)){                   
                   rs.first();
            }else {
                rs.next();
            }
            c.setCustomerID(rs.getInt("CustomerID"));
            c.setCustomerName(rs.getString("CustomerName"));
            c.setDateOfJoin(rs.getString("DateOfJoin"));
            c.setRevenue(rs.getDouble("Revenue"));
            rs.close();
            con.close();
            
        }catch(SQLException e){
            System.out.println(e);
        }
        return c;
    }
    public Customers getPreviousCustomer(String currentID){
        Customers firstCustomer = getFirstCustomer();
        int id = firstCustomer.getCustomerID();
        String firstID = Integer.toString(id);
        Connection con = getConnection();
        Customers c = new Customers();         
        String sqlString = "select * from Customers";
        try{
            PreparedStatement ps = con.prepareStatement(sqlString, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = ps.executeQuery();
            // move cursor to first customer
            rs.first();
            // use loop to move cursor to current customer
            while(!rs.getString("CustomerID").equalsIgnoreCase(currentID)){
                rs.next();  
                // break loop if cursor is first customer
                if(rs.getString("CustomerID").equalsIgnoreCase(firstID)){                   
                    break;
                }
            }
            //move to last if cursor is first customer
            if(rs.getString("CustomerID").equalsIgnoreCase(firstID)){                   
                   rs.last();
            }else {
                rs.previous();
            }
            c.setCustomerID(rs.getInt("CustomerID"));
            c.setCustomerName(rs.getString("CustomerName"));
            c.setDateOfJoin(rs.getString("DateOfJoin"));
            c.setRevenue(rs.getDouble("Revenue"));
            rs.close();
            con.close();          
        }catch(SQLException e){
            System.out.println(e);
        }
        return c;
    }
    public DefaultTableModel searchCustomer(String searchString){
        DefaultTableModel customersTableModel = new DefaultTableModel();       
        Connection con = getConnection();
        String sqlString = "select * from Customers where CustomerName like '%"+searchString+"%'";
        try{
            PreparedStatement ps = con.prepareStatement(sqlString);           
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsMetaData = rs.getMetaData();
            // add column name to table model
            int columnsCount = rsMetaData.getColumnCount();
            String[] columnsName = new String[columnsCount];
            for(int i = 1;i<=columnsCount;i++){
                columnsName[i-1] = rsMetaData.getColumnName(i);
            }
            customersTableModel.setColumnIdentifiers(columnsName);
            // add row to table model
            while(rs.next()){
                String rows[] = new String[columnsCount];
                for(int i = 1;i<=columnsCount;i++){
                    rows[i-1] = rs.getString(i);
                }              
                customersTableModel.addRow(rows);               
            }
            rs.close();
            con.close();
        }catch(SQLException e){
            System.out.println(e);
        }
        return customersTableModel;
    }

//end    
}
