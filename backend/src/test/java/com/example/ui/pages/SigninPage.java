package com.example.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class SigninPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // 1. Define Locators as private final By
    private final By emailInputLocator = By.id("email");
    private final By passwordInputLocator = By.id("password");
    private final By signInButtonLocator = By.cssSelector("button.submit-button");
    // Assuming a generic error message container based on standard Angular apps
    private final By errorMessageLocator = By.cssSelector(".api-error, .toast-error");

    // 2. Constructor (No PageFactory.initElements needed)
    public SigninPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // 3. Interaction Methods using Explicit Waits dynamically
    public void navigateTo() {
        driver.get("http://localhost:4200/signin");
    }

    public void enterEmail(String email) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInputLocator)).clear();
        driver.findElement(emailInputLocator).sendKeys(email);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInputLocator)).clear();
        driver.findElement(passwordInputLocator).sendKeys(password);
    }

    public void clickSignIn() {
        // Crucial: Wait for button to be clickable (handles disabled states)
        wait.until(ExpectedConditions.elementToBeClickable(signInButtonLocator)).click();
    }

    public void performLogin(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickSignIn();
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessageLocator)).getText();
    }

    public boolean isSignInButtonEnabled() {
        return driver.findElement(signInButtonLocator).isEnabled();
    }
}