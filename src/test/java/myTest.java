
import static io.restassured.RestAssured.given;
import static org.testng.AssertJUnit.assertEquals;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Base64;

public class myTest {

    public String getToken(String encodedToken){
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] bytes = decoder.decode(encodedToken);
        String getToken = new String(bytes);
        return getToken;
    }

    String myUser = "lxndr1";
    static String gistId;
    String payloadCreateGist = "{\r\n  \"description\":\"Example of a gist\",\r\n  \"public\":false,\r\n  \"files\":\r\n" +
            "{\r\n      \"README.md\":\r\n      {\r\n          \"content\":\"Hello World\"\r\n      }\r\n\r\n    }\r\n}";
    String payloadUpdateGist = "{\r\n  \"description\":\"This is a gist update\",\r\n  \"public\":false,\r\n  \"files\":\r\n" +
            "{\r\n      \"README.md\":\r\n      {\r\n          \"content\":\"Update my Hello World\"\r\n      }\r\n\r\n    }\r\n}";

    String token = getToken("QmVhcmVyIGdocF8xd0NkMnhvQnBFQ043UzJVdmpydUppYTBoZ1g5M3gyVkozSXc=");

    ExtentReports extent = new ExtentReports();
    ExtentSparkReporter spark = new ExtentSparkReporter("target/Spark.html");

    @BeforeTest
    public static void setup() {
        RestAssured.baseURI = "https://api.github.com";
    }

    @Test(priority = 1)
    void getRequest() {
        Response response = given()
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .when()
                .get("/gists/public")
                .then()
                .extract().response();

        assertEquals(200, response.statusCode());

        extent.attachReporter(spark);
        extent.createTest("MyFirstTest")
                .log(Status.PASS, "It is possible to get a request");
        extent.flush();

    }

    @Test(priority = 2)
    void getListForSpecificUser() {
        Response response = given()
                .header("Accept", "application/vnd.github?+json")
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .when()
                .get("/users/" + myUser +"/gists")
                .then()
                .extract().response();

        assertEquals(200, response.statusCode());

        extent.attachReporter(spark);
        extent.createTest("MyFirstTest")
                .log(Status.PASS, "It is possible to get a request");
        extent.flush();

    }

    @Test(priority = 3)
    public void createGist() {

        Response response = given()
                .header("Accept", "application/vnd.github?+json")
                .header("Authorization", token)
                .and()
                .body(payloadCreateGist)
                .contentType(ContentType.JSON)
                .when()
                .post("/gists")
                .then()
                .extract().response();

        gistId =  response.getBody().jsonPath().get("id");

        assertEquals(201, response.statusCode());

        extent.attachReporter(spark);
        extent.createTest("MyFirstTest")
                .log(Status.PASS, "It is possible to get a request");
        extent.flush();

    }

    @Test(priority = 4, dependsOnMethods = {"createGist"})
    public void UpdateGist() {

        Response response = given()
                .header("Accept", "application/vnd.github?+json")
                .header("Authorization", token)
                .and()
                .body(payloadUpdateGist)
                .contentType(ContentType.JSON)
                .when()
                .patch("/gists/" + gistId)
                .then()
                .extract().response();

        gistId =  response.getBody().jsonPath().get("id");

        assertEquals(200, response.statusCode());

        extent.attachReporter(spark);
        extent.createTest("MyFirstTest")
                .log(Status.PASS, "It is possible to get a request");
        extent.flush();

    }

    @Test(priority = 5, dependsOnMethods = {"UpdateGist"})
    public void DeleteGist() {

        Response response = given()
                .header("Accept", "application/vnd.github?+json")
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .when()
                .delete("/gists/" + gistId)
                .then()
                .extract().response();

        assertEquals(204, response.statusCode());

        extent.attachReporter(spark);
        extent.createTest("MyFirstTest")
                .log(Status.PASS, "It is possible to get a request");
        extent.flush();
    }

    @Test(priority = 6, dependsOnMethods = {"DeleteGist"})
    public void CheckGistIsDeleted() {

        Response response = given()
                .header("Accept", "application/vnd.github?+json")
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .when()
                .delete("gists/" + gistId)
                .then()
                .extract().response();

        assertEquals(404, response.statusCode());

        extent.attachReporter(spark);
        extent.createTest("MyFirstTest")
                .log(Status.PASS, "It is possible to get a request");
        extent.flush();
    }
}