package demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;



/**
 * Hello world!
 *
 */
public class App {
    Connection connDerby;
    Connection connDerby2;
    String dbUrl = "jdbc:derby:Derby;create=true";
    
    public static void main(String[] args) throws SQLException,InterruptedException {
        App appDerby = new App();
        appDerby.connectionDerby();
        //appDerby.testRows();
        //appDerby.testColumns();
        //appDerby.testConstraintName();
        //appDerby.testExportPlan();
        //appDerby.testDirtyRead();
        appDerby.testDeadLock();
    }

    public void connectionDerby() throws SQLException {
        // URL format is
        // jdbc:derby:<local directory to save data>
        connDerby = DriverManager.getConnection(dbUrl, "root", "root");
        connDerby2 = DriverManager.getConnection(dbUrl, "root", "root");
    }

    //MaxRows is unlimitation accoring to disk
    public void testRows() throws SQLException {
        Statement stmt = connDerby.createStatement();

        // drop table
        stmt.executeUpdate("Drop Table test1");

        // create table
        stmt.executeUpdate("Create table test1 (id int primary key, name varchar(30))");

        // insert 10,000 rows
        for(int i = 1; i <= 3; i++){
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


    public void testDirtyRead() throws SQLException {
        //Turn off JDBC setAutoCommit method
        connDerby.setAutoCommit(false);
        
        Statement stmt = connDerby.createStatement();
        // drop table
        stmt.execute("Drop Table BankDetailTbl");
        // create table
        stmt.execute("Create table BankDetailTbl (Id INT PRIMARY KEY, AccountNumber VARCHAR(40), ClientName VARCHAR(100), Balance INT)");
        //Set waittime = 2 second
        //stmt.execute("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.locks.waitTimeout', '2')");
        // insert a data row
        stmt.execute("INSERT INTO BankDetailTbl VALUES (1 , '7Y290394', 'Betty H. Bonds', 78)");
        // commit the above transactions
        connDerby.commit();
        //stmt.close();
        

        Statement stmt2 = connDerby2.createStatement();
        // Update record id = 1
        stmt2.execute("UPDATE BankDetailTbl SET Balance = 45 WHERE AccountNumber = '7Y290394'");
        // Update record id = 1
        stmt2.execute("UPDATE BankDetailTbl SET Balance = 30 WHERE AccountNumber = '7Y290394'");    
        connDerby2.rollback();    
        stmt2.close();

        //Thread.sleep(3000);

        //stmt = connDerby.createStatement();
        // Query-2
        ResultSet rs = stmt.executeQuery("SELECT * FROM BankDetailTbl");
        // print out query result
        while (rs.next()) {
            System.out.printf("%s\t%s\t%s\t%s\t", rs.getInt("id"), rs.getString("AccountNumber"), rs.getString("ClientName"), rs.getInt("Balance"));
        }
    }


    public void testDeadLock() throws SQLException, InterruptedException {
        //Turn off JDBC setAutoCommit method
        connDerby.setAutoCommit(false);
        connDerby2.setAutoCommit(false);
        connDerby.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        
        
        Statement stmt1 = connDerby.createStatement();
        //Set waittime = 2 second
        stmt1.execute("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.locks.waitTimeout', '2')");

        // drop table
        stmt1.execute("Drop Table BankDetailTbl");
        // create table
        stmt1.execute("Create table BankDetailTbl (Id INT PRIMARY KEY, AccountNumber VARCHAR(40), ClientName VARCHAR(100), Balance INT)");
        // insert a data row
        stmt1.execute("INSERT INTO BankDetailTbl VALUES (1 , '7Y290394', 'Betty H. Bonds', 78)");
        connDerby.commit();

        //stmt1.execute("LOCK TABLE BankDetailTbl IN EXCLUSIVE MODE");
        stmt1.execute("LOCK TABLE BankDetailTbl IN SHARE  MODE");
        // commit the above transactions
        //connDerby.commit();
        // stmt1.close();
        

        Statement stmt2 = connDerby.createStatement();
        // Update record id = 1
        stmt2.execute("LOCK TABLE BankDetailTbl IN EXCLUSIVE MODE");
        //stmt2.execute("LOCK TABLE BankDetailTbl IN SHARE  MODE");
        // Update record id = 1
        stmt2.execute("INSERT INTO BankDetailTbl VALUES (2 , '22222', 'Betty H. Bonds', 60)");   
        //connDerby.commit();    
        // stmt.close();

        //Thread.sleep(3000);

        Statement stmt3 = connDerby2.createStatement();
        // Query-2
        //stmt3.execute("DELETE FROM BankDetailTbl WHERE AccountNumber = '7Y290394'");    
        //stmt3.execute("INSERT INTO BankDetailTbl VALUES (3, '22222', 'Betty H. Bonds', 60)"); 
        ResultSet rs = stmt3.executeQuery("SELECT * FROM BankDetailTbl");
        // print out query result
        // connDerby2.commit();  
        // while (rs.next()) {
        //     System.out.printf("%s\t%s\t%s\t%s\n", rs.getInt("id"), rs.getString("AccountNumber"), rs.getString("ClientName"), rs.getInt("Balance"));
        // }
        System.out.println("Success to pass");
    }
}
