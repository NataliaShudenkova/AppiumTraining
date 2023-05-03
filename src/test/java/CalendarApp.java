import Screens.CalendarScreen;
import Screens.EventScreen;
import Screens.NotificationScreen;
import Screens.WelcomeCalendarScreen;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CalendarApp extends TestBase {
    private AndroidDriver driver;
    WelcomeCalendarScreen welcomeCalendarScreen;
    CalendarScreen calendarScreen;
    EventScreen eventScreen;
    NotificationScreen notificationScreen;

    String eventName;



    @Before
    public void pageSetUp() throws MalformedURLException {
        driver = driverSetUp();
        welcomeCalendarScreen = new WelcomeCalendarScreen(driver);
        calendarScreen = new CalendarScreen(driver);
        eventScreen = new EventScreen(driver);
        notificationScreen = new NotificationScreen(driver);
    }

    @Test
    public void testMethod() throws InterruptedException {
        //Verify if Welcome window displayed.
        if(welcomeCalendarScreen.isWelcomeCalendarScreen())
        {
            welcomeCalendarScreen.NavigateToCalendar();
        }

        calendarScreen.clickAddEvent();

        //calculate default Time in control in accordance with current OffsetTime for getting control path
        OffsetTime startTimeButtonValue;

        if(OffsetTime.now().getMinute() < 30){
            startTimeButtonValue = OffsetTime.now(ZoneOffset.UTC).minusMinutes(OffsetTime.now().getMinute()).plusMinutes(30);
        }
        else{
            startTimeButtonValue = OffsetTime.now(ZoneOffset.UTC).minusMinutes(OffsetTime.now().getMinute()).plusMinutes(60);
        }

        //Set Event Start Time and End Time
        var startTimeForEvent = OffsetTime.now(ZoneOffset.UTC).plusHours(1);
        var endTimeButtonValue = startTimeForEvent.plusHours(1);
        var endTimeForEvent = endTimeButtonValue.plusMinutes(30);
        var defaultTimeButtonValues = new ArrayList<OffsetTime>();
        defaultTimeButtonValues.add(startTimeButtonValue);
        defaultTimeButtonValues.add(endTimeButtonValue);
        var desiredEventTime = new ArrayList<OffsetTime>();
        desiredEventTime.add(startTimeForEvent);
        desiredEventTime.add(endTimeForEvent);

        //Create Event with location
        eventName = "New Test Event 1";
        var locationSearchParam = "Vilnius";
        var locationResultParam = "Vilnius, Vilnius City Municipality";
        eventScreen.createEventWithLocation(eventName, defaultTimeButtonValues, desiredEventTime, driver,
                locationSearchParam, locationResultParam );

        var formattedStartTimeValue = startTimeForEvent.format(DateTimeFormatter.ofPattern("h:mm a"));
        var formattedEndTimeValue = endTimeForEvent.format(DateTimeFormatter.ofPattern("h:mm a"));

        //Verify: the Event is displayed in Calendar with proper time and location
        Assert.assertTrue("The event is not found or event qty is more than one.",
                calendarScreen.tryFindElement(By.xpath("//*[contains(@content-desc,'" + eventName +"')]")));
        var actualEventResult = driver.findElement(By.xpath("//*[contains(@content-desc,'" + eventName +"')]")).getAttribute("content-desc");
        Assert.assertEquals("The event name, or time, or location is not expected.",
                eventName +
                        ", " + formattedStartTimeValue + " – " + formattedEndTimeValue + ", Vilnius, Vilnius City Municipality, Lithuania", actualEventResult);
    }

    @Test
    public void calendarNotification() throws InterruptedException{
        if(welcomeCalendarScreen.isWelcomeCalendarScreen())
        {
            welcomeCalendarScreen.NavigateToCalendar();
        }

        calendarScreen.clickAddEvent();

        //calculate default Time in control in accordance with current OffsetTime for getting control path
        OffsetTime startTimeButtonValue;

        if(OffsetTime.now().getMinute() < 30){
            startTimeButtonValue = OffsetTime.now(ZoneOffset.UTC).minusMinutes(OffsetTime.now().getMinute()).plusMinutes(30);
        }
        else{
            startTimeButtonValue = OffsetTime.now(ZoneOffset.UTC).minusMinutes(OffsetTime.now().getMinute()).plusMinutes(60);
        }

        //Set desired Event Start Time and End Time in Time Controls
        var startTimeForEvent = OffsetTime.now(ZoneOffset.UTC).plusMinutes(2);
        var endTimeButtonValue = startTimeForEvent.plusHours(1);
        var endTimeForEvent = startTimeForEvent.plusMinutes(1);
        var defaultTimeButtonValues = new ArrayList<OffsetTime>();
        defaultTimeButtonValues.add(startTimeButtonValue);
        defaultTimeButtonValues.add(endTimeButtonValue);
        var desiredEventTime = new ArrayList<OffsetTime>();
        desiredEventTime.add(startTimeForEvent);
        desiredEventTime.add(endTimeForEvent);

        //Create Event with desired time
        eventName = "New Test Event 2";
        eventScreen.createEvent(eventName, defaultTimeButtonValues, desiredEventTime, driver);

        //Open Notification page
        driver.openNotifications();
       // Verify the Calendar Notification is displayed with correct Name and Time
        Assert.assertTrue("Event Name is not found.",  notificationScreen.getElementText(By.id("android:id/title")).contains(eventName));
        var formattedStartTimeValue = startTimeForEvent.format(DateTimeFormatter.ofPattern("h:mm"));
        var formattedEndTimeValue = endTimeForEvent.format(DateTimeFormatter.ofPattern("h:mm a"));
        Assert.assertTrue("Incorrect time", notificationScreen.getElementText(By.id("android:id/big_text")).contains(formattedStartTimeValue + " – " + formattedEndTimeValue));
        driver.pressKey(new KeyEvent(AndroidKey.BACK));
    }

    @After
    public void driverTearDown() {
        //deleteEvent();
        calendarScreen.deleteEventFromSchedule(eventName);
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
    }

    /*private void deleteEvent() throws InterruptedException {
        var eventElements = driver.findElements(By.xpath("//*[contains(@content-desc, '" + eventName + "')]"));

        if(!eventElements.isEmpty()){
            for (var eventElement:
                    eventElements) {
                eventElement.click();
                Thread.sleep(5000);driver.findElement(AppiumBy.accessibilityId("More options")).click();
                Thread.sleep(3000);

                //Delete the event
                driver.findElement(By.xpath("//*[@text= 'Delete']")).click();
                Thread.sleep(3000);
                driver.findElement(By.id("android:id/button1")).click();
                Thread.sleep(3000);
            }
        }
    }*/

}

