package tests;
import static io.restassured.RestAssured.given;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import POJO.Booking;
import POJO.BookingDates;
import POJO.PatchBooking;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import payload.*;

public class SecondTest {
    ExtentReports extent = new ExtentReports();
    ExtentSparkReporter spark = new ExtentSparkReporter("target/Spark2.html");
    ExtentTest extnt = extent.createTest("Test 2");
	
	static Logger Logger= LogManager.getLogger(FirstTest.class);
	RequestPayload user = new RequestPayload();
    
    public String BookingID;
    public String Token;

    @Test(priority = 1)
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

        // Access the token field
        Token = jsonPath.getString("token");
        System.out.println("Token: " + Token);

        //Assertions Added
        Assert.assertEquals(statusCode, 200);
        Assert.assertTrue(responseBody.contains("token"));
        Logger.info("Authentication successfully completed.");
    }

    @Test(priority = 2)
    public void createBookingTest() {
    	Logger.info("Add booking Started..");
   
        String url = paths.baseURL + paths.BOOKING_ENDPOINT;
        Booking booking = new Booking();
        booking.setFirstname(userData.FirstName);
        booking.setLastname(userData.LastName);
        booking.setTotalprice(100);
        booking.setDepositpaid(true);

        BookingDates bookingDates = new BookingDates();
        bookingDates.setCheckin("2023-01-02");
        bookingDates.setCheckout("2023-01-07");

        booking.setBookingdates(bookingDates);
        booking.setAdditionalneeds("Lunch");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
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
        System.out.println("Booking Id: " + BookingID);
        
        System.out.println("Status Code: " + statusCode);
        System.out.println("Response Body: " + responseBody);
        
        //Assertions Added
        Assert.assertEquals(statusCode, 200);
        Logger.info("Add booking successfully completed.");
        extnt.pass(" 1st test pass - Add booking successfully completed.");
    }
    
    @Test(priority = 3)
    public void patchBookingTest() {
    	Logger.info("Partial update booking Started..");
        String url = paths.baseURL + paths.BOOKING_ENDPOINT + "/" + BookingID;
        
        PatchBooking patchBooking = new PatchBooking();
        patchBooking.setFirstname(userData.FirstName);
        patchBooking.setLastname(userData.LastName);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .header("Cookie", "token=" + Token)
                .body(patchBooking)
            .when()
                .patch(url)
            .then()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("Status Code: " + statusCode);
        System.out.println("Response Body: " + responseBody);
        
        //Assertions Added
        Assert.assertEquals(statusCode, 200);
        Logger.info("Partial update booking successfully completed.");
        extnt.pass(" 2nd test pass - Partial update booking successfully completed.");
    }
    
    @Test (priority = 4)
    public void deleteBookingTest() {
    	Logger.info("Delete booking Started..");
        String url = paths.bookingURL+ BookingID;
        
        
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Cookie", "token=" + Token)
            .when()
                .delete(url)
            .then()
                .extract()
                .response();

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("Status Code: " + statusCode);
        System.out.println("Response Body: " + responseBody);
        
        //Assertions Added
        //Assert.assertEquals(statusCode, 201);
       
        Logger.info("Delete booking successfully completed.");
        extnt.pass(" 3rd test pass - Delete booking successfully completed.");
        extnt.log(Status.INFO, "Closing the test");
        extent.flush();
    }
}
