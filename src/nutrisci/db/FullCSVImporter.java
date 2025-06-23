package nutrisci.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class CSVToDBImporter {
    private final String dbUrl;
    private final String user;
    private final String password;
    private final int batchSize = 1000;

    private final Map<String, String> csvToTableMap = new HashMap<String, String>() {{
        put("FOOD NAME.csv", "FoodDescriptions");
        put("FOOD GROUP.csv", "FoodGroups");
        put("FOOD SOURCE.csv", "FoodSources");
        put("NUTRIENT NAME.csv", "Nutrients");
        put("NUTRIENT SOURCE.csv", "NutrientSources");
        put("NUTRIENT AMOUNT.csv", "NutrientData");
        put("MEASURE NAME.csv", "Measures");
        put("CONVERSION FACTOR.csv", "ConversionFactors");
        put("REFUSE NAME.csv", "RefuseDescriptions");
        put("REFUSE AMOUNT.csv", "RefuseAmounts");
        put("YIELD NAME.csv", "YieldDescriptions");
        put("YIELD AMOUNT.csv", "Yields");
    }};

    private final Map<String, Map<String, String>> columnMappings = new HashMap<String, Map<String, String>>() {{
        put("FoodDescriptions", new HashMap<String, String>() {{
            put("FoodID", "food_id");
            put("FoodCode", "food_code");
            put("FoodGroupID", "food_group_id");
            put("FoodSourceID", "food_source_id");
            put("FoodDescription", "description_en");
            put("FoodDescriptionF", "description_fr");
            put("FoodDateOfEntry", "date_of_entry");
            put("FoodDateOfPublication", "date_of_publication");
            put("CountryCode", "country_code");
            put("ScientificName", "scientific_name");
        }});
        
        put("FoodGroups", new HashMap<String, String>() {{
            put("FoodGroupID", "food_group_id");
            put("FoodGroupCode", "food_group_code");
            put("FoodGroupName", "name_en");
            put("FoodGroupNameF", "name_fr");
        }});
        
        put("FoodSources", new HashMap<String, String>() {{
            put("FoodSourceID", "food_source_id");
            put("FoodSourceCode", "source_code");
            put("FoodSourceDescription", "description_en");
            put("FoodSourceDescriptionF", "description_fr");
        }});
        
        put("Nutrients", new HashMap<String, String>() {{
            put("NutrientID", "nutrient_id");
            put("NutrientCode", "nutrient_code");
            put("NutrientSymbol", "symbol");
            put("NutrientUnit", "unit");
            put("NutrientName", "name_en");
            put("NutrientNameF", "name_fr");
            put("Tagname", "tagname");
            put("NutrientDecimals", "decimals");
        }});
        
        put("NutrientSources", new HashMap<String, String>() {{
            put("NutrientSourceID", "nutrient_source_id");
            put("NutrientSourceCode", "source_code");
            put("NutrientSourceDescription", "description_en");
            put("NutrientSourceDescriptionF", "description_fr");
        }});
        
        put("NutrientData", new HashMap<String, String>() {{
            put("FoodID", "food_id");
            put("NutrientID", "nutrient_id");
            put("NutrientValue", "nutrient_value");
            put("StandardError", "standard_error");
            put("NumberofObservations", "observations");
            put("NutrientSourceID", "nutrient_source_id");
            put("NutrientDateOfEntry", "date_of_entry");
        }});
        
        put("Measures", new HashMap<String, String>() {{
            put("MeasureID", "measure_id");
            put("MeasureDescription", "description_en");
            put("MeasureDescriptionF", "description_fr");
        }});
        
        put("ConversionFactors", new HashMap<String, String>() {{
            put("FoodID", "food_id");
            put("MeasureID", "measure_id");
            put("ConversionFactorValue", "conversion_factor");
            put("ConvFactorDateOfEntry", "date_of_entry");
        }});
        
        put("RefuseDescriptions", new HashMap<String, String>() {{
            put("RefuseID", "refuse_id");
            put("RefuseDescription", "description_en");
            put("RefuseDescriptionF", "description_fr");
        }});
        
        put("RefuseAmounts", new HashMap<String, String>() {{
            put("FoodID", "food_id");
            put("RefuseID", "refuse_id");
            put("RefuseAmount", "refuse_amount");
            put("RefuseDateOfEntry", "date_of_entry");
        }});
        
        put("YieldDescriptions", new HashMap<String, String>() {{
            put("YieldID", "yield_id");
            put("YieldDescription", "description_en");
            put("YieldDescriptionF", "description_fr");
        }});
        
        put("Yields", new HashMap<String, String>() {{
            put("FoodID", "food_id");
            put("YieldID", "yield_id");
            put("YieldAmount", "yield_amount");
            put("YieldDateOfEntry", "date_of_entry");
        }});
    }};

    public CSVToDBImporter(String dbUrl, String user, String password) {
        this.dbUrl = dbUrl;
        this.user = user;
        this.password = password;
    }

    public void importAllCSVs() throws Exception {
        String csvDir = "src/CSVs/";
        
        String[] importOrder = {
            "FOOD GROUP.csv",
            "FOOD SOURCE.csv", 
            "FOOD NAME.csv",
            "NUTRIENT SOURCE.csv",
            "NUTRIENT NAME.csv",
            "MEASURE NAME.csv",
            "REFUSE NAME.csv",
            "YIELD NAME.csv",
            "NUTRIENT AMOUNT.csv",
            "CONVERSION FACTOR.csv",
            "REFUSE AMOUNT.csv",
            "YIELD AMOUNT.csv"
        };
        
        for (String csvFile : importOrder) {
            String csvPath = csvDir + csvFile;
            String tableName = csvToTableMap.get(csvFile);
            if (tableName != null) {
                System.out.println("Importing " + csvFile + " into " + tableName + "...");
                importCSV(csvPath, tableName);
            }
        }
    }

    public void importCSV(String csvPath, String tableName) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath));
             Connection conn = DriverManager.getConnection(dbUrl, user, password)) {
            
            conn.setAutoCommit(false);
            String headerLine = br.readLine();
            if (headerLine == null) throw new IOException("CSV is empty: " + csvPath);
            
            String[] csvColumns = headerLine.split(",");
            Map<String, String> mapping = columnMappings.get(tableName);
            
            if (mapping == null) {
                System.err.println("No column mapping found for table: " + tableName);
                return;
            }
            
            List<String> dbColumns = new ArrayList<>();
            List<Integer> columnIndexes = new ArrayList<>();
            
            for (int i = 0; i < csvColumns.length; i++) {
                String csvCol = csvColumns[i].trim();
                String dbCol = mapping.get(csvCol);
                if (dbCol != null) {
                    dbColumns.add(dbCol);
                    columnIndexes.add(i);
                }
            }
            
            if (dbColumns.isEmpty()) {
                System.err.println("No matching columns found for table: " + tableName);
                return;
            }
            
            String insertSQL = buildInsertSQL(tableName, dbColumns);
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                String line;
                int count = 0;
                
                while ((line = readCSVLine(br)) != null) {
                    String[] values = parseCSVLine(line, csvColumns.length);
                    
                    if (isEmptyRow(values)) {
                        continue;
                    }
                    
                    for (int i = 0; i < columnIndexes.size(); i++) {
                        int csvIndex = columnIndexes.get(i);
                        String value = (csvIndex < values.length) ? values[csvIndex] : "";
                        setParameterValue(pstmt, i + 1, value, dbColumns.get(i));
                    }
                    
                    pstmt.addBatch();
                    
                    if (++count % batchSize == 0) {
                        pstmt.executeBatch();
                        System.out.println("Processed " + count + " rows...");
                    }
                }
                
                pstmt.executeBatch();
                conn.commit();
                System.out.println("Successfully imported " + count + " rows into " + tableName);
            }
        }
    }

    private String readCSVLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        if (line == null) return null;
        
        int quoteCount = 0;
        for (char c : line.toCharArray()) {
            if (c == '"') quoteCount++;
        }
        
        while (quoteCount % 2 != 0) {
            String nextLine = br.readLine();
            if (nextLine == null) break;
            line += " " + nextLine;
            for (char c : nextLine.toCharArray()) {
                if (c == '"') quoteCount++;
            }
        }
        
        return line;
    }

    private String buildInsertSQL(String table, List<String> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(table).append(" (");
        sb.append(String.join(",", columns));
        sb.append(") VALUES (");
        sb.append(String.join(",", Collections.nCopies(columns.size(), "?")));
        sb.append(")");
        return sb.toString();
    }

    private String[] parseCSVLine(String line, int expectedCols) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString().trim());
        
        while (result.size() < expectedCols) {
            result.add("");
        }
        
        return result.toArray(new String[0]);
    }

    private void setParameterValue(PreparedStatement pstmt, int paramIndex, String value, String columnName) throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            if (columnName.contains("_id") || columnName.contains("_code") || columnName.equals("decimals")) {
                pstmt.setNull(paramIndex, Types.INTEGER);
            } else {
                pstmt.setNull(paramIndex, Types.VARCHAR);
            }
        } else {
            value = value.trim();
            
            if (columnName.contains("_id") || columnName.contains("_code") || columnName.equals("decimals")) {
                try {
                    pstmt.setInt(paramIndex, Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    pstmt.setNull(paramIndex, Types.INTEGER);
                }
            } else if (columnName.contains("_value") || columnName.contains("_factor") || 
                       columnName.contains("_error") || columnName.contains("_amount")) {
                try {
                    pstmt.setDouble(paramIndex, Double.parseDouble(value));
                } catch (NumberFormatException e) {
                    pstmt.setNull(paramIndex, Types.DOUBLE);
                }
            } else if (columnName.equals("observations")) {
                try {
                    pstmt.setInt(paramIndex, Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    pstmt.setNull(paramIndex, Types.INTEGER);
                }
            } else {
                pstmt.setString(paramIndex, value);
            }
        }
    }

    private boolean isEmptyRow(String[] values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        String dbUrl = DBConnectionHelper.get("DB_URL");
        String user = DBConnectionHelper.get("DB_USER");
        String password = DBConnectionHelper.get("DB_PASSWORD");
        
        CSVToDBImporter importer = new CSVToDBImporter(dbUrl, user, password);
        importer.importAllCSVs();
        
        System.out.println("All CSV files imported successfully!");
    }
} 