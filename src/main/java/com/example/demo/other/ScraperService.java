package com.example.demo.other;

import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;


public class ScraperService {


    public static void main(String[] args) {
        // Set up ChromeDriver (ensure chromedriver is correctly configured in your PATH)
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\nikos\\OneDrive - unipi.gr\\Desktop\\chromedriver-win64\\chromedriver.exe");

        // Initialize the WebDriver
        WebDriver driver = new ChromeDriver();

        try {
            // Navigate to okairos.gr
            driver.get("https://okairos.gr");

            // Wait until the search bar is present (identified by its id 'q')
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Increased wait time

            try {
                // Detect and handle the cookie consent dialog
                WebElement consentDialog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("fc-consent-root")));

                // Locate and click the accept button in the consent dialog (you may need to modify the selector)
                WebElement acceptButton = consentDialog.findElement(By.cssSelector(".fc-cta-consent"));
                acceptButton.click();

                // Wait for the consent dialog to disappear
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("fc-consent-root")));
                System.out.println("Consent dialog closed.");
            } catch (NoSuchElementException | TimeoutException e) {
                // If the consent dialog is not present, continue with the script
                System.out.println("Consent dialog not found, continuing...");
            }

            // Wait for the search input to be visible
            WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("q")));

            // Enter the search term 'Αθήνα'
            searchInput.sendKeys("Αθήνα");

            // Scroll to the submit button to ensure it's within the viewport
            WebElement submitButton = driver.findElement(By.xpath("//input[@type='submit']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);

            // Wait until the overlay and consent dialog are completely gone and then click the submit button
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("fc-dialog-overlay")));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("fc-consent-root")));
            submitButton.click();

            // Wait for the list of search results to appear and click the first link
            WebElement firstResultLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//ol//li[1]//a[1]"))); // XPath to locate the first <a> inside the first <li> inside an <ol>

            // Click the first result in the list of links
            firstResultLink.click();

            // Click the link to display current conditions
            WebElement currentConditionsLink = wait.until(ExpectedConditions.elementToBeClickable(By.id("current-conditions-handle")));
            currentConditionsLink.click();

            // Wait for the current conditions container to be visible
            WebElement currentConditions = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("current-conditions")));

            // Now locate the temperature element using its CSS selector
            WebElement temperatureElement = currentConditions.findElement(By.cssSelector("strong.temp"));
            String temperature = temperatureElement.getText().replace("º","");

            // Print the extracted temperature
            System.out.println("Current Temperature: " + temperature);

            sendTemperatureToController(temperature);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the browser
            driver.quit();
        }
    }


    private static void sendTemperatureToController(String temperature) {
        try {
            // URL of the controller endpoint
            URL url = new URL("http://localhost:8080/temperature");

            // Open a connection to the URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);


            // Create the JSON payload using org.json
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("dateTime", LocalDateTime.now());
            jsonPayload.put("temperature", temperature);

            // Send the request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check the response
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                System.out.println("Temperature sent successfully.");
            } else {
                System.out.println("Failed to send temperature.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
