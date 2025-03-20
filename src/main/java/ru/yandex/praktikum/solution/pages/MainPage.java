package ru.yandex.praktikum.solution.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;
import java.time.Duration;
import java.util.List;

public class MainPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Конструктор
    public MainPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }
    private By cookieButton = By.id("rcc-confirm-button");

    // Локатор для раздела "Вопросы о важном"
    private By importantQuestionsSection = By.xpath("//div[contains(text(), 'Вопросы о важном')]");

    // Локаторы для выпадающих вопросов и ответов
    private By faqQuestions = By.xpath("//div[@data-accordion-component='AccordionItemButton']");
    private By faqAnswers = By.xpath("//div[@data-accordion-component='AccordionItemPanel' and contains(@class, 'accordion__panel')]");

    // Локаторы для кнопок "Заказать"
    private By topOrderButton = By.xpath("//button[contains(@class, 'Button_Button__') and text()='Заказать']");
    private By bottomOrderButton = By.xpath("//div[contains(@class, 'Home_FinishButton__')]/button[text()='Заказать']");

    public void acceptCookies() {
        try {
            WebElement cookieBtn = wait.until(ExpectedConditions.elementToBeClickable(cookieButton));
            cookieBtn.click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(cookieButton)); // Ждем, пока баннер исчезнет
        } catch (TimeoutException e) {
            // Если кнопки нет, значит баннера нет — ничего не делаем
        }
    }

    // Метод для прокрутки к "Вопросы о важном"
    public void scrollToImportantQuestions() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(importantQuestionsSection));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    // Клик по вопросу (по индексу)
    public void clickFaqQuestion(int index) {
        acceptCookies(); // Закрываем баннер перед кликом
        List<WebElement> questions = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(faqQuestions));
        if (index >= 0 && index < questions.size()) {
            questions.get(index).click();
        } else {
            throw new IndexOutOfBoundsException("Invalid FAQ index: " + index);
        }
    }


    // Получение текста ответа по индексу
    public String getFaqAnswerText(int index) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Ожидание, пока ответ станет видимым
        By answerLocator = By.xpath("(//div[@data-accordion-component='AccordionItemPanel'])[" + (index + 1) + "]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(answerLocator));

        return driver.findElement(answerLocator).getText();
    }


    // Клик по верхней кнопке "Заказать"
    public void clickOrderButtonTop() {
        acceptCookies(); // Сначала закроем баннер
        System.out.println("Кликаем верхнюю кнопку 'Заказать'");
        WebElement orderButton = driver.findElement(topOrderButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", orderButton);

        // Добавляем небольшую паузу, чтобы кнопка успела "прорисоваться" после скролла
        try {
            Thread.sleep(500); // 0.5 секунды
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        orderButton.click();
    }

    public void clickOrderButtonBottom() {
        acceptCookies(); // Закрываем баннер перед кликом
        System.out.println("Кликаем верхнюю кнопку 'Заказать'");
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(bottomOrderButton));
        button.click();
    }
}
