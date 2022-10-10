import POJO.Location;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ZippoTest {

    @Test
    public void test(){

        given()
                // hazırlık işlemlerini yapacağız (token, send body, parametreler)

                .when()
                //  link i ve metodu veriyoruz (GET, PUT, DELETE, POST ve URL bölümleri)

                .then();
                //  assertion kısmı ve verileri ele alma extract (Postman in TEST bölümü ve alınacak veriler ID, NAME)
    }

    @Test
    public void statusCodeTest(){

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()           // log.all() bütün response değerlerini gösterir (header...)
                .statusCode(200)     // status kontrolü
                .contentType(ContentType.JSON)  // hatalı durum kontrolü
        ;
    }
    @Test
    public void checkStateInResponseBody(){

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()   // body i kontrol etme amaçlı
                .body("country", equalTo("United States"))  // body.country == United States ?
                .statusCode(200)
        ;
    }

 //            body.'post code'   --> body("post code")
 //            body.country       --> body("country")
 //            body.'country abbreviation'  --> body("country abbreviation")
 //            body.places[0].'place name'  --> body("places[0].'place name'")
 //            body.places[0].state         --> body("places[0].state")

    @Test
    public void bodyJsonPathTest1(){

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()   // body i kontrol etme amaçlı
                .body("places[0].state", equalTo("California"))
                .statusCode(200)
        ;
    }

    @Test
    public void bodyJsonPathTest2(){

        given()

             .when()
             .get("http://api.zippopotam.us/tr/01000")

             .then()
             .log().body()
             .body("places.'place name'", hasItem("Çaputçu Köyü"))  // bir index verilmezse dizinin bütün elemanlarında arıyor
             .statusCode(200)                       // "places.'place name'" bu bilgiler "Çaputçu Köyü" bu iteme sahip mi?
        ;
    }

    @Test
    public void bodyArrayHasSizeTest(){

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places", hasSize(1))  // verilen path deki listin size kontrolü yani index sayısı
                .statusCode(200)
        ;
    }

    @Test
    public void bodyCombiningTest(){

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places", hasSize(1))
                .body("places.state", hasItem("California"))
                .body("places[0].'place name'", equalTo("Beverly Hills"))
                .statusCode(200)
        ;
    }

    @Test
    public void pathParamTest1(){

        given()
                .pathParam("Country", "us")
                .pathParam("Post Code", 90210)  // int olunca istersek çift tırnaklada yazabiliriz
                .log().uri()  // request linki

                .when()
                .get("http://api.zippopotam.us/{Country}/{Post Code}")

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test
    public void pathParamTest2(){

        // 90210 dan 90213 kadar test sonuçlarında places in size nın hepsinde 1 gediğini test ediniz.

      for (int i = 90210; i <= 90213; i++)
        given()
                .pathParam("Country", "us")
                .pathParam("ZipCode", i)
                .log().uri()

                .when()
                .get("http://api.zippopotam.us/{Country}/{ZipCode}")

                .then()
                .log().body()
                .body("places",hasSize(1))
                .statusCode(200)
        ;
    }

    @Test
    public void queryParamTest1(){

        given()
                .param("page", 1)
                .log().uri()   // request linki

                .when()
                .get("https://gorest.co.in/public/v1/users")

                .then()
                .log().body()
                .body("meta.pagination.page", equalTo(1))
                .statusCode(200)
        ;
    }

    @Test
    public void queryParamTest2(){

    for (int pageNo = 1; pageNo <=10; pageNo++)

        given()
                .param("page", pageNo)
                .log().uri()

                .when()
                .get("https://gorest.co.in/public/v1/users")

                .then()
                .log().body()
                .body("meta.pagination.page", equalTo(pageNo))
                .statusCode(200)
        ;
    }

    RequestSpecification requestSpecs;
    ResponseSpecification responseSpecs;

    @BeforeTest
    void Setup(){
        baseURI = "https://gorest.co.in/public/v1";

        requestSpecs = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setAccept(ContentType.JSON)
                .build();

        responseSpecs = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.BODY)
                .build();
    }

    @Test
    public void requestResponseSpecification(){

        given()
                .param("page", 1)
                .spec(requestSpecs)

                .when()
                .get("https://gorest.co.in/public/v1/users")

                .then()
                .body("meta.pagination.page", equalTo(1))
                .spec(responseSpecs)
        ;
    }

    @Test
    public void extractingJsonPath(){

        String placeName=
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .statusCode(200)
                .extract().path("places[0].'place name'")
        // extract metodu ile given ile başlayan satır, bir değer döndürür halle geldi, en sonda extract olmalı
        ;
        System.out.println("placeName = " + placeName);
    }

    @Test
    public void extractingJsonPathInt1(){

        int limit=
               given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                     //   .log().body()
                        .statusCode(200)
                       .extract().path("meta.pagination.limit")
               ;
        System.out.println("limit = " + limit);
        Assert.assertEquals(limit,10,"test sonucu");
    }

    @Test
    public void extractingJsonPathInt2() {

        int id =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                          .log().body()
                        .statusCode(200)
                        .extract().path("data[2].id")
                ;
        System.out.println("id = " + id);
    }

    @Test
    public void extractingJsonPathIntList() {

        List<Integer> idler =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //   .log().body()
                        .statusCode(200)
                        .extract().path("data.id")  // data daki bütün id leri bir list şeklinde verir
                ;
        System.out.println("idler = " + idler);
        Assert.assertTrue(idler.contains(3045));
    }

    @Test
    public void extractingJsonPathStringList() {

        List<String> names =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //   .log().body()
                        .statusCode(200)
                        .extract().path("data.name")  // data daki bütün name leri bir list şeklinde verir
                ;
        System.out.println("names = " + names);
        Assert.assertTrue(names.contains("Ganapati Gupta"));
    }

    @Test
    public void extractingJsonPathResponseAll() {

        Response response =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //   .log().body()
                        .statusCode(200)
                        .extract().response()  // bütün body alındı
                ;
        List<Integer> idler = response.path("data.id");
        List<String> isimler = response.path("data.name");
        int limit = response.path("meta.pagination.limit");

        System.out.println("idler = " + idler);
        System.out.println("isimler = " + isimler);
        System.out.println("limit = " + limit);

    }

    @Test
    public void extractingJsonPOJO(){

        Location yer =
                given()

                        .when()
                        .get("http://api.zippopotam.us/us/90210")


                        .then()
                        .extract().as(Location.class);  // Location şablonu
        
        System.out.println("yer = " + yer);

        System.out.println("yer.getCountry() = " + yer.getCountry());
        System.out.println("yer.getPlaces().get(0).getPlaceName() = " + yer.getPlaces().get(0).getPlaceName());
    }


}
