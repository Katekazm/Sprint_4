package ru.yandex.praktikum.solution.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.praktikum.solution.pages.OrderPage;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class OrderTest extends BaseTest {
    private final String name;
    private final String surname;
    private final String address;
    private final String metro;
    private final String phone;
    private final String date;
    private final String rentDuration;
    private final boolean blackScooter;
    private final boolean greyScooter;
    private final String comment;
    private final boolean useTopOrderButton;

    public OrderTest(String name, String surname, String address, String metro, String phone, String date,
                     String rentDuration, boolean blackScooter, boolean greyScooter, String comment, boolean useTopOrderButton) {
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.metro = metro;
        this.phone = phone;
        this.date = date;
        this.rentDuration = rentDuration;
        this.blackScooter = blackScooter;
        this.greyScooter = greyScooter;
        this.comment = comment;
        this.useTopOrderButton = useTopOrderButton;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {"Евгений", "Зубов", "ул. Ленина, 21", "Комсомольская", "+79001234567",
                        "25.03.2025", "двое суток", true, false, "Позвоните за 30 минут", true},
                {"Наталья", "Лукина", "пр. Мира, 15", "Арбатская", "+79161234567",
                        "27.03.2025", "сутки", false, true, "Оставьте у охраны", false}
        });
    }

    @Test
    public void testScooterOrderFlow() {
        if (useTopOrderButton) {
            System.out.println("Используем верхнюю кнопку 'Заказать'");
            mainPage.clickOrderButtonTop();
        } else {
            System.out.println("Используем нижнюю кнопку 'Заказать'");
            mainPage.clickOrderButtonBottom();
        }

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/order"));

        orderPage = new OrderPage(driver);
        orderPage.fillFirstPage(name, surname, address, metro, phone);
        orderPage.fillSecondPage(date, rentDuration, blackScooter, greyScooter, comment);

        String orderNumber = orderPage.getOrderNumber();
        System.out.println("Заказ успешно оформлен. Номер заказа: " + orderNumber);
        assertNotNull("Номер заказа не был отображен!", orderNumber);
        assertFalse("Номер заказа пустой!", orderNumber.trim().isEmpty());

        assertTrue("Кнопка 'Посмотреть статус' не отображается!", orderPage.isTrackOrderButtonVisible());

        orderPage.clickTrackOrderButton();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/track"));

        System.out.println("Переход на страницу отслеживания выполнен.");
    }
}
