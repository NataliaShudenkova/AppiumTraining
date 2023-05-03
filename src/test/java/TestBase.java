import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public  class TestBase {

    private AndroidDriver driver;

    public AndroidDriver driverSetUp() throws MalformedURLException {
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
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        return driver;
    }
}
