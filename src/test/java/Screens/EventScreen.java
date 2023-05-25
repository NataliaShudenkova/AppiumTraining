package Screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventScreen extends BaseScreen{
    private AppiumDriver driver;

    public EventScreen(AppiumDriver driver){
        super(driver);
        this.driver = driver;
    }

    private final By Event_Title_Field =  By.id("title");
    private final By Event_Switch_ToTimeInputMode_Button =  AppiumBy.accessibilityId("Switch to text input mode for the time input.");
    private final By Event_Hour_Field =  By.id("android:id/input_hour");
    private final By Event_Minute_Field =  By.id("android:id/input_minute");
    private final By Event_AM_PM_Dropdown = By.className("android.widget.CheckedTextView");
    private final By Event_Time_Ok_Button = new AppiumBy.ByAndroidUIAutomator("new UiSelector().text(\"OK\")");

    private final By Event_Add_Location_Button = By.xpath("//*[contains(@text, 'location')]");
    private final By Event_AutoDetect_Location_Popup = By.id("com.android.permissioncontroller:id/permission_message");
    private final By Event_AutoDetect_Location_Deny_Button = By.id("com.android.permissioncontroller:id/permission_deny_and_dont_ask_again_button");

    private final By Event_Location_Search_Field = By.id("search_text");
    private final By Event_Save_Button =  By.id("save");


    @Step("Create event")
    public void createEvent(String eventName, List<OffsetTime> timeButtonValue,
                            List<OffsetTime> eventTime, AndroidDriver androidDriver) throws InterruptedException {
        findByWithWaitForSeconds(Event_Title_Field).sendKeys(eventName);
        driver.findElement(By.id("title")).click();
        if(androidDriver.isKeyboardShown()) androidDriver.pressKey(new KeyEvent(AndroidKey.BACK));
        setTime(true, timeButtonValue.get(0), eventTime.get(0));
        setTime(false, timeButtonValue.get(1), eventTime.get(1));
        findByWithWaitForSeconds(Event_Save_Button).click();
    }

    @Step("Create event with location.")
    public void createEventWithLocation(String eventName, List<OffsetTime> timeButtonValue,
                                        List<OffsetTime> eventTime, AndroidDriver androidDriver,
                                        String locationSearch, String locationResult) throws InterruptedException {
        findByWithWaitForSeconds(Event_Title_Field).sendKeys(eventName);
        driver.findElement(By.id("title")).click();
        if(androidDriver.isKeyboardShown()) androidDriver.pressKey(new KeyEvent(AndroidKey.BACK));
        setTime(true, timeButtonValue.get(0), eventTime.get(0));
        setTime(false, timeButtonValue.get(1), eventTime.get(1));
        setLocation(locationSearch, locationResult);
        findByWithWaitForSeconds(Event_Save_Button).click();
    }

    @Step("Set location.")
    private void setLocation(String locationSearch, String locationResult) {
        findByWithWaitForSeconds(Event_Add_Location_Button).click();
        if(tryFindElement(Event_AutoDetect_Location_Popup)){
            findByWithWaitForSeconds(Event_AutoDetect_Location_Deny_Button).click();
        }
        findByWithWaitForSeconds(Event_Location_Search_Field).sendKeys(locationSearch);
        findByWithWaitForSeconds(By.xpath("//*[@text = '"+ locationResult + "']")).click();
    }

    @Step("Set time")
    private void setTime(boolean isStartTime, OffsetTime timeButtonValue, OffsetTime eventTime) throws InterruptedException {
        var formattedTimeButtonValue = timeButtonValue.format(DateTimeFormatter.ofPattern("h:mm a"));
        var timeButton = findByWithWaitForSeconds(AppiumBy.accessibilityId(isStartTime ? "Start time: " + formattedTimeButtonValue : "End time: " + formattedTimeButtonValue));
        timeButton.click();
        findByWithWaitForSeconds(Event_Switch_ToTimeInputMode_Button).click();
        var eventHoursValue = eventTime.format(DateTimeFormatter.ofPattern("h"));
        var eventMinutesValue = Integer.toString(eventTime.getMinute());
        var eventAmPmValue = eventTime.format(DateTimeFormatter.ofPattern("a"));
        findByWithWaitForSeconds(Event_Hour_Field).sendKeys(eventHoursValue);
        driver.findElement(Event_Minute_Field).sendKeys(eventMinutesValue);
        driver.findElement(Event_AM_PM_Dropdown).click();
        Thread.sleep(2000);

        if(eventAmPmValue.contains("AM")){
            findByWithWaitForSeconds(By.xpath("//*[@text = 'AM']")).click();
        }else{
            findByWithWaitForSeconds(By.xpath("//*[@text = 'PM']")).click();
        }

        driver.findElement(Event_Time_Ok_Button).click();
    }
}
