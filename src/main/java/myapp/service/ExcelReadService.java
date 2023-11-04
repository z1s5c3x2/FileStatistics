package myapp.service;

import myapp.model.ExcelReadOnlyDto;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

public class ExcelReadService {
    public static void main(String[] args) {
        try{
            ExcelReadOnlyDto excelReadOnlyDto = new ExcelReadOnlyDto().initFile(new ClassPathResource("1월.xlsx").getFile());
            List<List<String>> asd = excelReadOnlyDto.getRows();
            System.out.println("asd = " + asd);
             excelReadOnlyDto = new ExcelReadOnlyDto().initFile(new ClassPathResource("2월.xlsx").getFile());
            asd = excelReadOnlyDto.getRows();
            System.out.println("asd = " + asd);
        }catch(Exception e) {
            System.out.println("main" + e);
        }

    }
}
