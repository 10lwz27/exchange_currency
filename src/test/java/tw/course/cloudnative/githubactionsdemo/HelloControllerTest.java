package tw.course.cloudnative.githubactionsdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HelloControllerTest { // 保持檔名不變，方便你直接貼上覆蓋

    @Autowired
    private MockMvc mockMvc;

    @Value("${app.api.key}")
    private String apiKey;

    @Test
    void testConvertWithoutApiKeyShouldReturn403() throws Exception {
        // 情境一：沒帶 X-API-KEY 標頭，應該回傳 403 Forbidden (可用來示範掉燈與修復)
        mockMvc.perform(get("/api/convert")
                        .param("from", "USD")
                        .param("amount", "100"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Invalid or missing API Key!"));
    }

    @Test
    void testConvertWithValidApiKeyShouldReturn200() throws Exception {
        // 情境二：帶上正確的金鑰，應該成功換算並拿到 200 OK
        mockMvc.perform(get("/api/convert")
                        .header("X-API-KEY", apiKey)
                        .param("from", "USD")
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("USD"))
                .andExpect(jsonPath("$.resultTWD").exists());
    }
}