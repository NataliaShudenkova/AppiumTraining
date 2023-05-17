import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class TestBase {

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
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(6));
        return driver;
    }

   @Attachment(value = "Failed Test Screenshot",type = "image/png")
   @Step("Add screenshot if test is failed.")
    public byte[] takeScreenshotToAttachOnAllureReport(AndroidDriver driver) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
        Date date = new Date();
        String fileName = sdf.format(date);
        File SrcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(SrcFile, new File(System.getProperty("user.dir")+"//allure-results//"+fileName+".png"));
        return ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
    }


    @Attachment(value = "Environment", fileExtension = ".txt")
    @Step("Add environment file if test is failed")
    public static byte[] getBytesAnnotationWithArgs() throws IOException {
        return Files.readAllBytes(Paths.get("C:\\AppiumLecture1\\allure-results", "environment.properties"));
    }
}
