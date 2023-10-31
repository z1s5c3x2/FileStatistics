package myapp.service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class KmpSearch {
    private static KmpSearch instance = new KmpSearch();
    public static KmpSearch getInstance() {return instance;}
    private KmpSearch() {};

    //대한민국 시 선언
    private static final List<String> CITIES = new ArrayList<>(Arrays.asList(
            "서산시", "나주시", "세종특별자치시", "하남시", "광명시", "당진시", "삼척시", "인천광역시","안성시","영천시","부천시","상주시","통영시","김해시",
            "광주광역시", "평택시", "포천시", "여주시", "울산광역시", "의왕시", "김제시", "안동시","대구광역시","오산시","양주시","김천시","과천시","동해시",
            "목포시", "충주시", "군산시", "속초시", "동두천시", "순천시", "강릉시", "여수시", "남양주시","계룡시","정읍시","익산시","화성시","남원시",
            "구리시", "이천시", "제주시", "거제시", "시흥시", "춘천시", "논산시", "서귀포시", "파주시","광주시","아산시","의정부시",
            "광양시", "공주시", "영주시", "보령시", "서울특별시", "밀양시", "부산광역시", "원주시", "문경시","제천시","양산시","사천시","태백시",
            "군포시", "진주시", "김포시", "경주시", "구미시", "경산시", "대전광역시"));
    private static Map<String,int[]> CITTES_PATTTERN = new HashMap<>();

    public void initPatternIndex()
    {
        for(String __city : CITIES)
        {
            CITTES_PATTTERN.put(__city,getPi(__city));
        }
    }
    public String kmpSearch(String _address) {

        if(CITTES_PATTTERN.size() == 0)
        {
            initPatternIndex();
        }

        for(String __city : CITIES)
        {
            if(KMP(_address,__city))
            {
                return __city;
            }
        }
        return "작은곳?";
    }

    private int[] getPi(String pattern) {
        int j = 0;
        int[] pi = new int[pattern.length()];
        for (int i = 1; i < pattern.length(); i++) {
            // 맞는 위치가 나올 때까지 j - 1칸의 공통 부분 위치로 이동
            while(j > 0 && pattern.charAt(i) != pattern.charAt(j)){
                j = pi[j - 1];
            }
            // 맞는 경우
            if(pattern.charAt(i) == pattern.charAt(j)) {
                //i길이 문자열의 공통 길이는 j의 위치 + 1
                pi[i] = ++j;
            }
        }
        return pi;
    }

    private boolean KMP(String address,String city) {
        int j = 0;
        for (int i = 0; i < address.length(); i++) {
            // 맞는 위치가 나올 때까지 j - 1칸의 공통 부분 위치로 이동
            while(j > 0 && address.charAt(i) != city.charAt(j)) {
                j = CITTES_PATTTERN.get(city)[j - 1];
            }

            if(address.charAt(i) == city.charAt(j)) {
                if(j == city.length() - 1) {
                    return true;
                }
                //맞았지만 패턴이 끝나지 않았다면 j를 하나 증가
                else
                    ++j;
            }
        }
        return false;
    }
}
