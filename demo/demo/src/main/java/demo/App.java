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
    String dbUrl = "jdbc:derby:Derby;create=true";
    
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

    //https://db.apache.org/derby/docs/10.6/tuning/ctun_xplain_style.html
    //https://performancetestexpert.wordpress.com/category/derby/
    public void testExportPlan() throws SQLException {
        Statement stmt = connDerby.createStatement();

        //turn on RUNTIMESTATISTICS for connection:
        stmt.execute("CALL SYSCS_UTIL.SYSCS_SET_RUNTIMESTATISTICS(1)");
        stmt.execute("CALL SYSCS_UTIL.SYSCS_SET_STATISTICS_TIMING(1)");
        //Indicate that statistics information should be captured into
        stmt.execute("CALL SYSCS_UTIL.SYSCS_SET_XPLAIN_SCHEMA('MYSCHEMA')");
        //stmt.execute("call SYSCS_UTIL.SYSCS_SET_XPLAIN_MODE(1)");
        //stmt.execute("call syscs_util.syscs_set_xplain_mode(1)");

        //execute queries, step through result sets, perform application processing

        // drop table
        stmt.executeUpdate("Drop Table test1");

        // create table
        stmt.executeUpdate("Create table test1 (id int primary key, name varchar(30))");
        
        // insert 10,000 rows
        for(int i = 1; i <= 10; i++){
            stmt.executeUpdate("insert into test1 values ("+i+",'tom')");
        }
        
        // query
        stmt.executeQuery("SELECT * FROM test1");

        //Turn off runtime statistics:
        stmt.execute("VALUES SYSCS_UTIL.SYSCS_GET_RUNTIMESTATISTICS()");
        stmt.execute("CALL SYSCS_UTIL.SYSCS_SET_RUNTIMESTATISTICS(0)");

        //query
        ResultSet rs = stmt.executeQuery("select stmt_text, xplain_time from myschema.sysxplain_statements order by xplain_time");
    
        // print out query result
        while (rs.next()) {
            System.out.printf("%s\t%s\n", rs.getString("stmt_text"), rs.getTime("xplain_time"));
        }

        
    }
}
