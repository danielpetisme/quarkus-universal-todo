package me.escoffier;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import me.escoffier.model.Todo;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(DatabaseResource.class)
class TodoResourceTest {

    @Test
    public void verifyThatUnauthenticatedUsersCannotAccessTheTodoList() {
        get("/api")
                .then()
                .statusCode(401);
    }

    @Test
    public void verifyThatAliceCanAccessHerTodoList() {
        given()
                .auth().basic("alice", "alice")
                .get("/api")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(4));
    }

    @Test
    public void testThatDanielCanChangeHisTodoList() {
        Todo todo = new Todo();
        todo.owner = "daniel";
        todo.title = "test";
        todo.completed = false;

        given()
                .auth().basic("daniel", "daniel")
                .get("/api")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(4));

        String id = given()
                .auth().basic("daniel", "daniel")
                .header("content-type", "application/json")
                .body(todo)
                .post("/api")
                .then()
                .statusCode(201)
                .extract().body().jsonPath().getString("id");

        given()
                .auth().basic("daniel", "daniel")
                .get("/api")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(5));

        given()
                .auth().basic("daniel", "daniel")
                .pathParam("id", id)
                .get("/api/{id}")
                .then()
                .statusCode(200)
                .body("title", is("test"))
                .body("owner", is("daniel"))
                .body("completed", is(false));

        todo.title = "fixed";
        todo.completed = true;
        given()
                .auth().basic("daniel", "daniel")
                .pathParam("id", id)
                .header("content-type", "application/json")
                .body(todo)
                .patch("/api/{id}")
                .then()
                .statusCode(200);

        given()
                .auth().basic("daniel", "daniel")
                .pathParam("id", id)
                .get("/api/{id}")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(5))
                .body("title", is("fixed"))
                .body("owner", is("daniel"))
                .body("completed", is(true));

        // Ensure that Alice does not see the new task.
        given()
                .auth().basic("alice", "alice")
                .get("/api")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(4));

        // Ensure that Alice cannot access Daniel new task
        given()
                .auth().basic("alice", "alice")
                .pathParam("id", id)
                .get("/api/{id}")
                .then()
                .statusCode(404);

        given()
                .auth().basic("daniel", "daniel")
                .pathParam("id", id)
                .delete("/api/{id}")
                .then()
                .statusCode(204);

        given()
                .auth().basic("daniel", "daniel")
                .get("/api")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(4));
    }


    @Test
    public void verifyThatNeoCannotAccessTheAPI() {
        // It returns a Forbidden - 403 response.
        given()
                .auth().basic("neo", "neo")
                .get("/api")
                .then()
                .statusCode(403);
    }
}