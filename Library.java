package framework;

import org.openqa.selenium.*;
import org.sikuli.script.*;
import org.sikuli.script.Screen;
import org.sikuli.script.FindFailed;
import java.util.*;
import java.util.function.Consumer;

public class Library {
    public WebDriver driver;
    public Screen screen = new Screen(); // Đối tượng chính của Sikuli để quét ảnh màn hình

    // Bản đồ điều hướng: Map giữa Action trong JSON và hàm xử lý trong code
    public Map<String, Consumer<Models.Step>> commands = new HashMap<>();

    public Library(WebDriver driver) {
        this.driver = driver;
        // Chỉ truyền driver vào WebUI nếu nó thực sự tồn tại
        if (this.driver != null) {
            new WebUI(this.driver);
        }
        initCommands();
    }

    private void initCommands() {
        // ==========================================
        // NHÓM 1: CÁC LỆNH WEB (Dùng WebUI cũ của bạn)
        // ==========================================
        commands.put("web.open", s -> WebUI.openURL(s.target));
        commands.put("web.click", s -> WebUI.clickElement(parseLocator(s.target)));
        commands.put("web.type", s -> WebUI.setText(parseLocator(s.target), s.value));
        commands.put("web.hover", s -> WebUI.mouseHover(parseLocator(s.target)));
        commands.put("web.scroll", s -> WebUI.scrollToElement(parseLocator(s.target)));
        commands.put("web.sleep", s -> WebUI.sleep(Double.parseDouble(s.value)));

        // Nhóm Verify (Kiểm tra kết quả)
        commands.put("web.verify_text", s -> {
            String act = WebUI.getElementText(parseLocator(s.target));
            WebUI.verifyEquals(act, s.expected);
        });

        commands.put("web.verify_exist", s -> {
            boolean isExist = WebUI.checkElementExist(parseLocator(s.target));
            WebUI.verifyEquals(isExist, Boolean.parseBoolean(s.expected));
        });

        // Nhóm Wait (Đợi)
        commands.put("web.wait_visible", s -> WebUI.waitForElementVisible(parseLocator(s.target)));
        commands.put("web.wait_clickable", s -> WebUI.waitForElementClickable(parseLocator(s.target)));

        // ==========================================
        // NHÓM 2: CÁC LỆNH DESKTOP (Sikuli - Cho App Qt)
        // ==========================================
        // Click vào một tấm ảnh trên màn hình
        commands.put("desktop.click", s -> {
            try { screen.click(s.target); }
            catch (FindFailed e) { throw new RuntimeException("Sikuli: Không tìm thấy ảnh " + s.target); }
        });

        // Nhập liệu vào một ô input (dựa trên ảnh nhận diện)
        commands.put("desktop.type", s -> {
            try {
                screen.type(s.target, s.value);
            } catch (Exception e) {
                throw new RuntimeException("Sikuli Error: Không nhập được vào " + s.target);
            }
        });

        // Double click
        commands.put("desktop.double_click", s -> {
            try { screen.doubleClick(s.target); }
            catch (FindFailed e) { throw new RuntimeException("Sikuli: Lỗi Double Click ảnh " + s.target); }
        });

        // Kiểm tra ảnh có hiển thị hay không (Expected: true/false)
        commands.put("desktop.verify_visible", s -> {
            boolean isFound = (screen.exists(s.target, 5.0) != null);
            if (isFound != Boolean.parseBoolean(s.expected)) {
                throw new AssertionError("Sikuli Verify Fail! Expected visibility: " + s.expected);
            }
        });

        // ==========================================
        // NHÓM 3: CÁC PHÍM HỆ THỐNG (Robot Keys)
        // ==========================================
        commands.put("key.enter", s -> WebUI.pressENTER());
        commands.put("key.esc", s -> WebUI.pressESC());
    }

    /**
     * Hàm quan trọng: Chuyển đổi String từ JSON thành đối tượng By của Selenium
     * Hỗ trợ: id, xpath, css, name
     */
    private By parseLocator(String loc) {
        if (loc == null || loc.isEmpty()) return null;
        if (loc.startsWith("id:")) return By.id(loc.substring(3));
        if (loc.startsWith("xpath:")) return By.xpath(loc.substring(6));
        if (loc.startsWith("css:")) return By.cssSelector(loc.substring(4));
        if (loc.startsWith("name:")) return By.name(loc.substring(5));
        // Nếu không có tiền tố, mặc định coi là xpath
        return By.xpath(loc);
    }
}