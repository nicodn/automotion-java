package util.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static util.driver.CapabilitiesFactory.getCapabilities;
import static environment.EnvironmentFactory.*;

public class WebDriverFactory {
    private final Logger LOG = LoggerFactory.getLogger(WebDriverFactory.class);

    private String remoteUrlPath;
    private AppiumDriver appiumDriver;
    private WebDriver driver;
    private WebDriver webDriver;
    private RemoteWebDriver remoteWebDriver;
    private DesiredCapabilities capabilities;

    public WebDriverFactory() {
        capabilities = getCapabilities();
        LOG.info("Using capabilities: " + capabilities.toString());
        remoteUrlPath = getRemoteUrlPath();
    }

    public WebDriver getDriver() {
        if (isMobile()) {
            driver = getMobileDriver();
            LOG.info("Start Mobile driver");
        } else if (isLocal()) {
            driver = getLocalWebDriver();
            LOG.info("Start Local web driver");
        } else if (isRemote()) {
            driver = getRemoteWebDriver();
            LOG.info("Start Mobile driver");
        }

        return driver;
    }

    public void updateCapabilities(Map<String, Object> mapCapabilities){
        CapabilitiesFactory.updateCapabilities(capabilities, mapCapabilities);
    }

    private AppiumDriver getMobileDriver() {
        if (isAndroid()) {
            try {
                appiumDriver = new AndroidDriver(new URL(getRemoteUrlPath()), capabilities);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else if (isIOS()) {
            try {
                appiumDriver = new IOSDriver(new URL(getRemoteUrlPath()), capabilities);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return appiumDriver;
    }

    private WebDriver getLocalWebDriver() {
        if (isFirefox()) {
            webDriver = new FirefoxDriver();
        } else if (isChrome()) {
            setChromeDriver();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            webDriver = new ChromeDriver(options);
        } else if (isSafari()) {
            webDriver = new SafariDriver();
        } else if (isInternetExplorer()) {
            setIEDriver();
            webDriver = new InternetExplorerDriver();
        }

        return webDriver;
    }

    private RemoteWebDriver getRemoteWebDriver() {
        try {
            remoteWebDriver = new RemoteWebDriver(new URL(remoteUrlPath), capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return remoteWebDriver;
    }

    private static void setChromeDriver() {
        Platform platform = Platform.getCurrent();
        String chromeBinary = "src/main/resources/drivers/chromedriver"
                + (platform.toString().toUpperCase().contains("WIN") ? ".exe" : "");
        System.setProperty("webdriver.chrome.driver", chromeBinary);
    }

    private static void setIEDriver() {
        Platform platform = Platform.getCurrent();
        String chromeBinary = "src/main/resources/drivers/IEDriverServer"
                + (platform.toString().toUpperCase().contains("WIN") ? ".exe" : "");
        System.setProperty("webdriver.ie.driver", chromeBinary);
    }
}
