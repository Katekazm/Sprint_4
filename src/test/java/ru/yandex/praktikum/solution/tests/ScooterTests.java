package ru.yandex.praktikum.solution.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.praktikum.solution.pages.MainPage;
import ru.yandex.praktikum.solution.pages.OrderPage;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ScooterTests {
    private WebDriver driver;
    private MainPage mainPage;
    private OrderPage orderPage;

    private final String name;
    private final String surname;
    private final String address;
    private final String metro;
    private final String phone;
    private final String date;
    private final String rentTime;
    private final boolean blackScooter;
    private final boolean greyScooter;
    private final String comment;

    public ScooterTests(String name, String surname, String address, String metro, String phone,
                        String date, String rentTime, boolean blackScooter, boolean greyScooter, String comment) {
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.metro = metro;
        this.phone = phone;
        this.date = date;
        this.rentTime = rentTime;
        this.blackScooter = blackScooter;
        this.greyScooter = greyScooter;
        this.comment = comment;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {"Евгений", "Зубов", "ул. Ленина, 21", "Комсомольская", "+79001234567",
                        "25.03.2025", "двое суток", true, false, "Позвоните за 30 минут"},
                {"Наталья", "Лукина", "пр. Мира, 15", "Арбатская", "+79161234567",
                        "27.03.2025", "сутки", false, true, "Оставьте у охраны"}
        });
    }

    @Before
    public void setUp() {
        String browser = System.getProperty("browser", "chrome");
        if ("firefox".equalsIgnoreCase(browser)) {
            driver = new FirefoxDriver();
        } else {
            driver = new ChromeDriver();
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get("https://qa-scooter.praktikum-services.ru/");
        mainPage = new MainPage(driver);
    }

    @Test
    public void testFullFlow() {
        // 1. Проверяем раздел FAQ
        mainPage.scrollToImportantQuestions();
        String[][] faqData = {
                {"Сколько это стоит? И как оплатить?", "Сутки — 400 рублей. Оплата курьеру — наличными или картой."},
                {"Хочу сразу несколько самокатов! Так можно?", "Пока что у нас так: один заказ — один самокат. Если хотите покататься с друзьями, можете просто сделать несколько заказов — один за другим."},
                {"Как рассчитывается время аренды?", "Допустим, вы оформляете заказ на 8 мая. Мы привозим самокат 8 мая в течение дня. Отсчёт времени аренды начинается с момента, когда вы оплатите заказ курьеру. Если мы привезли самокат 8 мая в 20:30, суточная аренда закончится 9 мая в 20:30."},
                {"Можно ли заказать самокат прямо на сегодня?", "Только начиная с завтрашнего дня. Но скоро станем расторопнее."},
                {"Можно ли продлить заказ или вернуть самокат раньше?", "Пока что нет! Но если что-то срочное — всегда можно позвонить в поддержку по красивому номеру 1010."},
                {"Вы привозите зарядку вместе с самокатом?", "Самокат приезжает к вам с полной зарядкой. Этого хватает на восемь суток — даже если будете кататься без передышек и во сне. Зарядка не понадобится."},
                {"Можно ли отменить заказ?", "Да, пока самокат не привезли. Штрафа не будет, объяснительной записки тоже не попросим. Все же свои."},
                {"Я жизу за МКАДом, привезёте?", "Да, обязательно. Всем самокатов! И Москве, и Московской области."}
        };

        for (int i = 0; i < faqData.length; i++) {
            mainPage.clickFaqQuestion(i);
            String actualAnswer = mainPage.getFaqAnswerText(i);
            assertFalse("Ответ не должен быть пустым", actualAnswer.isEmpty());
            assertEquals("Ответ на вопрос не соответствует ожидаемому!", faqData[i][1], actualAnswer);
        }

        // 2. Переход на страницу заказа
        mainPage.clickOrderButtonBottom();
        boolean urlChanged = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/order"));
        assertTrue("URL не изменился после клика по кнопке 'Заказать'", urlChanged);

        // 3. Заполняем первую страницу
        orderPage = new OrderPage(driver);
        orderPage.fillFirstPage(name, surname, address, metro, phone);

        // 4. Заполняем вторую страницу
        orderPage.fillSecondPage(date, rentTime, blackScooter, greyScooter, comment);

        // 5. Проверяем, активна ли кнопка "Заказать"
        boolean isOrderButtonActive = driver.findElement(By.xpath("//button[text()='Заказать']")).isEnabled();
        assertTrue("Кнопка 'Заказать' должна быть активна, но она заблокирована!", isOrderButtonActive);

        // 6. Подтверждаем заказ
        orderPage.clickOrderButton();

        // 7. Проверяем, появилось ли подтверждение заказа
        boolean isOrderConfirmed = orderPage.isOrderConfirmed();
        assertTrue("Заказ не был подтвержден!", isOrderConfirmed);
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
