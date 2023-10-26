package myapp;

import myapp.service.ReadService;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class AppStart {
    private static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.println(Paths.get("").toAbsolutePath().toString());
        //초기 스레드수 설정 5
        ExecutorService threadPool = new ThreadPoolExecutor(
                3, // 코어 스레드 개수
                100, // 최대 스레드 개수
                120L, // 최대 놀 수 있는 시간 (이 시간 넘으면 스레드 풀에서 쫓겨 남.)
                TimeUnit.SECONDS, // 놀 수 있는 시간 단위
                new SynchronousQueue<Runnable>() // 작업 큐
        );
        System.out.println("1월 ~ 12월 입력 >> ");

        while (true)
        {
            String _str = sc.nextLine();
            System.out.println(_str + " 읽기 시작(스레드 요청)");
            threadPool.submit(() -> {
                ReadService.getInstance().readFile(_str);
            });
            System.out.println(_str + " 읽기 끝");

        }

    }
}
