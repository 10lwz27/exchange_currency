package tw.course.cloudnative.githubactionsdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CurrencyController {

    private final CurrencyService currencyService;

    @Value("${app.api.key}")
    private String requiredApiKey;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/api/convert")
    public ResponseEntity<?> convertCurrency(
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey,
            @RequestParam String from,
            @RequestParam double amount) {

        // 1. 驗證自訂的 Secret 機敏安全金鑰
        if (apiKey == null || !apiKey.equals(requiredApiKey)) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid or missing API Key!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        // 2. 進行匯率轉換
        double result = currencyService.convertToTwd(from, amount);
        
        // 四捨五入到小數後兩位
        double roundedResult = Math.round(result * 100.0) / 100.0; 

        // 3. 回傳標準結果
        return ResponseEntity.ok(new ConversionResult(from.toUpperCase(), amount, roundedResult));
    }
}