package nutrisci.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import io.github.cdimascio.dotenv.Dotenv;

public class CSVToDBImporter {
    private final String dbUrl;
    private final String user;
    private final String password;
    private final int batchSize = 1000;

    public CSVToDBImporter(String dbUrl, String user, String password) {
        this.dbUrl = dbUrl;
        this.user = user;
        this.password = password;
    }

    public void importCSV(String csvPath) throws Exception {
        String tableName = getTableNameFromPath(csvPath);
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath));
             Connection conn = DriverManager.getConnection(dbUrl, user, password)) {
            conn.setAutoCommit(false);
            String headerLine = br.readLine();
            if (headerLine == null) throw new IOException("CSV is empty: " + csvPath);
            String[] columns = headerLine.split(",");
            String insertSQL = buildInsertSQL(tableName, columns);
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                String line;
                int count = 0;
                while ((line = br.readLine()) != null) {
                    String[] values = parseCSVLine(line, columns.length);
                    for (int i = 0; i < columns.length; i++) {
                        pstmt.setString(i + 1, values[i]);
                    }
                    pstmt.addBatch();
                    if (++count % batchSize == 0) {
                        pstmt.executeBatch();
                    }
                }
                pstmt.executeBatch();
                conn.commit();
                System.out.println("Imported " + count + " rows into " + tableName);
            }
        }
    }

    private String getTableNameFromPath(String path) {
        String file = path.substring(path.lastIndexOf('/') + 1);
        int dot = file.lastIndexOf('.');
        return (dot > 0) ? file.substring(0, dot).replaceAll(" ", "_") : file.replaceAll(" ", "_");
    }

    private String buildInsertSQL(String table, String[] columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(table).append(" (");
        for (int i = 0; i < columns.length; i++) {
            sb.append(columns[i]);
            if (i < columns.length - 1) sb.append(",");
        }
        sb.append(") VALUES (");
        for (int i = 0; i < columns.length; i++) {
            sb.append("?");
            if (i < columns.length - 1) sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }

    private String[] parseCSVLine(String line, int expectedCols) {
        String[] parts = line.split(",", -1);
        if (parts.length < expectedCols) {
            String[] padded = new String[expectedCols];
            System.arraycopy(parts, 0, padded, 0, parts.length);
            Arrays.fill(padded, parts.length, expectedCols, "");
            return padded;
        }
        return parts;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java CSVToDBImporter <csv-file-path>");
            System.exit(1);
        }
        Dotenv dotenv = Dotenv.load();
        String dbUrl = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");
        CSVToDBImporter importer = new CSVToDBImporter(dbUrl, user, password);
        importer.importCSV(args[0]);
    }
} 