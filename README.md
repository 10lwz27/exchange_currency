# GitHub Actions Demo

這是一個給課堂示範用的最小 Spring Boot 3.5.14 專案。目標是讓學生先看懂 CI 的基本流程，再慢慢加上更多檢查。

## 專案內容

- `GET /hello`：回傳一筆簡單 JSON。
- `HelloControllerTest`：用 MockMvc 驗證 API。
- `.github/workflows/ci.yml`：push、pull request、手動觸發時執行 Maven 測試與打包。

## 本機執行

```bash
mvn spring-boot:run
```

開啟：

```text
http://localhost:8080/hello
```

## 本機測試

```bash
mvn test
```

## Dependency Check

CI 會在測試與打包成功後執行 OWASP Dependency-Check。

```bash
mvn org.owasp:dependency-check-maven:12.2.2:check -Dformats=HTML,JSON -DfailBuildOnCVSS=9
```

Dependency-Check 第一次執行時需要下載 NVD 弱點資料，可能會花比較久。建議在 GitHub repository 加上 `NVD_API_KEY` secret：

1. 到 NVD 申請 API key。
2. 到 GitHub repository 的 `Settings` > `Secrets and variables` > `Actions`。
3. 新增 repository secret：`NVD_API_KEY`。

如果沒有設定 `NVD_API_KEY`，workflow 仍然可能執行，但比較容易遇到 NVD rate limit 或下載逾時。

## slscan

CI 也會在測試與打包成功後執行 ShiftLeft Security Scan。

這個 job 會執行：

- `credscan`：掃描可能外洩的 credentials。
- `java`：掃描 Java 程式碼。
- `depscan`：掃描依賴套件。

掃描報告會上傳成 GitHub Actions artifact：`slscan-reports`。

## 課堂建議流程

1. 先執行 `mvn test`，讓學生知道 CI 實際上是在跑同一批檢查。
2. 推到 GitHub，觀察 Actions 自動執行。
3. 故意把測試期望值改錯，示範紅燈。
4. 修正測試或程式，再推一次，示範綠燈。
5. 開一個 pull request，說明 CI 如何保護 main branch。
