package Screens;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;

public class WelcomeCalendarScreen extends BaseScreen{
    private AppiumDriver driver;
    public WelcomeCalendarScreen(AppiumDriver driver){
        super(driver);
        this.driver = driver;
    }

    private final By Welcome_Next_Button =  By.id("next_arrow");
    private By Welcome_Done_Button =  By.id("oobe_done_button");


    public boolean isWelcomeCalendarScreen(){
        var welcomeWindow = driver.findElements(By.xpath("//*[@content-desc='Welcome to Google Calendar']"));
        return welcomeWindow.isEmpty() ? false : true;
    }

    public void NavigateToCalendar(){
        findByWithWaitForSeconds(Welcome_Next_Button).click();
        findByWithWaitForSeconds(Welcome_Done_Button).click();
    }
}
