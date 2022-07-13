
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotSame;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.val;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

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
    void listPublicGists() {
        Response response = given()
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .when()
                .get("/gists/public")
                .then()
                .extract().response();

        assertEquals(200, response.statusCode());

//        System.out.println(response.getBody().asString());

        extent.attachReporter(spark);
        extent.createTest("listPublicGists")
                .log(Status.PASS, "It is possible to list public gists");
        extent.flush();

    }

    @Test(priority = 2)
    void getGistListForSpecificUser() {
        Response response = given()
                .header("Accept", "application/vnd.github?+json")
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .when()
                .get("/users/" + myUser +"/gists")
                .then()
                .extract().response();

        assertEquals(200, response.statusCode());

        //System.out.println(response.getBody().asString());

        extent.attachReporter(spark);
        extent.createTest("getGistListForSpecificUser")
                .log(Status.PASS, "It is possible to list all my gists");
        extent.flush();

    }

    @Test(priority = 3)
    public void createNewGist() {

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
        assertEquals("Example of a gist", response.getBody().jsonPath().get("description"));
        assertNotSame("Update my Hello World", response.getBody().jsonPath().get("description"));

        extent.attachReporter(spark);
        extent.createTest("createNewGist")
                .log(Status.PASS, "It is possible to create a new gist");
        extent.flush();

    }

    @Test(priority = 4, dependsOnMethods = {"createNewGist"})
    public void updateGist() {

        Response response=
                given()
                .header("Accept", "application/vnd.github?+json")
                .header("Authorization", token)
                .body(payloadUpdateGist)
                .contentType(ContentType.JSON)
                .when()
                .patch("/gists/" + gistId)
                .then()
                .extract().response();

        gistId = response.getBody().jsonPath().get("id");
        String agistId = response.getBody().jsonPath().get("files[0].README.md[0].content");
       // System.out.println(agistId);


        extent.attachReporter(spark);
        extent.createTest("UpdateGist")
                .log(Status.PASS, "It is possible to update gist created previously");
        extent.flush();

    }

    @Test(priority = 5, dependsOnMethods = {"updateGist"})
    public void deleteGist() {

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
        extent.createTest("DeleteGist")
                .log(Status.PASS, "It is possible to delete gist created previously");
        extent.flush();
    }

    @Test(priority = 6, dependsOnMethods = {"deleteGist"})
    public void checkGistIsDeleted() {

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
        extent.createTest("checkGistIsDeleted")
                .log(Status.PASS, "The created gist is no longer available");
        extent.flush();
    }

    @Test(priority = 7)
    public void starAPublicGist() {

        Response response = given()
                .header("Accept", "application/vnd.github?+json")
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .when()
                .put("/gists/1/star")
                .then()
                .extract().response();

        assertEquals(204, response.statusCode());

        extent.attachReporter(spark);
        extent.createTest("StarAPublicGist")
                .log(Status.PASS, "The first gist was starred");
        extent.flush();
    }

    @Test(priority = 8)
    public void unstarAPublicGist() {

        given()
                .header("Accept", "application/vnd.github?+json")
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .when()
                .delete("/gists/1/star")
                .then()
                .assertThat().statusCode(204);

        extent.attachReporter(spark);
        extent.createTest("UnstarAPublicGist")
                .log(Status.PASS, "The first gist was removed from starred");
        extent.flush();
    }

    @Test(priority = 9)
    public void listStarredGists() {

        Response response = given()
                .header("Accept", "application/vnd.github?+json")
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .when()
                .get("/gists/starred")
                .then()
                .extract().response();

        assertEquals("[]", response.getBody().asString());
       // System.out.println(response.getBody().asString());

        extent.attachReporter(spark);
        extent.createTest("ListStarredGists")
                .log(Status.PASS, "The created gist is no longer available");
        extent.flush();
    }

}