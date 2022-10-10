import POJO.ToDo;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Tasks {

    /** Task 1
     * create a request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * Converting Into POJO
     */

    @Test
    public void task1(){

     ToDo todo =
        given()

                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")

                .then()
                .statusCode(200)
           //     .log().body()
                .extract().as(ToDo.class);

        System.out.println("todo = " + todo);
        System.out.println("todo.getUserId() = " + todo.getUserId());
    }

    /**
     * Task 2
     * create a request to https://httpstat.us/203
     * expect status 203
     * expect content type TEXT
     */

    @Test
    public void task2(){

        given()

                .when()
                .get("https://httpstat.us/203")

                .then()
          //      .log().body()
                .statusCode(203)
                .contentType(ContentType.TEXT);
    }

    /**
     * Task 3
     * create a request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * expect content type JSON
     * expect title in response body to be "quis ut nam facilis et officia qui"
     */

    @Test
    public void task3(){


        given()

                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("title", equalTo("quis ut nam facilis et officia qui"));
    }

    /** Task 4
     * create a request to https://jsonplaceholder.typicode.com/todos
     * expect status 200
     * expect content type JSON
     * expect third item have:
     *      title = "fugiat veniam minus"
     *      userId = 1
     */

    @Test
    public void task4(){

        Response response =
        given()

                .when()
                .get("https://jsonplaceholder.typicode.com/todos")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().response();

        List<Integer> userIds = response.path("userId");
        List<String> titles = response.path("title");
        int ids = response.path("id");

        Assert.assertEquals(userIds.contains(1),titles.contains("fugiat veniam minus"));



    }





}
