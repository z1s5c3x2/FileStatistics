package myapp.model;


import lombok.*;
import org.apache.poi.ss.usermodel.Row;

import java.util.Date;

@Builder @ToString @AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class ExcelDataDto {

    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String mereName;
    private float areaSize;
    private String contractYM;
    private String contractD;
    private int amount;
    private int layer;
    private String buildY;
    private String addressNew;
    private String cancleYMD;
    private String registrationDate;
    private String transType;
    private String brokerAddress;

    public ExcelDataDto rowToDto(Row row)
    {
        return ExcelDataDto.builder()
                .address1(row.getCell(0).getStringCellValue())
                .address2(row.getCell(1).getStringCellValue())
                .address3(row.getCell(2).getStringCellValue())
                .address4(row.getCell(3).getStringCellValue())
                .mereName(row.getCell(4).getStringCellValue())
                .areaSize(Float.parseFloat(row.getCell(5).getStringCellValue()))
                .contractYM(row.getCell(6).getStringCellValue())
                .contractD(row.getCell(7).getStringCellValue())
                .amount(Integer.parseInt(row.getCell(8).getStringCellValue().replaceAll(",","").trim() ))
                .layer(Integer.parseInt(row.getCell(9).getStringCellValue()))
                .buildY(row.getCell(10).getStringCellValue())
                .addressNew(row.getCell(11).getStringCellValue())
                .cancleYMD(row.getCell(12).getStringCellValue())
                .registrationDate(row.getCell(13).getStringCellValue())
                .transType(row.getCell(14).getStringCellValue())
                .brokerAddress(row.getCell(15).getStringCellValue())
                .build();
    }
}
