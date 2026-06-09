package tw.course.cloudnative.githubactionsdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class CurrencyService {

    @Value("${currency.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public double convertToTwd(String fromCurrency, double amount) {
        try {
            // 呼叫免費的匯率 API 
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
            if (response != null && response.containsKey("rates")) {
                Map<String, Object> rates = (Map<String, Object>) response.get("rates");
                
                // 取得該外幣相對台幣的匯率 (例如：USD 在 TWD 基準下大約是 0.031)
                Object rateObj = rates.get(fromCurrency.toUpperCase());
                if (rateObj != null) {
                    double rate = Double.parseDouble(rateObj.toString());
                    // 換算回台幣公式：外幣金額 / 該外幣在 TWD 基準下的匯率值
                    return amount / rate;
                }
            }
        } catch (Exception e) {
            // 【降級機制】如果學校網路斷線或 API 被限流，提供一個固定的教學基準匯率 (1 USD = 32.0 TWD)
            if ("USD".equalsIgnoreCase(fromCurrency)) {
                return amount * 32.0;
            }
        }
        return 0.0;
    }
}