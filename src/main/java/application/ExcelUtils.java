package application;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    // Path to the Excel file
    private static final String FILE_PATH = "C:/Users/PC/Desktop/products.xlsx";

    // Method to add a new product to the Excel sheet
    public static void addProductToExcel(Product product) {

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);  // Get the first sheet (Products sheet)

            // Create a new row for the new product
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);

            // Set the product data into the row
            row.createCell(0).setCellValue(product.getName());
            row.createCell(1).setCellValue(product.getCategory());
            row.createCell(2).setCellValue(product.getPrice());
            row.createCell(3).setCellValue(product.getQuantity());

            // Save the changes to the Excel file
            try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to create the Excel file if it doesn't exist
    public static void createExcelFileIfNotExists() {
        File file = new File(FILE_PATH);

        // Check if the file doesn't exist
        if (!file.exists()) {
            // Ensure the parent directory exists
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();  // Create the parent directories if they don't exist
            }

            // Create the Excel file
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Products");
                Row header = sheet.createRow(0);

                // Set the header row for the product columns
                header.createCell(0).setCellValue("Name");
                header.createCell(1).setCellValue("Category");
                header.createCell(2).setCellValue("Price");
                header.createCell(3).setCellValue("Quantity");

                // Save the Excel file
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to read products from the Excel file and return them as a list
    public static List<Product> getAllProductsFromExcel() {
        List<Product> products = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);  // Get the first sheet (Products sheet)
            for (Row row : sheet) {
                if (row.getRowNum() > 0) {  // Skip header row
                    String name = row.getCell(0).getStringCellValue();
                    String category = row.getCell(1).getStringCellValue();
                    double price = row.getCell(2).getNumericCellValue();
                    int quantity = (int) row.getCell(3).getNumericCellValue();

                    products.add(new Product(name, category, price, quantity));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }
}
