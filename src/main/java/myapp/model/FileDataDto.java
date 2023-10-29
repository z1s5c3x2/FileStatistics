package myapp.model;

import lombok.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@Builder @ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileDataDto {
    private int startRow;
    private int maxRow;
    private Sheet sheet;
    @Builder.Default
    private Vector<ExcelDataDto> rowList = new Vector<>();

}
