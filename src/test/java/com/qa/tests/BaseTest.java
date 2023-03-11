package com.qa.tests;

import com.qa.utils.TestUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidStartScreenRecordingOptions;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.annotations.Optional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.URL;
import java.time.Duration;
import java.util.*;
//import java.util.logging.LogManager;

// command to change the port no is "appium -p port_no"
/*  command to find the process id running on the specific port.
        netstat -ano|findstr "PID :4723"    */
/*
 *   Command to kill the process running on the specific port.
 * taskkill /pid 16680 /f
 * */

// for mac
// lsof -i :4723 this command will show the process id(PID) and then use Kill -QUIT PID


public class BaseTest {
    static Logger logger = LogManager.getLogger(BaseTest.class.getName());
    protected static ThreadLocal<AppiumDriver> driver = new ThreadLocal<AppiumDriver>();
    protected static ThreadLocal<Properties> props = new ThreadLocal<Properties>();
    protected static ThreadLocal<String> platform = new ThreadLocal<String>();
    protected static ThreadLocal<String> dateTime = new ThreadLocal<String>();
    protected static ThreadLocal<String> deviceName = new ThreadLocal<String>();
    private static AppiumDriverLocalService server;

    @BeforeSuite
    public void beforeSuite() throws Exception, Exception {
        ThreadContext.put("ROUTINGKEY", "ServerLogs");
//		server = getAppiumService(); // -> If using Mac, uncomment this statement and comment below statement
//        server = getAppiumServerDefault(); // -> If using Windows, uncomment this statement and comment above statement

//        if (!checkIfAppiumServerIsRunnning(4723)) {
//            server.start();
//            server.clearOutPutStreams(); // -> Comment this if you want to see server logs in the console
//            logger.info("Appium Server started");
//        } else {
//            logger.info("Appium server is already running");
//        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
//        if (server.isRunning()) {
//            server.stop();
//            logger.info("Appium server stopped");
//        }
    }

    public boolean checkIfAppiumServerIsRunnning(int port) throws Exception {
        boolean isAppiumServerRunning = false;
        ServerSocket socket;
        try {
            socket = new ServerSocket(port);
            socket.close();
        } catch (IOException e) {
            System.out.println("1");
            isAppiumServerRunning = true;
        } finally {
            socket = null;
        }
        return isAppiumServerRunning;
    }

    // for Windows
    public AppiumDriverLocalService getAppiumServerDefault() {
        return AppiumDriverLocalService.buildDefaultService();
    }

    // Below code also works for windows.
    public AppiumDriverLocalService getAppiumServerCustom() {
        return AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
                .usingDriverExecutable(new File("C:\\Program Files\\nodejs\\node.exe"))
                .withAppiumJS(new File("C:\\Users\\shreyansh.jain\\AppData\\Roaming\\npm\\node_modules\\appium\\build\\lib\\main.js"))
                .usingPort(4723)
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withLogFile(new File("ServerLogs/server.log")));
    }


    // for Mac. Update the paths as per your Mac setup
    public AppiumDriverLocalService getAppiumService() {
        HashMap<String, String> environment = new HashMap<String, String>();
        environment.put("PATH", "enter_your_path_here" + System.getenv("PATH"));
        environment.put("ANDROID_HOME", "enter_android_home_path");
        return AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
                .usingDriverExecutable(new File("/usr/local/bin/node"))
                .withAppiumJS(new File("/usr/local/lib/node_modules/appium/build/lib/main.js"))
                .usingPort(4723)
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
//				.withArgument(() -> "--allow-insecure","chromedriver_autodownload")
                .withEnvironment(environment)
                .withLogFile(new File("ServerLogs/server.log")));
    }


    InputStream inputStream;

    public BaseTest() {
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }

    TestUtils utils = new TestUtils();


    public AppiumDriver getDriver() {
        return driver.get();
    }
    // command to get the app activity and app package.
//        adb shell "dumpsys window | grep -E mCurrentFocus"

    public void setDriver(AppiumDriver driver2) {
        driver.set(driver2);
    }

    public Properties getProps() {
        return props.get();
    }

    public void setProps(Properties props2) {
        props.set(props2);
    }

    public String getPlatform() {
        return platform.get();
    }

    public void setPlatform(String platform2) {
        platform.set(platform2);
    }

    public String getDateTime() {
        return dateTime.get();
    }

    public void setDateTime(String dateTime2) {
        dateTime.set(dateTime2);
    }

    public String getDeviceName() {
        return deviceName.get();
    }

    public void setDeviceName(String deviceName2) {
        deviceName.set(deviceName2);
    }


    @BeforeMethod
    public void beforeMethodInTest() {
        ((CanRecordScreen) getDriver()).startRecordingScreen(
                new AndroidStartScreenRecordingOptions().withVideoSize("1280x720"));
        //  ((CanRecordScreen) driver).startRecordingScreen(IOSStartScreenRecordingOptions.startScreenRecordingOptions().withVideoType("mpeg4"));
    }

    //stop video capturing and create *.mp4 file
    @AfterMethod
    public synchronized void afterMethod(ITestResult result) throws Exception {

        System.out.println("After Method...");
        String media = ((CanRecordScreen) getDriver()).stopRecordingScreen();

        Map<String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();
        String dirPath = "videos" + File.separator + params.get("platformName") + "_" + params.get("deviceName")
                + File.separator + getDateTime() + File.separator + result.getTestClass().getRealClass().getSimpleName();

        File videoDir = new File(dirPath);
        dirPath = dirPath + File.separator + result.getName() + ".mp4";
        synchronized (videoDir) {
            if (!videoDir.exists()) {
                videoDir.mkdirs();
            }
        }

        FileOutputStream stream = null;
        try {
        /*    byte[] decode = java.util.Base64.getDecoder().decode(media);
            FileUtils.writeByteArrayToFile(new File(dirPath), decode);*/ // this code also works,.
            byte[] decode = Base64.getDecoder().decode(media);
            stream = new FileOutputStream(videoDir + File.separator + result.getName() + ".mp4");
            stream.write(decode);
            stream.close();
        } catch (Exception e) {
            System.out.println("exception...");
            e.printStackTrace();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }


    @BeforeTest
    @Parameters({"emulator", "platformName", "udid", "deviceName", "systemPort",
            "chromeDriverPort", "wdaLocalPort", "webkitDebugProxyPort"})
    public void beforeTest(@Optional("androidOnly") String emulator, String platformName, String udid, String deviceName,
                           @Optional("androidOnly") String systemPort, @Optional("androidOnly") String chromeDriverPort,
                           @Optional("iOSOnly") String wdaLocalPort, @Optional("iOSOnly") String webkitDebugProxyPort) throws Exception {
        /* ALL < DEBUG < INFO < WARN < ERROR < FATAL < OFF     */

        String filePath = "logs" + File.separator + platformName + "_" + deviceName;
        File logFile = new File(filePath);
        if (!logFile.exists()) {
            logFile.mkdirs();
        }
        ThreadContext.put("ROUTINGKEY", filePath);
        try {
            setDateTime(utils.dateTime());
            setPlatform(platformName);
            setDeviceName(deviceName);
            URL url;
            InputStream inputStream = null;
            Properties properties = new Properties();
            AppiumDriver driver;
            inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
            properties.load(inputStream);
            setProps(properties);

            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability(MobileCapabilityType.PLATFORM_NAME, platformName);
            caps.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
            caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, properties.get("androidAutomationName"));
            caps.setCapability(MobileCapabilityType.UDID, udid);
            caps.setCapability("appPackage", properties.getProperty("androidAppPackage"));
            caps.setCapability("appActivity", properties.getProperty("androidAppActivity"));
//            caps.setCapability("systemPort",systemPort);
            String appLocation = getClass().getResource(properties.getProperty("androidAppLocation")).getFile(); // this works.
            System.out.println("appLocation " + appLocation);
            String appUrl = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
                    + File.separator + "resources" + File.separator + "ApiDemos-debug.apk";
            caps.setCapability(MobileCapabilityType.APP, appLocation.substring(1));

            url = new URL(properties.getProperty("appiumURL"));
            driver = new AndroidDriver(url, caps);
            setDriver(driver);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public void waitForVisibility(WebElement e) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(TestUtils.WAIT));
        wait.until(ExpectedConditions.visibilityOf(e));

    }

    public void clear(WebElement e) {
        waitForVisibility(e);
        e.clear();
    }

    public void click(WebElement e) {
        waitForVisibility(e);
        e.click();
    }

    public void click(WebElement e, String msg) {
        waitForVisibility(e);
        e.click();
    }

    public void sendKeys(WebElement e, String txt) {
        waitForVisibility(e);
        e.sendKeys(txt);
    }

    public void sendKeys(WebElement e, String txt, String msg) {
        waitForVisibility(e);
        e.sendKeys(txt);
    }

    public String getAttribute(WebElement e, String attribute) {
        waitForVisibility(e);
        return e.getAttribute(attribute);
    }

    public String getText(WebElement e, String msg) {
        String txt = getAttribute(e, "text");
        return txt;
    }

    public void closeApp() {
        ((InteractsWithApps) getDriver()).terminateApp(getProps().getProperty("androidAppPackage"));
    }

    public void launchApp() {
        ((InteractsWithApps) getDriver()).activateApp(getProps().getProperty("androidAppPackage"));
    }

    public WebElement scrollToElement() {
        return getDriver().findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector()" + ".scrollable(true)).scrollIntoView("
                        + "new UiSelector().description(\"test-Price\"));"));
    }
//    public void iOSScrollToElement() {
//	  RemoteWebElement element = (RemoteWebElement)getDriver().findElement(By.name("test-ADD TO CART"));
//	  String elementID = element.getId();
//        HashMap<String, String> scrollObject = new HashMap<String, String>();
//	  scrollObject.put("element", elementID);
//        scrollObject.put("direction", "down");
//	  scrollObject.put("predicateString", "label == 'ADD TO CART'");
//	  scrollObject.put("name", "test-ADD TO CART");
//	  scrollObject.put("toVisible", "sdfnjksdnfkld");
//        getDriver().executeScript("mobile:scroll", scrollObject);


      /*  driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollTextIntoView(\"WebView\")"));

        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector()." +
                "text(\"Animation\"))")).click();
                */

//    click based on the co-ordinates.
   /*
    public static void action_clickOnPosition(AppiumDriver driver, int pointA_X, int pointA_Y) {

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        org.openqa.selenium.interactions.Sequence clickPosition = new org.openqa.selenium.interactions.Sequence(finger, 1);
        clickPosition.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), pointA_X, pointA_Y)).addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg())).addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Arrays.asList(clickPosition));
    }
    */

}

