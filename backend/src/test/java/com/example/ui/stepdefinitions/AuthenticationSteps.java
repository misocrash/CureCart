package com.example.ui.stepdefinitions;



import com.example.ui.context.TestContext;
import com.example.ui.pages.SigninPage;
import com.example.ui.pages.SignupPage;
import io.cucumber.java.en.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class AuthenticationSteps {
    private TestContext testContext;
    private SigninPage signinPage;
    private SignupPage signupPage;

    // PicoContainer automatically injects TestContext here
    public AuthenticationSteps(TestContext testContext) {
        this.testContext = testContext;
        this.signinPage = new SigninPage(testContext.getDriver());
        this.signupPage = new SignupPage(testContext.getDriver());
    }

    @Given("I am on the Signin page")
    public void i_am_on_the_signin_page() {
        signinPage.navigateTo();
    }

    @When("I login with {string} and {string}")
    public void i_login_with_credentials(String email, String password) {
        signinPage.performLogin(email, password);
    }

    @Then("I should see an error message {string}")
    public void i_should_see_error_message(String expectedError) {
        Assert.assertEquals(signinPage.getErrorMessage(), expectedError);
    }

    @Given("I am on the Signup page")
    public void i_am_on_the_signup_page() {
        signupPage.navigateTo();
    }

    @When("I register with a valid {string}, unique email, and {string}")
    public void i_register_with_valid_data(String name, String password) {
        // Generate a unique email using current timestamp to avoid "User already exists" errors
        String uniqueEmail = "testuser_" + System.currentTimeMillis() + "@example.com";

        // (Optional) Save this email to TestContext if needed in later steps
        // testContext.setRegisteredEmail(uniqueEmail);

        signupPage.performSignup(name, uniqueEmail, password);
    }

    @Then("I should be redirected to the Dashboard or Signin page")
    public void i_should_be_redirected() {
        WebDriver driver = testContext.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Handle the Success Alert
        try {
            // Wait for the alert to appear
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();

            // (Optional) Validate the alert text to ensure it's a success message, not an error
            String alertText = alert.getText();
            Assert.assertTrue(alertText.contains("Signup successful"), "Unexpected alert text: " + alertText);

            // Click "OK" on the alert
            alert.accept();
        } catch (TimeoutException e) {
            // If no alert appears within 10 seconds, log it (it might be a bug if it's expected)
            System.out.println("No success alert appeared. Proceeding to check redirection...");
        }

        // 2. Proceed with Redirection Check
        // Wait until URL contains either 'dashboard' OR 'signin'
        try {
            wait.until(d -> d.getCurrentUrl().contains("/dashboard") || d.getCurrentUrl().contains("/signin"));
        } catch (TimeoutException e) {
            Assert.fail("Redirection failed. Stuck on URL: " + driver.getCurrentUrl());
        }

        String currentUrl = driver.getCurrentUrl();
        boolean isRedirected = currentUrl.contains("/dashboard") || currentUrl.contains("/signin");
        Assert.assertTrue(isRedirected, "User was not redirected after signup. Current URL: " + currentUrl);
    }
}