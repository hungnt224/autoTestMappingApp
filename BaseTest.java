package framework;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    public static WebDriver driver;
    public static Library library;
    public Process qtAppProcess;

    String os = System.getProperty("os.name").toLowerCase();

    @BeforeMethod
    public void setup() {
        if (os.contains("win")) {
            // --- CHẠY TRÊN WINDOWS: CẦN DRIVER ---
            System.setProperty("webdriver.chrome.driver", "C:\\path\\to\\chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            driver = new ChromeDriver(options);

            // Khởi tạo WebUI static
            new WebUI(driver);
        } else {
            // --- CHẠY TRÊN UBUNTU: KHÔNG CẦN DRIVER ---
            driver = null;
            setupUbuntuQt();
        }

        // QUAN TRỌNG: Dù driver có null hay không, vẫn phải khởi tạo library
        // để Engine có cái mà gọi library.commands
        library = new Library(driver);
    }

    private void setupUbuntuQt() {
        try {
            String appDir = "/home/hungnt/Desktop/my_app_folder";
            String appName = "./OSM-App";
            String sudoPass = "1";
            String fullCommand = String.format("cd %s && echo %s | sudo -S %s", appDir, sudoPass, appName);

            ProcessBuilder pb = new ProcessBuilder("sh", "-c", fullCommand);
            pb.environment().put("DISPLAY", ":0");
            qtAppProcess = pb.start();
        } catch (Exception e) {
            System.err.println("Lỗi mở App Qt: " + e.getMessage());
        }
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) driver.quit();
        if (qtAppProcess != null) qtAppProcess.destroy();
    }
}