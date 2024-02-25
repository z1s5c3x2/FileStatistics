package myapp.service;

import lombok.extern.slf4j.Slf4j;
import myapp.model.ExcelDataDto;
import myapp.model.FileDataDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WriteService {

    private static WriteService instance = new WriteService();
    public static WriteService getInstance() {return instance;}
    private WriteService() {}

    public void writeLogFromExcelToYear(HashMap<String, FileDataDto> stringFileDataDtoHashMap) {

        SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("year");

        /*형식 지정*/
        Row initRow = sheet.createRow(0);
        Cell initCell = initRow.createCell(0);
        initCell.setCellValue("날짜");
        initCell = initRow.createCell(1);
        initCell.setCellValue("거래량");
        initCell = initRow.createCell(2);
        initCell.setCellValue("변동폭%");
        /* end */

        LocalDate currentDate = LocalDate.of(2022, 11, 1);
        LocalDate endDate = LocalDate.of(2023, 10, 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        int currentRow = 1;
        while (!currentDate.isAfter(endDate)) {
            Row row = sheet.createRow(currentRow);
            Cell cell = row.createCell(0);
            cell.setCellValue(currentDate.format(formatter)+ "월");
            cell = row.createCell(1);
            cell.setCellValue(stringFileDataDtoHashMap.get(currentDate.format(formatter)).getRowList().size());
            cell = row.createCell(2);
            String changePercent="100%";
            if(currentRow != 1)
            {
                int nowMonthSize =  stringFileDataDtoHashMap.get(currentDate.format(formatter)).getRowList().size();
                int preMonthSize = stringFileDataDtoHashMap.get(currentDate.minusMonths(1).format(formatter)).getRowList().size();
                changePercent = String.format("%.2f",(nowMonthSize-preMonthSize)/(float)preMonthSize*100)+"%";
            }
            cell.setCellValue(changePercent);
            currentDate = currentDate.plusMonths(1);
            currentRow++;
        }
        try {
            FileOutputStream out = new FileOutputStream("src\\main\\resources\\최근1년조회.xlsx");
            wb.write(out);
            out.flush();
            System.out.println("저장 성공");
            wb.close();
        } catch (Exception e) {
            System.out.println("e = " + e);
        }

    }


    public Map<String,Float> choiceCityInMere(HashMap<String, FileDataDto> stringFileDataDtoHashMap,String target)
    {
        //System.out.println("target = " + target);
        HashMap<String,int[]> mereCount = new HashMap<>();
        HashMap<String,Float> mereInfo = new HashMap<>();
        for(Map.Entry<String,FileDataDto> entry : stringFileDataDtoHashMap.entrySet())
        {
            for(ExcelDataDto _dto : entry.getValue().getRowList())
            {

                String _city =KmpSearch.getInstance().kmpSearch(_dto.getAddress1());
                if(_city.equals(target))
                {
                    String _mere = _dto.getMereName();
                    if(!mereInfo.containsKey(_mere))
                    {
                        mereInfo.put(_mere,_dto.getAreaSize());
                        mereCount.put(_mere, new int[]{1, _dto.getAmount()});
                    }else{
                        int[] _cityData =mereCount.get(_mere);
                        _cityData[0] ++;
                        _cityData[1] += _dto.getAmount();
                        mereCount.put(_mere,_cityData);
                    }
                }
            }
        }
        HashMap<String,Float> result = new HashMap<>();
        for(Map.Entry<String,int[]> entry : mereCount.entrySet())
        {
            int[] _tmp = entry.getValue();
            float _res = ( (float) _tmp[1] /_tmp[0] )/mereInfo.get(entry.getKey());
            result.put(entry.getKey(),_res);
        }
        LinkedHashMap<String,Float> rankSort = result.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue()),
                        LinkedHashMap::putAll);

        // 정렬된 결과 출력
        return rankSort;
    }
    public void writeLogFromExcelToMonth(FileDataDto fileDataDto,String fileName) {
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
        XSSFWorkbook wb = new XSSFWorkbook();
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
            FileOutputStream out = new FileOutputStream("src\\main\\resources\\"+ fileName+ "조회.xlsx");
            wb.write(out);
            out.flush();
            out.close();
            System.out.println("저장 성공");
            wb.close();
        } catch (Exception e) {
            System.out.println("e = " + e);
        }

    }
}
