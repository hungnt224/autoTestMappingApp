package framework;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.testng.annotations.Test;
import java.io.FileReader;
import java.util.List;

public class Engine extends BaseTest { // Kế thừa BaseTest để có sẵn driver và app

    @Test
    public void runTestSuite() throws Exception {
        // Đọc file JSON
        Gson gson = new Gson();
        String jsonPath = "src/test/resources/test_suite.json";
        List<Models.TestCase> testSuite = gson.fromJson(
                new FileReader(jsonPath),
                new TypeToken<List<Models.TestCase>>(){}.getType()
        );

        // Chạy từng Test Case
        for (Models.TestCase testCase : testSuite) {
            System.out.println("--- Đang chạy: " + testCase.name + " ---");

            for (Models.Step step : testCase.steps) {
                // Gọi lệnh từ Library (đã được khởi tạo ở BaseTest)
                if (library.commands.containsKey(step.action)) {
                    library.commands.get(step.action).accept(step);
                } else {
                    System.err.println("Lỗi: Không tìm thấy action " + step.action);
                }
            }
        }
    }
}