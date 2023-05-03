package Screens;

import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

public class CalendarScreen extends BaseScreen{
    private AppiumDriver driver;
    public CalendarScreen(AppiumDriver driver){
        super(driver);
        this.driver = driver;
    }

    private final By Calendar_Actions_Button =  By.id("floating_action_button");
    private final By Calendar_Event_Button =  AppiumBy.accessibilityId("Event button");
    private final By Calendar_Left_Sidebar =  AppiumBy.accessibilityId("Show Calendar List and Settings drawer");
    private final By Calendar_Schedule_Tab =  By.xpath("//*[contains(@content-desc,'Schedule view')]");
    private final By Calendar_Delete_Event_Button =  By.id("android:id/button1");
    public void clickAddEvent(){
        findByWithWaitForSeconds(Calendar_Actions_Button).click();
        findByWithWaitForSeconds(Calendar_Event_Button).click();
    }

    public void deleteEventFromSchedule(String eventName) {
        findByWithWaitForSeconds(Calendar_Left_Sidebar).click();
        findByWithWaitForMinute(Calendar_Schedule_Tab).click();
        var eventElements = driver.findElements(By.xpath("//*[contains(@content-desc, '" + eventName + "')]"));

        if(!eventElements.isEmpty()) {
            for (var eventElement :
                    eventElements) {
                var startY = eventElement.getLocation().getY();
                int startX = eventElement.getLocation().getX();
                var width = eventElement.getSize().getWidth();
                var height = eventElement.getSize().getHeight();
                ((JavascriptExecutor) driver).executeScript("mobile: swipeGesture", ImmutableMap.of(
                        "left", startX, "top", startY, "width", width, "height", height,
                        "direction", "right",
                        "percent", 1
                ));

                findByWithWaitForSeconds(Calendar_Delete_Event_Button).click();
            }
        }
    }
}
