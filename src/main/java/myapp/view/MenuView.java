package myapp.view;

import myapp.Controller.ReadController;

import java.util.Scanner;

public class MenuView {
    private static MenuView instance = new MenuView();
    public static MenuView getInstance() {return instance;}
    private MenuView() {};
    private static Scanner sc = new Scanner(System.in);

    public void selectMenu()
    {
        while(true) {
            System.out.println("1. 개별 월단위 조회 2.최근 1년 조회");
            if (sc.nextInt() == 1) {
                System.out.println("(2022-11 ~ 2023-10 ) 입력 \n(예시 : 2023-04 ) >> ");
                sc.nextLine();
                ReadController.getInstance().getFile(sc.nextLine() , "month");
            } else {
                ReadController.getInstance().getFile(null , "year");
            }
        }
    }

}

