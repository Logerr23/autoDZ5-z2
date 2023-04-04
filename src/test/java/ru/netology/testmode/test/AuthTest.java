package ru.netology.testmode.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;
import static ru.netology.testmode.data.DataGenerator.Registration.getRegisteredUser;
import static ru.netology.testmode.data.DataGenerator.Registration.getUser;
import static ru.netology.testmode.data.DataGenerator.getRandomLogin;
import static ru.netology.testmode.data.DataGenerator.getRandomPassword;

class AuthTest {
    @BeforeAll
    static void setUpAll(){
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll(){
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
        Configuration.holdBrowserOpen = true;

    }

    @Test
    @DisplayName("Should successfully login with active registered user")
    void shouldSuccessfulLoginIfRegisteredActiveUser() {
        var registeredUser = getRegisteredUser("active");
        $x("//*[@data-test-id = 'login']//input").sendKeys(registeredUser.getLogin());
        $x("//*[@data-test-id = 'password']//input").sendKeys(registeredUser.getPassword());
        $x("//*[@data-test-id = 'action-login']").click();
        $x("//*[contains(text(), 'Личный кабинет')]").shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Should get error message if login with not registered user")
    void shouldGetErrorIfNotRegisteredUser() {
        var notRegisteredUser = getUser("active");
        $x("//*[@data-test-id = 'login']//input").sendKeys(notRegisteredUser.getLogin());
        $x("//*[@data-test-id = 'password']//input").sendKeys(notRegisteredUser.getPassword());
        $x("//*[@data-test-id = 'action-login']").click();
        $x("//*[@data-test-id = 'error-notification']")
                .shouldHave(Condition.text("Неверно указан логин или пароль"))
                .shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Should get error message if login with blocked registered user")
    void shouldGetErrorIfBlockedUser() {
        var blockedUser = getRegisteredUser("blocked");
        $x("//*[@data-test-id = 'login']//input").sendKeys(blockedUser.getLogin());
        $x("//*[@data-test-id = 'password']//input").sendKeys(blockedUser.getPassword());
        $x("//*[@data-test-id = 'action-login']").click();
        $x("//*[@data-test-id = 'error-notification']")
                .shouldHave(Condition.text("Пользователь заблокирован"))
                .shouldBe(Condition.visible);

    }

    @Test
    @DisplayName("Should get error message if login with wrong login")
    void shouldGetErrorIfWrongLogin() {
        var registeredUser = getRegisteredUser("active");
        var wrongLogin = getRandomLogin();
        $x("//*[@data-test-id = 'login']//input").sendKeys(wrongLogin);
        $x("//*[@data-test-id = 'password']//input").sendKeys(registeredUser.getPassword());
        $x("//*[@data-test-id = 'action-login']").click();
        $x("//*[@data-test-id = 'error-notification']")
                .shouldHave(Condition.text("Неверно указан логин или пароль"))
                .shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Should get error message if login with wrong password")
    void shouldGetErrorIfWrongPassword() {
        var registeredUser = getRegisteredUser("active");
        var wrongPassword = getRandomPassword();
        $x("//*[@data-test-id = 'login']//input").sendKeys(registeredUser.getLogin());
        $x("//*[@data-test-id = 'password']//input").sendKeys(wrongPassword);
        $x("//*[@data-test-id = 'action-login']").click();
        $x("//*[@data-test-id = 'error-notification']")
                .shouldHave(Condition.text("Неверно указан логин или пароль"))
                .shouldBe(Condition.visible);
    }
}