package myapp.Controller;

import myapp.service.ReadService;


public class ReadController {
    private static ReadController instance = new ReadController();
    public static ReadController getInstance() {return instance;}
    private ReadController() {};

    public void readFile(String fileName)
    {
        ReadService.getInstance().readFile(fileName);
    }
}
