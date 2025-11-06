package com.example.ui.context;

import org.openqa.selenium.WebDriver;

public class TestContext {
    private WebDriver driver;

    public WebDriver getDriver() {
        if (driver == null) {
            // Calls the static method from your new WebDriverManager
            driver = WebDriverManager.getDriver();
        }
        return driver;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}