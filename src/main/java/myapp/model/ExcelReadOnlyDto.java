package myapp.model;

import lombok.*;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.xmlbeans.impl.common.SAXHelper;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder @ToString

public class ExcelReadOnlyDto implements XSSFSheetXMLHandler.SheetContentsHandler {
    private List<List<String>> rows = new ArrayList<>();
    private List<String> row = new ArrayList<>();
    private List<String> header = new ArrayList<>();

    private int currentColNum = -1;
    private int currentRowNum = 0;
    public void startRow(int rowNum) {
        this.currentColNum = -1;
        this.currentRowNum = rowNum;
    }

    @Override
    public void endRow( int rowNum ) {
        if ( rowNum == 0 ) {
            header = new ArrayList( row );
        } else {
            if ( row.size() < header.size() ) {
                for ( int i = row.size(); i < header.size(); i++ ) {
                    row.add( "" );
                }
            }
            rows.add( new ArrayList( row ) );
        }
        row.clear();
    }
    @Override
    public void cell(String colName, String value, XSSFComment comment) {
        int col = (new CellReference(colName)).getCol();
        int tmpCol = col - currentColNum -1;
        for(int i=0;i<tmpCol;i++)
        {
            row.add("");
        }
        currentColNum = col;
        row.add(value);
    }
    public ExcelReadOnlyDto initFile(File getFile){
        ExcelReadOnlyDto excelReadOnlyDto = new ExcelReadOnlyDto();
        try{
            File file =  getFile; //리소스 폴더 파일 접근
            OPCPackage opc = OPCPackage.open(file);      //  opc 객체 생성
            XSSFReader xssfReader = new XSSFReader(opc);
            StylesTable styleSheet = xssfReader.getStylesTable();
            ReadOnlySharedStringsTable readOnlySharedStringsTable = new ReadOnlySharedStringsTable(opc);

            InputStream inputStream = xssfReader.getSheetsData().next();
            InputSource inputSource = new InputSource(inputStream);
            ContentHandler contentHandler = new XSSFSheetXMLHandler(styleSheet,readOnlySharedStringsTable,excelReadOnlyDto,false);

            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true);
            SAXParser parser    = saxParserFactory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            xmlReader.setContentHandler(contentHandler);
            xmlReader.parse(inputSource);
            inputStream.close();
            opc.close();

        }catch(Exception e) {
            System.out.println("main" + e);
        }
        return excelReadOnlyDto;
    }


}
