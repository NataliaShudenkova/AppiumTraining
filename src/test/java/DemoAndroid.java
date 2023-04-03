import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.MalformedURLException;
import java.net.URL;

public class DemoAndroid
{
    AndroidDriver driver;
    @Before
    public void driverSetUp() throws MalformedURLException {
        URL driverUrl = new URL("http://0.0.0.0:4723/wd/hub");
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("udid","emulator-5554");
        caps.setCapability("appPackage", "com.google.android.apps.messaging");
        caps.setCapability("appActivity",".ui.ConversationListActivity");
        driver = new AndroidDriver(driverUrl, caps);
    }

    @Test
    public void testMethod() throws InterruptedException {
        driver.findElement(By.id("federated_learning_popup_positive_button")).click();
        driver.findElement(By.id("start_chat_fab")).click();
        Thread.sleep(3000);
        driver.findElement(By.id("recipient_text_view")).sendKeys("345");
        driver.findElement(By.id("recipient_text_view")).click();
        if(driver.isKeyboardShown()) driver.pressKey(new KeyEvent(AndroidKey.ENTER));
        Thread.sleep(3000);
        driver.findElement(By.id("compose_message_text")).sendKeys("Hello!!!");
        driver.findElement(By.id("send_message_button_icon")).click();
        Thread.sleep(3000);
        var actualSentText = driver.findElement(By.id("message_text")).getText();
        Assert.assertEquals("The message text is not expected","Hello!!!", actualSentText );
    }

    @After
    public void driverTearDown(){
        driver.quit();
    }
}
