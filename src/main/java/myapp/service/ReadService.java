package myapp.service;

import myapp.model.ExcelDataDto;
import myapp.model.ExcelReadOnlyDto;
import myapp.model.FileDataDto;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.core.io.ClassPathResource;


import javax.annotation.security.RunAs;
import java.io.FileOutputStream;
import java.util.*;
import java.util.concurrent.*;


public class ReadService {

    private static ReadService instance = new ReadService();

    public static ReadService getInstance() {
        return instance;
    }

    private ReadService() {
    }

    ;

    public void getFile(String _month, String getType) {
        if (getType.equals("month")) {
            writeLogFromExcelToMonth(initFileData(_month));
        } else {
            HashMap<String, FileDataDto> fileDataDtos = new HashMap<>();
            ExecutorService threadPool = new ThreadPoolExecutor(
                    3, // 코어 스레드 개수
                    3, // 최대 스레드 개수
                    120L, // 최대 놀 수 있는 시간 (이 시간 넘으면 스레드 풀에서 쫓겨 남.)
                    TimeUnit.SECONDS, // 놀 수 있는 시간 단위
                    new ArrayBlockingQueue<Runnable>(24) // 작업큐
            );
            List<Callable<Void>> tasks = new ArrayList<>();
            //List<List<List<String>>> asd = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                String _mon = i + "월";
                Callable<Void> task = () -> {

                    //asd.add(new ExcelReadOnlyDto().initFile(new ClassPathResource(_mon+".xlsx").getFile()).getRows());
                    fileDataDtos.put(_mon, initFileData(_mon));

                    return null;
                };
                tasks.add(task);
            }
            try {
                threadPool.invokeAll(tasks);
                threadPool.shutdown();
                //writeLogFromExcelToYear(fileDataDtos);
                /*int getFullSize = 0;
                for(List<List<String>> _list : asd)
                {   
                    getFullSize += _list.size();
                }
                System.out.println("getFullSize = " + getFullSize);*/
                /*for(Map.Entry<String,FileDataDto> item : fileDataDtos.entrySet())
                {
                    System.out.println(item.getValue().getRowList().size());
                }*/
                /*for(Map.Entry<String,FileDataDto> item : fileDataDtos.entrySet())
                {
                    System.out.println(item.getKey()+"item.getValue().getRowList().size() = " + item.getValue().getRowList().size());
                }*/
                writeLogFromExcelToYear(fileDataDtos);
            } catch (Exception e) {
                System.out.println("getFile" + e);
            }

        }
    }

    private FileDataDto initFileData(String _month) {
        System.out.println("_month = " + _month);
        System.out.println(_month + "파일 접근,데이터 초기화");
        ClassPathResource cpr = new ClassPathResource(_month + ".xlsx");
        //FileDataDto fileDataDto = null;
        /*
        FileDataDto fileDataDto = searchRow(cpr);
        System.out.println("fileDataDto = " + fileDataDto);
        System.out.println("스레드풀 초기화");
        ExecutorService threadPool = new ThreadPoolExecutor(
                3, // 코어 스레드 개수
                5, // 최대 스레드 개수
                120L, // 최대 놀 수 있는 시간 (이 시간 넘으면 스레드 풀에서 쫓겨 남.)
                TimeUnit.SECONDS, // 놀 수 있는 시간 단위
                new ArrayBlockingQueue<Runnable>(10)
        );
        List<Callable<Void>> tasks = new ArrayList<>();

        int startRow = fileDataDto.getStartRow();
        int endPage = (int)Math.ceil((fileDataDto.getMaxRow()-startRow)/10000);

        for(int start = 0; start<=endPage;start++)
        {
            int now = start*10000 == 0 ? fileDataDto.getStartRow() : start*10000;
            int end = now+10000 > fileDataDto.getMaxRow() ? fileDataDto.getMaxRow() :now+10000;
            Callable<Void> task = ()->{
                readFile(fileDataDto,now,end);
                return null;
            };
            tasks.add(task);
        }
        try {
            List<Future<Void>> futures = threadPool.invokeAll(tasks);
            System.out.println("성공");
            return fileDataDto;
        }catch (Exception e)
        {
            System.out.println("init 에러 = " + e);
        }*/

        try {
            XSSFWorkbook wb = new XSSFWorkbook(cpr.getInputStream());
            FileDataDto fileDataDto = FileDataDto.builder()
                    .startRow(16)
                    .maxRow(wb.getSheetAt(0).getLastRowNum())
                    .build();
            
            XSSFSheet sheet = wb.getSheetAt(0);
            wb.close();

            ExecutorService threadPool = new ThreadPoolExecutor(
                    3, // 코어 스레드 개수
                    3, // 최대 스레드 개수
                    120L, // 최대 놀 수 있는 시간 (이 시간 넘으면 스레드 풀에서 쫓겨 남.)
                    TimeUnit.SECONDS, // 놀 수 있는 시간 단위
                    new ArrayBlockingQueue<Runnable>(6)
            );

            int endPage = (int)Math.ceil((fileDataDto.getMaxRow()-16)/10000);
            List<Callable<Void>> tasks = new ArrayList<>();
            for(int page = 0; page <=endPage;page++)
            {
                int now = page == 0 ? 17 : page*10000;
                int end = Math.min((page + 1) * 10000, fileDataDto.getMaxRow());
                Callable<Void> task = () -> {
                    readFile(fileDataDto,now,end,sheet);
                    return null;
                };
                tasks.add(task);
            }
            threadPool.invokeAll(tasks);
            threadPool.shutdown();
            return fileDataDto;
        } catch (Exception e) {
            System.out.println("initFiledata" + e);
        }
        return null;
    }

    private void writeLogFromExcelToYear(HashMap<String, FileDataDto> stringFileDataDtoHashMap) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("year");


        Row initRow = sheet.createRow(0);
        Cell initCell = initRow.createCell(0);
        initCell.setCellValue("월");
        initCell = initRow.createCell(1);
        initCell.setCellValue("거래량");
        initCell = initRow.createCell(2);
        initCell.setCellValue("변동폭%");
        for (int i = 1; i <= 12; i++) {
            Row row = sheet.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(i + "월");
            cell = row.createCell(1);
            cell.setCellValue(stringFileDataDtoHashMap.get(i + "월").getRowList().size());
            cell = row.createCell(2);
            String changePercent="100%";
            if(i != 1)
            {
                int nowMonthSize =  stringFileDataDtoHashMap.get(i+"월").getRowList().size();
                int postMonthSize = stringFileDataDtoHashMap.get((i-1)+"월").getRowList().size();
                changePercent = String.format("%.2f",(nowMonthSize-postMonthSize)/(float)postMonthSize*100)+"%";
            }
            cell.setCellValue(changePercent);
        }
        try {
            FileOutputStream out = new FileOutputStream("src\\main\\resources\\lastyearhview.xlsx");
            wb.write(out);
            out.flush();
            System.out.println("저장 성공");
            wb.close();
        } catch (Exception e) {
            System.out.println("e = " + e);
        }

    }

    private void writeLogFromExcelToMonth(FileDataDto fileDataDto) {
        // 주소 분리,거래량 저장
        Map<String, Integer> saveData = new HashMap<>();
        for (ExcelDataDto _dto : fileDataDto.getRowList()) {
            String city = KmpSearch.getInstance().kmpSearch(_dto.getAddress1());
            if (!saveData.containsKey(city)) {
                saveData.put(city, 1);
            } else {
                saveData.put(city, saveData.get(city) + 1);
            }
        }
        saveData.remove("X");
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("data");

        int nowRow = 1;
        Row initrow = sheet.createRow(0);
        // 지역
        Cell initcell = initrow.createCell(0);
        initcell.setCellValue("지역");
        // 거래량
        initcell = initrow.createCell(1);
        initcell.setCellValue("거래수");
        for (Map.Entry<String, Integer> _map : saveData.entrySet()) {

            Row row = sheet.createRow(nowRow++);
            // 지역
            Cell cell = row.createCell(0);
            cell.setCellValue(_map.getKey());
            // 거래량
            cell = row.createCell(1);
            cell.setCellValue(_map.getValue());
        }

        try {
            FileOutputStream out = new FileOutputStream("src\\main\\resources\\lastmonthview.xlsx");
            wb.write(out);
            out.flush();
            System.out.println("저장 성공");
            wb.close();
        } catch (Exception e) {
            System.out.println("e = " + e);
        }

    }
    private void readFile(FileDataDto fdt,int start,int end,XSSFSheet sheet)
    {
        try {
            //System.out.println(", start = " + start + ", end = " + end);
            for(int nowRow = start;nowRow<=end;nowRow++)
            {
                Row row = sheet.getRow(nowRow);

                if(row == null) break;
                fdt.getRowList().add(new ExcelDataDto().rowToDto(row));

            }
        }catch (Exception e )
        {
            //System.out.println(fdt.getSheet().getRow(asd).toString());
            System.out.println("readerror = " + e);
        }

    }
    /*
    private FileDataDto searchRow(ClassPathResource cpr)
    {
        FileDataDto fileDataDto = null;
        try {
            XSSFWorkbook wb = new XSSFWorkbook(cpr.getInputStream());
            fileDataDto = FileDataDto.builder()
                    .startRow(1)
                    .maxRow(wb.getSheetAt(0).getLastRowNum())
                    .sheet(wb.getSheetAt(0)).build();

            while (true)
            {
                Row row = fileDataDto.getSheet().getRow(fileDataDto.getStartRow());
                fileDataDto.setStartRow(fileDataDto.getStartRow()+1);
                if(fileDataDto.getStartRow() == fileDataDto.getMaxRow()) return null;
                else if(row == null) continue;
                else if(row.getCell(0).getStringCellValue().equals("시군구"))
                {
                    fileDataDto.setStartRow(fileDataDto.getStartRow()+1);
                    return fileDataDto;
                }

            }
        }catch (Exception e)
        {
            System.out.println("fileDataDto.getStartRow() = " + fileDataDto.getStartRow());
            System.out.println("e  = " +e);
        }

        return fileDataDto;
    }*/


}
