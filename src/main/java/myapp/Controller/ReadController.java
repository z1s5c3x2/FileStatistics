package myapp.Controller;

import myapp.service.ReadService;

import java.util.HashMap;
import java.util.Map;


public class ReadController {
    private static ReadController instance = new ReadController();
    public static ReadController getInstance() {return instance;}
    private ReadController() {};

    public void getFile(String fileName,String getType)
    {
        ReadService.getInstance().getFile(fileName,getType);
    }
    public Map<String,Float> cityToThreeRank(String target) {
        return ReadService.getInstance().cityToThreeRank(target);
    }
}
