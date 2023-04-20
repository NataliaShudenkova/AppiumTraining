import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class CalendarApp {
    AndroidDriver driver;
    String eventName;
    @Before
    public void driverSetUp() throws MalformedURLException {
        URL driverUrl = new URL("http://0.0.0.0:4723/wd/hub");
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("unlockType", "pin");
        caps.setCapability("unlockKey","1111");
        caps.setCapability("udid","emulator-5554");
        caps.setCapability("noReset",true);
        caps.setCapability("avd","Pixel_6_API_33_Android_13_2");
        caps.setCapability("avdLaunchTimeout","120000");
        caps.setCapability("avdReadyTimeout","60000");
        caps.setCapability("appPackage", "com.google.android.calendar");
        caps.setCapability("appActivity","com.android.calendar.event.LaunchInfoActivity");
        driver = new AndroidDriver(driverUrl, caps);
    }

    @Test
    public void testMethod() throws InterruptedException {

        //Verify if Welcome window displayed.
        var welcomeWindow = driver.findElements(By.xpath("//*[@content-desc='Welcome to Google Calendar']"));
        if(!welcomeWindow.isEmpty())
        {
            driver.findElement(By.id("next_arrow")).click();
            driver.findElement(By.id("oobe_done_button")).click();
            Thread.sleep(5000);
        }

        driver.findElement(By.id("floating_action_button")).click();
        Thread.sleep(2000);

        //Add Event
        driver.findElement(AppiumBy.accessibilityId("Event button")).click();
        Thread.sleep(2000);
        eventName = "New Test Event";
        driver.findElement(By.id("title")).sendKeys(eventName);
        driver.findElement(By.id("title")).click();
        if(driver.isKeyboardShown()) driver.pressKey(new KeyEvent(AndroidKey.BACK));

        //Set Event Start Time and End Time
        OffsetTime startTimeButtonValue;

        if(OffsetTime.now().getMinute() < 30){
            startTimeButtonValue = OffsetTime.now(ZoneOffset.UTC).minusMinutes(OffsetTime.now().getMinute()).plusMinutes(30);
        }
        else{
            startTimeButtonValue = OffsetTime.now(ZoneOffset.UTC).minusMinutes(OffsetTime.now().getMinute()).plusMinutes(60);
        }

        var startTimeForEvent = OffsetTime.now(ZoneOffset.UTC).plusHours(1);
        setTime(true, startTimeButtonValue, startTimeForEvent);
        Thread.sleep(5000);
        var endTimeButtonValue = startTimeForEvent.plusHours(1);
        var endTimeForEvent = endTimeButtonValue.plusMinutes(30);
        setTime(false, endTimeButtonValue, endTimeForEvent);
        Thread.sleep(2000);
        driver.findElement(By.id("save")).click();
        Thread.sleep(5000);
        var formattedStartTimeValue = startTimeForEvent.format(DateTimeFormatter.ofPattern("h:mm a"));
        var formattedEndTimeValue = endTimeForEvent.format(DateTimeFormatter.ofPattern("h:mm a"));

        //Verify: the Evens is displayed in Calendar
        Assert.assertTrue("The event is not found or event qty is more than one.",
                TryFindElement("//*[contains(@content-desc,'" + eventName +"')]"));
        var actualEventResult = driver.findElement(By.xpath("//*[contains(@content-desc,'" + eventName +"')]")).getAttribute("content-desc");
        Assert.assertEquals("The event name or time is not expected.",
                eventName +
                        ", " + formattedStartTimeValue + " – " + formattedEndTimeValue, actualEventResult);
    }

    private boolean TryFindElement(String path){
        var elements = driver.findElements(By.xpath(path));
        if(!elements.isEmpty() && elements.size() == 1){
            return true;
        }else{
            return false;
        }
    }

    private void setTime(boolean isStartTime, OffsetTime timeButtonValue, OffsetTime eventTime) throws InterruptedException {
        var formattedTimeButtonValue = timeButtonValue.format(DateTimeFormatter.ofPattern("h:mm a"));
        var timeButton = driver.findElement(AppiumBy.accessibilityId(isStartTime ? "Start time: " + formattedTimeButtonValue : "End time: " + formattedTimeButtonValue));
        timeButton.click();
        Thread.sleep(3000);
        driver.findElement(AppiumBy.accessibilityId("Switch to text input mode for the time input.")).click();
        Thread.sleep(3000);
        var eventHoursValue = eventTime.format(DateTimeFormatter.ofPattern("h"));
        var eventMinutesValue = Integer.toString(eventTime.getMinute());
        var eventAmPmValue = eventTime.format(DateTimeFormatter.ofPattern("a"));
        var hourField = driver.findElement(By.id("android:id/input_hour"));
        hourField.sendKeys(eventHoursValue);
        driver.findElement(By.id("android:id/input_minute")).sendKeys(eventMinutesValue);
        driver.findElement(By.className("android.widget.CheckedTextView")).click();
        Thread.sleep(2000);

        if(eventAmPmValue.contains("AM")){
            driver.findElement(By.xpath("//*[@text = 'AM']")).click();
        }else{
            driver.findElement(By.xpath("//*[@text = 'PM']")).click();
        }

        driver.findElement(new AppiumBy.ByAndroidUIAutomator("new UiSelector().text(\"OK\")")).click();
    }

    private void deleteEvent() throws InterruptedException {
        var eventElements = driver.findElements(By.xpath("//*[contains(@content-desc, '" + eventName + "')]"));

        if(!eventElements.isEmpty()){
            for (var eventElement:
                    eventElements) {
                eventElement.click();
                Thread.sleep(5000);
                driver.findElement(AppiumBy.accessibilityId("More options")).click();
                Thread.sleep(3000);

                //Delete the event
                driver.findElement(By.xpath("//*[@text= 'Delete']")).click();
                Thread.sleep(3000);
                driver.findElement(By.id("android:id/button1")).click();
                Thread.sleep(3000);
            }
        }
    }

    @After
    public void driverTearDown() throws InterruptedException, IOException {
        deleteEvent();
        driver.quit();
        //kill emulator
        try{
            var newDir = "D:\\AndroidSdk\\platform-tools";
            var killCommand = "cmd.exe /c start .\\adb.exe -s emulator-5554 emu kill";
            var builder = Runtime.getRuntime();
            var process = builder.exec(killCommand, null, new File(newDir));
            process.waitFor(4000, TimeUnit.MILLISECONDS);
        }catch (IOException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }


        //

    }
}

