package Screens;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;

public class NotificationScreen extends BaseScreen{
    private AppiumDriver driver;
    public NotificationScreen(io.appium.java_client.AppiumDriver driver){
        super(driver);
        this.driver = driver;
    }

    public String getElementText(By by){
        return findByWithWaitForMinute(by).getText();
    }
}
