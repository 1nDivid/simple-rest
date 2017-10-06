package ru.individ.simplerest;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.individ.simplerest.entities.Transaction;

import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.DOUBLE;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

class RestServiceTest {
    private final String url = "http://127.0.0.1:3000";
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    @BeforeAll
    static void setUp() {
        SimpleServer.main(null);
    }

    @Test
    void endToEndTest() {
        // generate 2 transactions
        Transaction fTransaction = generateTransaction();
        Transaction sTransaction = generateTransaction();

        // store 2 transactions
        ValidatableResponse fCreateResponse = given().body(fTransaction).post(url + "/transactions")
                .then().statusCode(201)
                .and().header("Location", matchesPattern("/transactions/\\d+"));
        ValidatableResponse sCreateResponse = given().body(sTransaction).post(url + "/transactions")
                .then().statusCode(201)
                .and().header("Location", matchesPattern("/transactions/\\d+"));

        // check we have 2 transactions
        when().get(url + "/transactions")
                .then().statusCode(200)
                .and().body("$.size()", is(2))
                .and().body("[0].id", notNullValue())
                .and().body("[0].senderId.toLong()", is(fTransaction.senderId))
                .and().body("[1].id", notNullValue())
                .and().body("[1].senderId.toLong()", is(sTransaction.senderId));

        // check if it is valid location in response + check get by id
        ValidatableResponse fGetResponse = given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(DOUBLE)))
                .when().get(url + fCreateResponse.extract().header("Location"))
                .then().statusCode(200)
                .and().body("senderId.toLong()", is(fTransaction.senderId))
                .and().body("recipientId.toLong()", is(fTransaction.recipientId))
                .and().body("amount", is(fTransaction.amount));

        // update first transaction
        Transaction update = generateTransaction();
        Long updateId = fGetResponse.extract().body().jsonPath().getLong("id");
        given().body(update).put(url + "/transactions/" + updateId)
                .then().statusCode(200);

        // check transaction has been updated
        given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(DOUBLE)))
                .when().get(url + "/transactions/" + updateId)
                .then().statusCode(200)
                .and().body("senderId.toLong()", is(update.senderId))
                .and().body("recipientId.toLong()", is(update.recipientId))
                .and().body("amount", is(update.amount));

        // delete second transaction
        ValidatableResponse sGetResponse = given().config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(DOUBLE)))
                .when().get(url + sCreateResponse.extract().header("Location"))
                .then().statusCode(200)
                .and().body("id", notNullValue());

        Long secondId = sGetResponse.extract().body().jsonPath().getLong("id");
        when().delete(url + "/transactions/" + secondId)
                .then().statusCode(204);

        // validate deletion of second transaction
        when().get(url + "/transactions")
                .then().statusCode(200)
                .and().body("$.size()", is(1))
                .and().body("[0].id", not(secondId));
    }

    @Test
    void faultRequests() {
        when().get(url).then().statusCode(404);
        when().get(url + "/transactions/12345").then().statusCode(404);
        when().get(url + "/transactions/fault").then().statusCode(400);
        when().delete(url + "/transactions/12345").then().statusCode(404);

        String dummy = "{ \"dummy\": \"dummyobject\" }";
        given().body(dummy).post(url + "/transactions").then().statusCode(400);

        Transaction transaction = generateTransaction();
        given().body(transaction).put(url + "/transactions/12345").then().statusCode(404);
    }

    private Transaction generateTransaction() {
        return new Transaction(null, random.nextLong(100) + 1,
                random.nextLong(100) + 1, random.nextDouble(9000) + 1);
    }
}
