package GoRest;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUsersTests {

    @BeforeClass
    void Setup() {
        baseURI = "https://gorest.co.in/public/v2/";
    }

    public String getRandomName() {

        return RandomStringUtils.randomAlphabetic(8);
    }

    public String getRandomEmail() {

        return RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@gmail.com";
    }

    int userId = 0;
    User newUser;

    @Test
    public void createUserObject() {
        newUser = new User();
        newUser.setName(getRandomName());
        newUser.setGender("male");
        newUser.setEmail(getRandomEmail());
        newUser.setStatus("active");

        userId =
                given()
                        // api metoduna gitmeden önceki hazırlıklar: token, gidecek body, parametreler
                        .header("Authorization", "Bearer fec151b1b29086e50e95ef0102a1d7c296d0122d82f691a53fba5887e5a70b92")
                        .contentType(ContentType.JSON)
                        .body(newUser)
                        .log().body()

                        .when()
                        .post("users")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        //   .extract().path("id")
                        .extract().jsonPath().getInt("id");
        // path : class veya tip dönüşümüne imkan veremeyen direk veriyi verir. List<String> gibi
        // jsonPath : class dönüşümüne ve tip dönüşümüne izin vererek , veriyi istediğimiz formatta verir.

        System.out.println("userId = " + userId);
    }

    @Test(dependsOnMethods = "createUserObject", priority = 1)
    public void updateUserObject() {

        //      Map<String, String> updateUser = new HashMap<>();
        //      updateUser.put("name","timur demirors");

        newUser.setName("timur demirors");

        given()
                .header("Authorization", "Bearer fec151b1b29086e50e95ef0102a1d7c296d0122d82f691a53fba5887e5a70b92")
                .contentType(ContentType.JSON)
                //     .body(updateUser)
                .body(newUser)
                .log().body()
                .pathParam("userId", userId)

                .when()
                .put("users/{userId}")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo("timur demirors"));
    }

    @Test(dependsOnMethods = "createUserObject", priority = 2)
    public void getUserById() {

        given()
                .header("Authorization", "Bearer fec151b1b29086e50e95ef0102a1d7c296d0122d82f691a53fba5887e5a70b92")
                .contentType(ContentType.JSON)
                .log().body()
                .pathParam("userId", userId)

                .when()
                .get("users/{userId}")

                .then()
                .log().body()
                .statusCode(200)
                .body("id", equalTo(userId));
    }

    @Test(dependsOnMethods = "createUserObject", priority = 3)
    public void deleteUserById() {

        given()
                .header("Authorization", "Bearer fec151b1b29086e50e95ef0102a1d7c296d0122d82f691a53fba5887e5a70b92")
                .contentType(ContentType.JSON)
                .log().body()
                .pathParam("userId", userId)

                .when()
                .delete("users/{userId}")

                .then()
                .log().body()
                .statusCode(204);
    }

    @Test(dependsOnMethods = "deleteUserById")
    public void deleteUserByIdNegative() {

        given()
                .header("Authorization", "Bearer fec151b1b29086e50e95ef0102a1d7c296d0122d82f691a53fba5887e5a70b92")
                .contentType(ContentType.JSON)
                .log().body()
                .pathParam("userId", userId)

                .when()
                .delete("users/{userId}")

                .then()
                .log().body()
                .statusCode(404);
    }

    @Test
    public void getUsersList() {

        Response response =
                given()
                        .header("Authorization", "Bearer fec151b1b29086e50e95ef0102a1d7c296d0122d82f691a53fba5887e5a70b92")

                        .when()
                        .get("users")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().response();

        // TODO : 3. usersın id sini alınız (path ve jsonPath ile ayrı ayrı yapınız)
        int idUser3path = response.path("[2].id");
        int idUser3JsonPath = response.jsonPath().getInt("[2].id");
        System.out.println("idUser3path = " + idUser3path);
        System.out.println("idUser3JsonPath = " + idUser3JsonPath);

        // TODO : GetUserByID testinde dönen user ı bir nesneye atınız.
        User[] usersPath = response.as(User[].class);
        System.out.println("Arrays.toString(usersPath) = " + Arrays.toString(usersPath));

        List<User> usersJsonPath = response.jsonPath().getList("", User.class);   // path olmadığı için ilk boşluk bıraktık
        System.out.println("usersJsonPath = " + usersJsonPath);
    }

    @Test
    public void getUserByIdExtract() {   // TODO : Tüm gelen veriyi bir nesneye atınız (google araştırması)

        User user =
                given()
                        .header("Authorization", "Bearer fec151b1b29086e50e95ef0102a1d7c296d0122d82f691a53fba5887e5a70b92")
                        .contentType(ContentType.JSON)
                        .pathParam("userId", 2303)

                        .when()
                        .get("users/{userId}")

                        .then()
                        .log().body()
                        .statusCode(200)
                        //      .extract().as(User.class)
                        .extract().jsonPath().getObject("", User.class);   // path olmadığı için ilk boşluk bıraktık

        System.out.println("user = " + user);
    }

    @Test
    public void getUsersV1() {

        Response response =
                given()
                        .header("Authorization", "Bearer fec151b1b29086e50e95ef0102a1d7c296d0122d82f691a53fba5887e5a70b92")

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().response();
        //      response.as();  tüm gelen response a uygun nesneler için tüm class ların yapılması gerekiyor

        List<User> dataUsers = response.jsonPath().getList("data", User.class);
        //  jsonPath bir response içindeki bir parçayı nesneye dönüştürebiliriz
        System.out.println("dataUsers = " + dataUsers);

        // Daha önceki örneklerde (as) Clas dönüşümleri için tüm yapıya karşılık gelen
        // gereken tüm classları yazarak dönüştürüp istediğimiz elemanlara ulaşıyorduk.
        // Burada ise(JsonPath) aradaki bir veriyi clasa dönüştürerek bir list olarak almamıza
        // imkan veren JSONPATH i kullandık.Böylece tek class ise veri alınmış oldu
        // diğer class lara gerek kalmadan
        // path : class veya tip dönüşümüne imkan veremeyen direk veriyi verir. List<String> gibi
        // jsonPath : class dönüşümüne ve tip dönüşümüne izin vererek , veriyi istediğimiz formatta verir. 
    }


    @Test(enabled = false)
    public void createUser() {

        userId =
                given()
                        // api metoduna gitmeden önceki hazırlıklar: token, gidecek body, parametreler
                        .header("Authorization", "Bearer fec151b1b29086e50e95ef0102a1d7c296d0122d82f691a53fba5887e5a70b92")
                        .contentType(ContentType.JSON)
                        .body("{\"name\":\"" + getRandomName() + "\", \"gender\":\"male\", \"email\":\"" + getRandomEmail() + "\", \"status\":\"active\"}")

                        .when()
                        .post("users")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
        System.out.println("userId = " + userId);
    }


    @Test(enabled = false)
    public void createUserMap() {

        Map<String, String> newUser = new HashMap<>();
        newUser.put("name", getRandomName());
        newUser.put("gender", "male");
        newUser.put("email", getRandomEmail());
        newUser.put("status", "active");

        userId =
                given()
                        // api metoduna gitmeden önceki hazırlıklar: token, gidecek body, parametreler
                        .header("Authorization", "Bearer fec151b1b29086e50e95ef0102a1d7c296d0122d82f691a53fba5887e5a70b92")
                        .contentType(ContentType.JSON)
                        .body(newUser)
                        .log().body()

                        .when()
                        .post("users")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");

        System.out.println("userId = " + userId);
    }

  static class User {

        private int İd;
        private String name;
        private String gender;
        private String email;
        private String status;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getId() {
            return İd;
        }

        public void setId(int id) {
            İd = id;
        }

        @Override
        public String toString() {
            return "User{" +
                    "İd=" + İd +
                    ", name='" + name + '\'' +
                    ", gender='" + gender + '\'' +
                    ", email='" + email + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }
    }
}