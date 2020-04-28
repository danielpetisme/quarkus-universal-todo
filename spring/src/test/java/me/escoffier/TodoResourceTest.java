package me.escoffier;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import me.escoffier.model.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.get;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Testcontainers
@SpringBootTest(classes = {TodoApplication.class, TestSecurityConfiguration.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles({"test"})
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration")
class TodoResourceTest {

    public static final PostgreSQLContainer DATABASE = new PostgreSQLContainer<>("postgres:11.2")
            .withDatabaseName("quarkus_test")
            .withUsername("quarkus_test")
            .withPassword("quarkus_test")
            .withExposedPorts(5432);


    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.standaloneSetup(
                MockMvcBuilders.webAppContextSetup(context)
                        .apply(springSecurity())
        );

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    @Test
    @WithAnonymousUser
    public void verifyThatUnauthenticatedUsersCannotAccessTheTodoList() {
        get("/api")
                .then()
                .statusCode(anyOf(is(401), is(403)));
    }

    @Test
    @WithMockUser(username = "alice")
    public void verifyThatAliceCanAccessHerTodoList() {
        given()
                .get("/api")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(4));

        given()
                .get("/api/{id}", 0) // Todo.id == 0 is a Daniel todo
                .then()
                .statusCode(404);
    }

    @Test
    @WithMockUser(username = "daniel")
    public void testThatDanielCanChangeHisTodoList() throws Exception {
        Todo todo = new Todo();
        todo.setOwner("daniel");
        todo.setTitle("test");
        todo.setCompleted(false);

        given()
                .get("/api")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(4));

        String id = given()
                .header("content-type", "application/json")
                .body(todo)
                .post("/api")
                .then()
                .statusCode(201)
                .extract().body().jsonPath().getString("id");

        given()
                .get("/api")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(5));

        given()
                .get("/api/{id}", id)
                .then()
                .statusCode(200)
                .body("title", is("test"))
                .body("owner", is("daniel"))
                .body("completed", is(false));

        todo.setTitle("fixed");
        todo.setCompleted(true);
        given()
                .header("content-type", "application/json")
                .body(todo)
                .patch("/api/{id}", id)
                .then()
                .statusCode(200);

        given()
                .get("/api/{id}", id)
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(5))
                .body("title", is("fixed"))
                .body("owner", is("daniel"))
                .body("completed", is(true));

        given()
                .delete("/api/{id}", id)
                .then()
                .statusCode(204);

        given()
                .get("/api")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(4));
    }


    @Test
    @WithMockUser(username = "neo", authorities = {"ROLE_RED_PILL"})
    public void verifyThatNeoCannotAccessTheAPI() {
        // It returns a Forbidden - 403 response.
        given()
                .get("/api")
                .then()
                .statusCode(403);
    }
}