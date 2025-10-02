//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import JDBC.DBConnection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        try {
            DBConnection db = DBConnection.getInstance();
            Statement stmt = db.createStatement();
            ResultSet rs = stmt.executeQuery("DESCRIBE card");

            while (rs.next()) {
                System.out.println(rs.getString(1) + " " + rs.getString(2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}