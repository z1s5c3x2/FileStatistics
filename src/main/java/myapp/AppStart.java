package myapp;

import myapp.Controller.ReadController;
import myapp.service.ReadService;
import myapp.view.MenuView;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class AppStart {

    public static void main(String[] args) {
        MenuView.getInstance().selectMenu();;
    }
}
        //while (true)
        /*
        *  기능구현 1.
        *   첫줄부터 한줄씩 읽어서 진짜 데이터 row를 식별하여 거기서부터 읽기 시작
        *
        * List<Integer> testList = new ArrayList<>();
        for(int i=1;i<=12;i++)
        {
            testList.add(i);
        }
        /*for(int mon : testList)
        {
            //String _str = sc.nextLine();
            String _str = mon+"월";
            System.out.println(_str + " 읽기 시작(스레드 요청)");
            threadPool.submit(() -> {
                ReadService.getInstance().readFile(_str);
            });
            System.out.println(_str + " 읽기 끝");

        }*/



