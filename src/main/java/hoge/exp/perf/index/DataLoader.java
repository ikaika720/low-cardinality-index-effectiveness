package hoge.exp.perf.index;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class DataLoader {
    private static final int DEFAULT_NUM_OF_RCD = 100_000;
    private static final String DEFAULT_DB_URL = "jdbc:postgresql:perf_index";
    private static final String DEFAULT_DB_USER = "user1";
    private static final String DEFAULT_DB_PASSWD = "password";
    private static final int DEFAULT_NUM_OF_INSERT_LOOP = 1;
    private static final int DEFAULT_NUM_OF_SELECT_LOOP = 1000;
    private static final int DEFAULT_FLAGGED_RATIO = 1000;
    private static final String[] DEFAULT_TABLES = new String[] {
        "without_index",
        "with_index_flag",
        "with_index_flag_id"
    };

    public static void main(String[] args) {
        int numOfRecordToInsert = args.length > 0
                ? Integer.parseInt(args[0]) : DEFAULT_NUM_OF_RCD;
        String databaseURL = args.length > 1 ? args[1] : DEFAULT_DB_URL;
        String user = args.length > 2 ? args[2] : DEFAULT_DB_USER;
        String password = args.length > 3 ? args[3] : DEFAULT_DB_PASSWD;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection conn = DriverManager.getConnection(
                databaseURL, user, password)) {
            conn.setAutoCommit(false);

            long duration;
            Map<String, Long> total = new HashMap<>();
            Stream.of(DEFAULT_TABLES).forEach(t -> total.put(t, 0L));
            for (int i = 0; i < DEFAULT_NUM_OF_INSERT_LOOP; i++ ) {
                for (String table : DEFAULT_TABLES) {
                    truncate(conn, table);

                    duration = insert(conn, table, numOfRecordToInsert);
                    total.put(table, total.get(table) + duration);
                    System.out.println(String.format("insert %s %,d ms", table, duration));
                }
            }
            System.out.println();
            List<Entry<String, Long>> list = new ArrayList<Entry<String, Long>>(total.entrySet());
            Collections.sort(list, new Comparator<Entry<String, Long>>() {
                @Override
                public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            });
            list.forEach(e -> {
                System.out.println(String.format("%s %,.2f ms", e.getKey(), ((double) e.getValue() / (double) DEFAULT_NUM_OF_INSERT_LOOP)));
            });
            System.out.println();

            for (String table : DEFAULT_TABLES) {
                duration = select(conn, table);
                System.out.println(String.format("select %s %,d ms", table, duration));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void truncate(Connection conn, String table)
            throws SQLException {
        conn.createStatement().executeUpdate("TRUNCATE TABLE " + table);
    }

    private static long insert(Connection conn, String table,
            int numOfRecordToInsert) throws SQLException {
        long start = System.currentTimeMillis();

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO " + table +
                    " VALUES(?, ?, ?, ?, ?, ?, current_timestamp, current_timestamp)");
        for (int i = 0; i < numOfRecordToInsert; i++) {
            ps.setInt(1, i);
            ps.setShort(2, i % DEFAULT_FLAGGED_RATIO == 0 ? (short) 1 : 0);
            ps.setString(3, Integer.toString(i));
            ps.setString(4, Integer.toString(i));
            ps.setString(5, Integer.toString(i));
            ps.setString(6, Integer.toString(i));

            ps.addBatch();

            if (i % 1000 == 999) {
                ps.executeBatch();
                conn.commit();
            }
        }
        ps.executeBatch();
        conn.commit();

        return System.currentTimeMillis() - start;
    }

    private static long select(Connection conn, String table)
            throws SQLException {
        long start = System.currentTimeMillis();

        PreparedStatement ps = conn.prepareStatement(
                "SELECT id, flag, data1, data2, data3, data4, created, updated FROM " + table + " WHERE flag = '1'");
        for (int i = 0; i < DEFAULT_NUM_OF_SELECT_LOOP; i++) {
            ps.executeQuery();
        }
        conn.commit();

        return System.currentTimeMillis() - start;
    }

}
