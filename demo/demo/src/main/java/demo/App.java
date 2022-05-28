package demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 
import java.io.IOException;
import java.io.Writer;

import org.apache.derby.impl.tools.planexporter.AccessDatabase;
import org.apache.derby.impl.tools.planexporter.CreateHTMLFile;
import org.apache.derby.impl.tools.planexporter.CreateXMLFile;

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
        appDerby.testExportPlan();
    }

    public void connectionDerby() throws SQLException {
        // URL format is
        // jdbc:derby:<local directory to save data>
        String dbUrl = "jdbc:derby:Derby;create=true";
        connDerby = DriverManager.getConnection(dbUrl, "root", "root");
    }

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
        
        // drop table
        stmt.executeUpdate("Drop Table test3");
        
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


    public void testExportPlan() throws SQLException {
        Statement stmt = connDerby.createStatement();

        String dbUrl = "jdbc:derby:Derby;create=true";

        //AccessDatabase access = new AccessDatabase(dbUrl, "root", "root");
    }
}
