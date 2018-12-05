package sample;

import java.sql.*;

public class Db {
    public static String fname, lname, accNum, balance, bid, blocation, ifsc, phone;
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    public static void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/NetBanking", "sudarshan", "12345");
            con.setAutoCommit(false);
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            System.out.println("connected to database");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static boolean findBranch(String bid) {
        try {
            rs = stmt.executeQuery("select  * from branch where bid = '" + bid + "';");
            if (rs.next()) {
                blocation = rs.getString("blocation");
                ifsc = rs.getString("ifsc");
                phone = rs.getString("phone");
                return true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    public static boolean findUser(String accNo, String password) {
        try {
            rs = stmt.executeQuery("select  * from Users where accNo ='" + accNo + "' && password= '" + password + "';");
            if (rs.next()) {
                fname = rs.getString("fname");
                lname = rs.getString("lname");
                accNum = rs.getString("accNo");
                balance = rs.getString("balance");
                bid = rs.getString("bid");
                findBranch(bid);
                return true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return false;
    }

    public static String getBalance() {
        try {
            rs = stmt.executeQuery("select  balance from Users where accNo ='" + accNum + "';");
            rs.next();
            balance = rs.getString("balance");
            return balance;
        } catch (Exception e) {
            System.out.println(e);
        }
        return "0";
    }

    //function to write the transactions in the database
    public static boolean writeTransaction(String accNo1, String accNo2, String description, String amount) {
        try {
            String query = "INSERT INTO transaction(accNo, acc2No, description, amount) VALUES(" + accNo1 + "," + accNo2 + ",'" + description + "'," + amount + ");";
            int result = stmt.executeUpdate(query);
            if (result > 0)
                return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
        return false;
    }

    public static boolean checkValidAmount(String num) {
        try {
            if (Integer.parseInt(num) < 0)
                return false;
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static int sendMoney(String accNo, String amount) {
        amount = amount.replaceAll("\\s", "");
        try {
            if (!checkValidAmount(amount))
                return 5;
            if (Integer.parseInt(balance) - Integer.parseInt(amount) > 500) {
                if (!accNo.equals(accNum)) {
                    int rs = stmt.executeUpdate("update Users set balance = balance +" + amount + " where accNo =" + accNo + ";");
                    if (rs > 0) {
                        stmt.executeUpdate("update Users set balance = balance -" + amount + " where accNo =" + accNum + ";");
                        if (!writeTransaction(accNum, accNo, "SEND", amount)) {
                            con.rollback();
                            return -1;
                        }
                        if (!writeTransaction(accNo, accNum, "RECEIVE", amount)) {
                            con.rollback();
                            return -1;
                        }
                        con.commit();
                    } else {
                        con.rollback();
                        return 4;
                    }
                } else
                    return 3;
            } else
                return 2;
        } catch (Exception e) {
            System.out.println(e);
        }
        return 1;
    }

    public static String[] getTransactions(){
        String query = "select * from transaction where accNo = " + accNum + " order by time desc LIMIT 10;";
        String transactions[] = new String[10], acc1NO, acc2No, description, amount, time;
        int i = 0;
        try{
            rs = stmt.executeQuery(query);
            while (rs.next())
            {
                acc1NO = rs.getString("accNo");
                acc2No = rs.getString("acc2No");
                description = rs.getString("description");
                amount = "Rs." + rs.getString("amount");
                time = rs.getString("time");
                transactions[i++] = acc1NO + "\t\t" + description + "\t\t" + acc2No + "\t\t" + amount+ "\t\t" + time;
            }
            return  transactions;
        }
        catch (SQLException se)
        {
            System.out.println(se);
            return transactions;
        }
    }
}