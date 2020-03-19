import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class SeleniumExample {
    private WebDriver driver;
    private String link;
    private String email;
    private String pass;
    private String courseName;
    private String courseLink;

    @BeforeTest
    public void getProperties(){
        Properties prop = new Properties();
        try {
            FileInputStream file = new FileInputStream("src/test/resources/testdata.properties");
            prop.load(file);
            link = prop.getProperty("test.link");
            email = prop.getProperty("test.mail");
            pass = prop.getProperty("test.pass");
            courseName = prop.getProperty("test.coursetitle");
            courseLink = prop.getProperty("test.courselink");
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeTest
    public void setUp(){
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
    }

    @AfterTest
    public void tearDown(){
        if (driver!=null){
            driver.quit();
            driver = null;
        }
    }

    @Test
    public void seleniumExampleTest() {
        driver.get(link);
        WebElement findCourse = driver.findElement(By.id("home-search"));
        findCourse.sendKeys(courseName);
        WebElement submitSearch = driver.findElement(By.id("edit-submit-home-search"));
        submitSearch.click();
        WebElement openCourse = driver.findElement(By.xpath("//a[contains(@href,'"+courseLink+"')]"));
        openCourse.click();
        WebElement enrollBtn = driver.findElement(By.cssSelector(".enroll-btn"));
        enrollBtn.click();
        WebElement signIn = driver.findElement(By.className("form-toggle"));
        signIn.click();
        WebElement emailField = driver.findElement(By.id("login-email"));
        emailField.sendKeys(email);
        WebElement passField = driver.findElement(By.id("login-password"));
        passField.sendKeys(pass);
        WebElement submitLogin = driver.findElement(By.xpath("//button[@type='submit']"));
        submitLogin.click();
        WebDriverWait enrollWait = new WebDriverWait(driver, 10);
        enrollWait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("finish-auth")));
        String enrollMessage = driver.findElement(By.xpath("//h3[@class='title']")).getText();
        WebElement dashboard = driver.findElement(By.xpath("//h1/a[@href='/dashboard']"));
        dashboard.click();
        List<WebElement> myCourses = driver.findElements(By.className("course-item"));
        String courseTitle = myCourses.get(0).findElement(By.xpath("//h3[@class='course-title']/a")).getText();
        Assert.assertEquals(myCourses.size(), 1);
        Assert.assertEquals(enrollMessage, "Congratulations! You are now enrolled in "+courseName);
        Assert.assertEquals(courseTitle, courseName);
    }
}