package ru.yandex.praktikum.solution.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;



public class OrderPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Поля формы заказа
    private By nameField = By.xpath("//input[@placeholder='* Имя']");
    private By surnameField = By.xpath("//input[@placeholder='* Фамилия']");
    private By addressField = By.xpath("//input[@placeholder='* Адрес: куда привезти заказ']");
    private By metroStationField = By.xpath("//input[@placeholder='* Станция метро']");
    private By phoneField = By.xpath("//input[@placeholder='* Телефон: на него позвонит курьер']");
    private By deliveryDateField = By.xpath("//input[@placeholder='* Когда привезти самокат']");
    private By rentDurationDropdown = By.className("Dropdown-root");
    private By commentField = By.xpath("//input[@placeholder='Комментарий для курьера']");
    private By blackScooterCheckbox = By.id("black");
    private By greyScooterCheckbox = By.id("grey");
    private By nextButton = By.xpath("//button[contains(text(), 'Далее')]");
    private By orderConfirmButton = By.xpath("//button[text()='Да']");
    private By orderSuccessPopup = By.className("Order_Modal__YZ-d3");

    // Выбор станции метро
    public void selectMetroStation(String metro) {
        WebElement metroField = driver.findElement(metroStationField);
        metroField.click(); // Открываем список

        try {
            // Ждем появления списка станций
            List<WebElement> stationOptions = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.xpath("//button[contains(@class, 'select-search__option')]")
            ));

            for (WebElement option : stationOptions) {
                if (option.getText().contains(metro)) {
                    option.click();
                    System.out.println("Выбрана станция метро: " + metro);
                    return;
                }
            }

            throw new NoSuchElementException("Станция метро не найдена: " + metro);
        } catch (TimeoutException e) {
            System.out.println("Не удалось выбрать станцию метро: " + metro);
        }
    }

    // Заполнение первой страницы
    public void fillFirstPage(String name, String surname, String address, String metro, String phone) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Order_Form__17u6u")));

        driver.findElement(nameField).sendKeys(name);
        driver.findElement(surnameField).sendKeys(surname);
        driver.findElement(addressField).sendKeys(address);
        driver.findElement(phoneField).sendKeys(phone);

        selectMetroStation(metro);

        wait.until(ExpectedConditions.presenceOfElementLocated(nextButton));
        clickNextButton();
    }

    // Выбор цвета самоката
    public void selectScooterColor(boolean blackScooter, boolean greyScooter) {
        if (blackScooter) {
            driver.findElement(blackScooterCheckbox).click();
        }
        if (greyScooter) {
            driver.findElement(greyScooterCheckbox).click();
        }
    }

    // Выбор даты доставки
    public void selectDeliveryDate(String date) {
        WebElement dateField = driver.findElement(deliveryDateField);
        dateField.click();
        dateField.sendKeys(date);
        dateField.sendKeys(Keys.ENTER);

        // Ждем, пока календарь закроется
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("react-datepicker")));
    }

    // Выбор длительности аренды
    public void selectRentDuration(String duration) {
        WebElement rentDropdown = driver.findElement(rentDurationDropdown);
        rentDropdown.click();

        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'Dropdown-option') and text()='" + duration + "']")
        ));

        option.click();
    }

    // Ввод комментария
    public void enterCourierComment(String comment) {
        driver.findElement(commentField).sendKeys(comment);
    }

    // Заполнение второй страницы
    public void fillSecondPage(String date, String rentTime, boolean blackScooter, boolean greyScooter, String comment) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Order_Header__BZXOb")));

        selectDeliveryDate(date);
        selectRentDuration(rentTime);
        selectScooterColor(blackScooter, greyScooter);
        enterCourierComment(comment);

        clickOrderButton();
    }

    // Нажатие на кнопку "Далее"
    public void clickNextButton() {
        WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(nextButton));
        nextBtn.click();
    }
    // Метод проверки, проверяем появилось ли модальное окно
    private boolean isModalVisible(WebDriverWait wait) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Order_Modal__YZ-d3")));
            return true; // Если окно появилось, возвращаем true
        } catch (TimeoutException e) {
            return false; // Если не появилось - false
        }
    }

    // Нажатие на кнопку "Заказать"
    public void clickOrderButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            WebElement orderBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class, 'Order_Buttons__1xGrp')]/button[text()='Заказать']"))
            );

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", orderBtn);
            Thread.sleep(1000); // Даем UI время

            System.out.println("Кнопка 'Заказать' найдена.");
            System.out.println("➡ Enabled: " + orderBtn.isEnabled());
            System.out.println("➡ Displayed: " + orderBtn.isDisplayed());

            // Пробуем обычный клик
            try {
                orderBtn.click();
                System.out.println("Обычный клик по 'Заказать' выполнен.");
            } catch (Exception e) {
                System.out.println("Обычный клик не сработал. Пробуем через JS.");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", orderBtn);
            }

            // Ждём появления модального окна подтверждения
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Order_Modal__YZ-d3")));
            System.out.println("Модальное окно подтверждения появилось.");

            // Ждём кнопку "Да" и кликаем
            WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[text()='Да']")
            ));
            confirmButton.click();
            System.out.println("Клик по кнопке 'Да' выполнен.");

            // Ждём появления номера заказа в заголовке
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.className("Order_ModalHeader__3FDaJ"),
                    "Номер заказа:"
            ));
            System.out.println("Номер заказа отображён — заказ успешно оформлен.");

        } catch (TimeoutException e) {
            System.out.println("ОШИБКА: Модальное окно или кнопка 'Да' не сработали вовремя.");
            throw new IllegalStateException("Заказ не был подтверждён — тест прерван.");
        } catch (Exception e) {
            System.out.println("Неожиданная ошибка: " + e.getMessage());
            throw new IllegalStateException("Исключение при оформлении заказа: " + e.getMessage());
        }
    }

    public String getOrderNumber() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement orderConfirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(text(),'Номер заказа:')]")
        ));

        String text = orderConfirmation.getText();
        System.out.println("Модальное окно: " + text);

        String[] parts = text.split("Номер заказа:");
        if (parts.length > 1) {
            String numberPart = parts[1].split("\\.")[0].trim();
            return numberPart;
        } else {
            return null;
        }
    }

    public boolean isTrackOrderButtonVisible() {
        try {
            WebElement trackButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button[text()='Посмотреть статус']")));
            return trackButton.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void clickTrackOrderButton() {
        WebElement trackButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Посмотреть статус')]")
        ));
        trackButton.click();
    }

    // Подтверждение заказа
    public void confirmOrder() {
        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(orderConfirmButton));
        confirmBtn.click();
    }

    // Проверка успешного оформления заказа
    public boolean isOrderConfirmed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(orderSuccessPopup)).isDisplayed();
    }
}
