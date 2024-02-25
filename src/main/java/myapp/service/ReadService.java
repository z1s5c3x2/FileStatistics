package myapp.service;


import com.github.pjfanning.xlsx.SharedStringsImplementationType;
import com.github.pjfanning.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
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
import org.yaml.snakeyaml.reader.StreamReader;


import javax.annotation.security.RunAs;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
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
            WriteService.getInstance().writeLogFromExcelToYear(fileYearRead());
        }
    }
    public Map<String,Float> cityToThreeRank(String target)
    {
        return WriteService.getInstance().choiceCityInMere(fileYearRead(),target);
    }
    public HashMap<String, FileDataDto> fileYearRead(){
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
                fileDataDtos.put(_date.format(formatter), initFileData(_date.format(formatter)));
                return null;
                //asd.add(new ExcelReadOnlyDto().initFile(new ClassPathResource(_mon+".xlsx").getFile()).getRows());
            };
            tasks.add(task);
            currentDate = currentDate.plusMonths(1); // 다음 달로 이동
        }
        try{
            threadPool.invokeAll(tasks);
            threadPool.shutdown();
            return fileDataDtos;
        }catch(Exception e) {
            System.out.println("fileYearRead" + e);
            return null;
        }
    }

    private FileDataDto initFileData(String _month) {
        System.out.println("_month = " + _month);
        System.out.println(_month + "파일 접근,데이터 초기화");

        ClassPathResource cpr = new ClassPathResource(_month + ".xlsx");
        try {
            //InputStream ins = cpr.getInputStream();

            FileDataDto fileDataDto = FileDataDto.builder()
                    .startRow(16)
                    .build();
            StreamingReader.builder()
                    .rowCacheSize(100)
                    .bufferSize(4096)
                    .open(cpr.getFile())
                    .getSheetAt(0).forEach( r-> {
                        if(r.getRowNum() > 16) {

                            fileDataDto.getRowList().add(new ExcelDataDto().rowToDto(r));
                        }
                    });
                    //.getSheetAt(0).forEach(r->fileDataDto.getRowList().add(r.));
//            wb.getSheetAt(0).iterator().forEachRemaining(r->fileDataDto.getRowList().add(r));
//            wb.close();



//            ExecutorService threadPool = new ThreadPoolExecutor(
//                    3, // 코어 스레드 개수
//                    3, // 최대 스레드 개수
//                    120L, // 최대 놀 수 있는 시간 (이 시간 넘으면 스레드 풀에서 쫓겨 남.)
//                    TimeUnit.SECONDS, // 놀 수 있는 시간 단위
//                    new ArrayBlockingQueue<Runnable>(6)
//            );
//
//            int endPage = (int)Math.ceil((fileDataDto.getMaxRow()-16)/10000);
//            endPage = 30000;
//            List<Callable<Void>> tasks = new ArrayList<>();
//            for(int page = 0; page <=endPage;page++)
//            {
//                int now = page == 0 ? 17 : page*10000;
//                int end = Math.min((page + 1) * 10000, fileDataDto.getMaxRow());
//                Callable<Void> task = () -> {
//                    readFile(fileDataDto,now,end,sheet);
//                    return null;
//                };
//                tasks.add(task);
//            }
//            threadPool.invokeAll(tasks);
//            threadPool.shutdown();
//            tasks.clear();
            return fileDataDto;
        } catch (Exception e) {
            log.error("init error {}",e);
        }
        return null;
    }


    private void readFile(FileDataDto fdt,int start,int end,Sheet sheet)
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
