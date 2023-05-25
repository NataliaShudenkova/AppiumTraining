package Screens;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BaseScreen {
    AppiumDriver driver;

    public BaseScreen(AppiumDriver driver){
        this.driver = driver;
    }

    private ExpectedCondition<WebElement> elementIsDisplayed(By by){
        return appiumDriver -> {
            List<WebElement> list;
            list = appiumDriver.findElements(by);
            if (list.size() > 0 && list.get(0).isDisplayed()) {
                return list.get(0);
            } else return null;
        };
    }

    public WebElement findByWithWaitForSeconds(By by){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        return wait.until(elementIsDisplayed(by));
    }


    public WebElement findByWithWaitForMinute(By by){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(1));
        return wait.until(elementIsDisplayed(by));
    }

    public boolean tryFindElement(By by){
        var elements = driver.findElements(by);
        if( elements.size() == 1 && elements.get(0).isDisplayed()){
            return true;
        }else{
            return false;
        }
    }

}
