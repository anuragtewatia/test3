package tests;
import static io.restassured.RestAssured.given;
import payload.*;

import org.testng.Assert;
import org.testng.annotations.*;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import POJO.Booking;
import POJO.BookingDates;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class FirstTest {
    ExtentReports extent = new ExtentReports();
    ExtentSparkReporter spark = new ExtentSparkReporter("target/Spark.html");
    ExtentTest extnt = extent.createTest("Test 1");
    
	static Logger Logger= LogManager.getLogger(FirstTest.class);
	RequestPayload user = new RequestPayload();

	public String BookingID;
	public String Token;
	
	@Test(priority =1)
	
    public void testAuthentication() {
		extent.attachReporter(spark);
        extnt.log(Status.INFO, "Stareted the test");
        
        Logger.info("Authentication Started..");
        String json= user.requestPayLoad();
        Response response = given()
            .baseUri(paths.baseURL)
            .basePath(paths.AUTH_ENDPOINT)
            .contentType(ContentType.JSON)
            .body(json)
            .when()
            .post();

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("Status Code: " + statusCode);
        System.out.println("Response Body: " + responseBody);

        // Parse the response body as JSON
        JsonPath jsonPath = new JsonPath(responseBody);

        // Access the tokenid field
        Token = jsonPath.getString("token");
        System.out.println("Token " + Token);

        //Assertions added
         Assert.assertEquals(statusCode, 200);
         Logger.info("Authentication successfully completed.");
         extnt.pass("Authentication successfully completed");
    }


    @Test (priority =2)
    public void createBookingTest() {
    	Logger.info("Creating new booking started..");
    	String url = paths.bookingURL ;
    	
        Booking booking = new Booking();
        booking.setFirstname(userData.FirstName);
        booking.setLastname(userData.LastName);
        booking.setTotalprice(100);
        booking.setDepositpaid(true);

        BookingDates bookingDates = new BookingDates();
        bookingDates.setCheckin("2023-01-10");
        bookingDates.setCheckout("2023-01-11");

        booking.setBookingdates(bookingDates);
        booking.setAdditionalneeds("Dinner");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json") // Include necessary headers
                .header("crumb", "your-valid-crumb-value") // Replace with the actual valid crumb value
                .body(booking)
            .when()
                .post(url)
            .then()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();
        
        // Parse the response body as JSON
        JsonPath jsonPath = new JsonPath(responseBody);

        // Access the bookingid field
        BookingID = jsonPath.getString("bookingid");
        System.out.println("Booking Id " + BookingID);
        
        System.out.println("Status Code: " + statusCode);
        System.out.println("Response Body: " + responseBody);
        
        //Assertions added
         Assert.assertEquals(statusCode, 200);
         Logger.info("New booking successfully completed.");
         extnt.pass(" 1st test pass - New booking successfully completed.");
    }
    
    @Test (priority = 3)
    public void updateBookingTest() {
    	Logger.info("Update booking started..");
        String url = paths.bookingURL + BookingID;
        String token = "abc123"; // Replace with actual token value
        
        Booking booking = new Booking();
        booking.setFirstname(userData.FirstName);
        booking.setLastname(userData.LastName);
        booking.setTotalprice(100);
        booking.setDepositpaid(true);

        BookingDates bookingDates = new BookingDates();
        bookingDates.setCheckin("2023-01-02");
        bookingDates.setCheckout("2023-01-11");

        booking.setBookingdates(bookingDates);
        booking.setAdditionalneeds("Lunch");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .header("Cookie", "token=" + Token)
                .body(booking)
            .when()
                .put(url)
            .then()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("Status Code: " + statusCode);
        System.out.println("Response Body: " + responseBody);
        
        //Assertions added
         Assert.assertEquals(statusCode, 200);
         Logger.info("Update booking successfully completed.");
         extnt.pass(" 2nd test pass - Update booking successfully completed.");
    }
    
    @Test (priority = 4)
    public void getBookingTest() {
    	Logger.info("Get booking started..");
        String url = paths.bookingURL +BookingID;

        Response response = RestAssured.get(url);

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("Status Code: " + statusCode);
        System.out.println("Response Body: " + responseBody);
        
        //Assertions added
         Assert.assertEquals(statusCode, 200);
         Logger.info("Get booking successfully completed.");
         extnt.pass(" 3rd test pass - Get booking successfully completed.");
         extnt.log(Status.INFO, "Closing the test");
         extent.flush();
    }
    
}
