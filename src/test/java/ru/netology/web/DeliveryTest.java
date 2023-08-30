package ru.netology.web;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class DeliveryTest {

    @BeforeAll
    static void setupAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    public void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        $(By.cssSelector("[data-test-id='city'] input")).setValue(validUser.getCity());
        $(By.cssSelector("[data-test-id='date'] input")).sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE); // очистка поля ввода
        $(By.cssSelector("[data-test-id='date'] input")).sendKeys(firstMeetingDate);
        $(By.cssSelector("[data-test-id='name'] input")).setValue(validUser.getName());
        $(By.cssSelector("[data-test-id='phone'] input")).setValue(validUser.getPhone());
        $(By.cssSelector("[data-test-id='agreement']")).click();
        $(byText("Запланировать")).click();
        $(byText("Успешно!")).shouldBe(Condition.visible, Duration.ofSeconds(15));
        $("[data-test-id='success-notification'] .notification__content").
                shouldBe(Condition.visible).
                shouldHave(Condition.exactText("Встреча успешно запланирована на " + firstMeetingDate));

        $(By.cssSelector("[data-test-id='date'] input")).sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE); // очистка поля ввода
        $(By.cssSelector("[data-test-id='date'] input")).sendKeys(secondMeetingDate);
        $(byText("Запланировать")).click();
        $("[data-test-id='replan-notification'] .notification__content").
                shouldBe(Condition.visible).
                shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $(By.cssSelector("[data-test-id='replan-notification'] button")).click();
        $("[data-test-id='success-notification'] .notification__content").
                shouldBe(Condition.visible).
                shouldHave(Condition.exactText("Встреча успешно запланирована на " + secondMeetingDate));
    }

}
