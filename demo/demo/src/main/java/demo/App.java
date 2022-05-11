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
    Connection conn;

    public static void main(String[] args) throws SQLException {
        App app = new App();

        app.connectionDerby();
        // app.testRows();
        app.testColumns();
    }

    public void connectionDerby() throws SQLException {
        // URL format is
        // jdbc:derby:<local directory to save data>
        String dbUrl = "jdbc:derby:e:\\vs code project\\vscode_project\\Derby-Limitation\\Data;create=true";
        conn = DriverManager.getConnection(dbUrl, "root", "root");
    }

    public void testRows() throws SQLException {
        Statement stmt = conn.createStatement();

        // drop table
        // stmt.executeUpdate("Drop Table users");

        // create table
        // stmt.executeUpdate("Create table users (id int primary key, name
        // varchar(30))");

        // insert 10,000 rows
        // for(int i = 1; i <= 10000; i++){
        // stmt.executeUpdate("insert into users values ("+i+",'tom')");
        // }

        // query
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS id FROM users");

        // print out query result
        while (rs.next()) {
            int count = rs.getInt("id");
            System.out.println("Table has " + count + " row(s).");
        }
    }

    public void testColumns() throws SQLException {
        Statement stmt = conn.createStatement();

        // drop table
        stmt.executeUpdate("Drop Table columns");

        // create table
        stmt.executeUpdate("Create table columns (id int primary key, column_1 varchar(5))");

        // insert 1,000 columns
        for (int i = 2; i <= 1000; i++) {
            stmt.executeUpdate("ALTER TABLE columns ADD column_" + i + " varchar(2)");
        }
    
        String sqlstring = "1,'tom'";
        for (int i = 2; i <= 1000; i++) {
            sqlstring += ",'t'";
        }
        stmt.executeUpdate("insert into columns values ("+sqlstring+")");

        // query
        ResultSet rs = stmt.executeQuery("SELECT * FROM columns");

        // print out query result
        while (rs.next()) {
            System.out.printf("%d\t%s\n", rs.getInt("id"), rs.getString("column_1"), rs.getString("column_2"), rs.getString("column_3"));
        }
    }
}
