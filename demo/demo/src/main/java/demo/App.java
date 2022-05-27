package demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Hello world!
 *
 */
public class App {
    Connection connDerby;
    
    public static void main(String[] args) throws SQLException {
        App appDerby = new App();
        appDerby.connectionDerby();
        //appDerby.testRows();
        //appDerby.testColumns();
        //appDerby.testConstraintName();

        // appMysql.connectionMysql();
        mysqltestRows();
    }

    public void connectionDerby() throws SQLException {
        // URL format is
        // jdbc:derby:<local directory to save data>
        String dbUrl = "jdbc:derby:Derby;create=true";
        connDerby = DriverManager.getConnection(dbUrl, "root", "root");
    }

    // public void connectionMysql() throws SQLException {
    //     // URL format is
    //     // jdbc:derby:<local directory to save data>
    //     try (Connection connMysql = DriverManager.getConnection(
    //         "jdbc:mysql://127.0.0.1:3306/test", "root", "password")) {

    //         if (connMysql != null) {
    //             System.out.println("Connected to the database!");
    //         } else {
    //             System.out.println("Failed to make connection!");
    //         }

    //     } catch (SQLException e) {
    //         System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    //MaxRows is unlimitation accoring to disk
    public void testRows() throws SQLException {
        Statement stmt = connDerby.createStatement();

        // drop table
        stmt.executeUpdate("Drop Table test1");

        // create table
        stmt.executeUpdate("Create table test1 (id int primary key, name varchar(30))");

        // insert 10,000 rows
        for(int i = 1; i <= 10000; i++){
        stmt.executeUpdate("insert into test1 values ("+i+",'tom')");
        }

        // query
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS id FROM test1");

        // print out query result
        while (rs.next()) {
            int count = rs.getInt("id");
            System.out.println("Table has " + count + " row(s).");
        }
    }
    
    //Maximum 1012 columns in a table
    public void testColumns() throws SQLException{
        Statement stmt = connDerby.createStatement();
    
        // drop table
        stmt.executeUpdate("Drop Table test2");
    
        // create table
        stmt.executeUpdate("Create table test2 (id int primary key, column_1 varchar(5))");
    
        // insert 1,000 columns
        for (int i = 2; i <= 2000; i++) {
            stmt.executeUpdate("ALTER TABLE test2 ADD column_" + i + " VARCHAR(5)");
        }
        
        // String sqlstring = "1,'tom'";
        // for (int i = 2; i <= 1000; i++) {
        //     sqlstring += ",'t'";
        // }
        // stmt.executeUpdate("insert into columns values ("+sqlstring+")");
    
        // // query
        // ResultSet rs = stmt.executeQuery("SELECT * FROM columns");
    
        // // print out query result
        // while (rs.next()) {
        //     System.out.printf("%d\t%s\n", rs.getInt("id"), rs.getString("column_1"), rs.getString("column_2"), rs.getString("column_3"));
        // }
    }

    //Maximum 128 characters in a constraint name 
    public void testConstraintName() throws SQLException {
        Statement stmt = connDerby.createStatement();
    
        // create table
        stmt.executeUpdate("Create table test3 (id int primary key, column_1 varchar(5))");

        // create constraint
        String sqlstring = "";
        for (int i = 0; i <=500; i++) {
            sqlstring += 'a';
        }
        stmt.executeUpdate("ALTER TABLE test3 ADD CONSTRAINT " + sqlstring + "PRIMARY KEY (id,column_1);");

        // drop table
        stmt.executeUpdate("Drop Table test3");
    }

    //MaxRows is unlimitation accoring to disk
    public static void mysqltestRows() throws SQLException {
        String createsql = "CREATE TABLE REGISTRATION " +
        "(id INTEGER not NULL, " +
        " first VARCHAR(255), " + 
        " last VARCHAR(255), " + 
        " age INTEGER, " + 
        " PRIMARY KEY ( id ))"; 

        String insertsql = " insert into REGISTRATION (id , first, last, age)"
                         + " values (1, 1, 1, 1)";

        String selectsql = "SELECT * FROM REGISTRATION";

        try(Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "root");
            Statement stmt = conn.createStatement())
        {		     
            stmt.executeQuery(createsql);
            stmt.executeQuery(insertsql);
            ResultSet rs = stmt.executeQuery(selectsql);
            while (rs.next()) {
                System.out.printf("%d\t%s\n", rs.getInt("id"), rs.getString("column_1"), rs.getString("column_2"), rs.getInt("age"));
            }	  
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }
}
