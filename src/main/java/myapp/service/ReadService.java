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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;


public class ReadService {
    private static ReadService instance = new ReadService();
    public static ReadService getInstance() {
        return instance;
    }
    private ReadService() {}
    public void getFile(String _month, String getType) {
        if (getType.equals("month")) {
            WriteService.getInstance().writeLogFromExcelToMonth(initFileData(_month),_month);
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
            LocalDate currentDate = LocalDate.of(2022, 11, 1);
            LocalDate endDate = LocalDate.of(2023, 10, 1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            while (!currentDate.isAfter(endDate)) {
                LocalDate _date = currentDate;
                Callable<Void> task = () -> {
                    fileDataDtos.put(_date.format(formatter), initFileData(_date.toString()));
                    return null;
                    //asd.add(new ExcelReadOnlyDto().initFile(new ClassPathResource(_mon+".xlsx").getFile()).getRows());
                };
                tasks.add(task);
                currentDate = currentDate.plusMonths(1); // 다음 달로 이동
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
                WriteService.getInstance().writeLogFromExcelToYear(fileDataDtos);
            } catch (Exception e) {
                System.out.println("getFile" + e);
            }

        }
    }

    private FileDataDto initFileData(String _month) {
        System.out.println("_month = " + _month);
        System.out.println(_month + "파일 접근,데이터 초기화");
        ClassPathResource cpr = new ClassPathResource(_month + ".xlsx");

        try {
            XSSFWorkbook wb = new XSSFWorkbook(cpr.getInputStream());
            FileDataDto fileDataDto = FileDataDto.builder()
                    .startRow(16)
                    .maxRow(wb.getSheetAt(0).getLastRowNum())
                    .build();
            
            XSSFSheet sheet = wb.getSheetAt(0);
            wb.close();
            cpr.getInputStream().close();

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
            tasks.clear();
            return fileDataDto;
        } catch (Exception e) {
            System.out.println("initFiledata" + e);
        }
        return null;
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



}
