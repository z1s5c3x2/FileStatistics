package myapp.view;

import myapp.Controller.ReadController;
import myapp.service.KmpSearch;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MenuView {
    private static MenuView instance = new MenuView();
    public static MenuView getInstance() {return instance;}
    private MenuView() {};
    private static Scanner sc = new Scanner(System.in);

    public void selectMenu()
    {
        while(true) {
            System.out.println("1. 개별 월단위 조회 2.최근 1년 조회,3 최근 1년 시,단지별 평균 면적 ");
            int ch = sc.nextInt();
            sc.nextLine();
            if (ch==1) {
                System.out.println("(2022-11 ~ 2023-10 ) 입력 \n(예시 : 2023-04 ) >> ");

                ReadController.getInstance().getFile(sc.nextLine() , "month");
            }else if(ch==2){
                ReadController.getInstance().getFile(null , "year");

            } else {
                System.out.println("조회할 시 입력");
                int newline = 1;
                for(String _c :KmpSearch.CITIES)
                {
                    System.out.printf("%d.%s ",newline,_c);
                    if(newline%7==0){
                        System.out.println();
                    }
                    newline++;
                }
                int city = sc.nextInt()-1;
                sc.nextLine();
                Map<String,Float> result = ReadController.getInstance().cityToThreeRank(KmpSearch.CITIES.get(city));
                int rank = 1;
                System.out.println(KmpSearch.CITIES.get(city)+" 전용 면적당 높은 가격순 정렬(단위 만원)");
                for(Map.Entry<String,Float> entry : result.entrySet())
                {
                    System.out.printf("%d. 단지명:%s \t전용면적(m^2)당 가격 : %.3f\n",rank++,entry.getKey(),entry.getValue());
                    if(rank==4) break;
                }

            }
        }
    }

}

