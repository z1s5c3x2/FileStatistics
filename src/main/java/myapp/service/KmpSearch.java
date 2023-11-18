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
    public static final List<String> CITIES = new ArrayList<>(Arrays.asList(
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
        // 전국 시 리스트를 저장, 문자열을 탐색할때 2개의 문자열을 비교할때
        // 패턴 테이블을 만들어 불일치가 일어나기 까지의 일치한 이전 문자열은 계산하지 않는다
        for(String __city : CITIES)
        {
            if(KMP(_address,__city))
            {
                return __city;
            }
        }
        return "X";
    }

    private int[] getPi(String pattern) {
        int leftIdx = 0;
        int[] pi = new int[pattern.length()]; // 패턴의 길이만큼 테이블 생성 , int의 기본값인 0으로 초기화
        //패턴의 길이만큼 순회
        for (int rightIdx = 1; rightIdx < pattern.length(); rightIdx++) {
            // 왼쪽의 인덱스가 0보다 크고 왼쪽 인덱스의 문자와 오른쪽 인덱스의 문자가 같지 않으면 문자가 일치 할 때 까지 
            // 왼쪽의 인덱스를 현재 인덱스를  현재 인덱스 위치의 이전의 pi의 값으로 초기화 한다
            while(leftIdx > 0 && pattern.charAt(rightIdx) != pattern.charAt(leftIdx)){
                leftIdx = pi[leftIdx - 1];
            }
            // 왼쪽 인덱스의 문자와 오른쪽 인덱스의 문자가 일치한다면 pi 오른쪽 인덱스 위치에 왼쪽 인덱스를 +1 해주고
            // pi 오른쪽 인덱스에 저장한다
            if(pattern.charAt(rightIdx) == pattern.charAt(leftIdx)) {
                pi[rightIdx] = ++leftIdx;
            }
        }
        return pi;
    }

    private boolean KMP(String address,String city) {
        //위 주석 참조
        int leftIdx = 0;
        for (int rightIdx = 0; rightIdx < address.length(); rightIdx++) {
            while(leftIdx > 0 && address.charAt(rightIdx) != city.charAt(leftIdx)) {
                leftIdx = CITTES_PATTTERN.get(city)[leftIdx - 1];
            }
            
            if(address.charAt(rightIdx) == city.charAt(leftIdx)) {
                //양쪽 인덱스의 문자가 같고 왼쪽 인덱스가 city의 길이-1만큼 일치한다면 문자열 일치,true리턴 
                if(leftIdx == city.length() - 1) {
                    return true;
                }
                // 문자가 같지만 완전 일치가 아닌경우 인덱스 1 증가
                else
                    ++leftIdx;
            }
        }
        return false;
    }
}
