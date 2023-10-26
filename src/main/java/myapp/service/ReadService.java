package myapp.service;

import myapp.model.ExcelDataDto;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;


import javax.annotation.security.RunAs;
import java.util.*;


public class ReadService {

    private static ReadService instance = new ReadService();
    public static ReadService getInstance() {return instance;}
    private ReadService() {};

    private static final int START_ROW = 16;
    private Scanner sc = new Scanner(System.in);
    public void readFile(String dateRange)
    {
        System.out.println(dateRange + " 읽기 시작");
        ClassPathResource cpr = new ClassPathResource(dateRange+".xlsx");

        List<ExcelDataDto> transList = new ArrayList<>();

        Workbook wb = null;
        try {
            //Path p = Paths.get(cpr.getURI());
            //System.out.println("_tmp = " + _tmp);

            // sheet 0번 접근
            wb = new XSSFWorkbook(cpr.getInputStream());
            Sheet sheet = wb.getSheetAt(0);

            int readStart = START_ROW;
            Row row;

            /*//System.out.println("_tmp.getPhysicalNumberOfRows() = " + _tmp.getPhysicalNumberOfRows());
            System.out.println("시작");
            System.out.println(row.getCell(0).getStringCellValue());
            System.out.println(row.getCell(1).getStringCellValue());
            System.out.println(row.getCell(2).getStringCellValue());
            System.out.println(row.getCell(3).getStringCellValue());
            System.out.println(row.getCell(4).getStringCellValue());
            System.out.println(Float.parseFloat(row.getCell(5).getStringCellValue()));
            System.out.println(row.getCell(6).getStringCellValue());
            System.out.println(row.getCell(7).getStringCellValue());
            System.out.println(Integer.parseInt(row.getCell(8).getStringCellValue().replaceAll(",","").trim() ));
            System.out.println(Integer.parseInt(row.getCell(9).getStringCellValue()));
            System.out.println(row.getCell(10).getStringCellValue());
            System.out.println(row.getCell(11).getStringCellValue());
            System.out.println(row.getCell(12).getStringCellValue());
            System.out.println(row.getCell(13).getStringCellValue());
            System.out.println(row.getCell(14).getStringCellValue());
            System.out.println(row.getCell(15).getStringCellValue());
            System.out.println("끝");*/

            while (true)
            {
                row = sheet.getRow(readStart+1);
                //System.out.println("row.getPhysicalNumberOfCells() = " + row.getPhysicalNumberOfCells());
                if(row == null) break;
                transList.add(new ExcelDataDto().rowToDto(row));
                readStart++;

            }

            //System.out.println("transList.size() = " + transList.size());
            System.out.println(dateRange + transList.get(transList.size()-1).toString());
            //return //dateRange + transList.get(transList.size()-1).toString();

        }catch (Exception e )
        {
            System.out.println("readerror = " + e);
        }
    }
}
