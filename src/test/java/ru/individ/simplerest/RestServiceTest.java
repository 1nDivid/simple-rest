package ru.individ.simplerest;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.individ.simplerest.dto.AccountDto;
import ru.individ.simplerest.dto.TransferDto;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.DOUBLE;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

class RestServiceTest {
    private final String url = "http://127.0.0.1:3000";

    @BeforeAll
    static void setUp() {
        SimpleServer.main(null);
    }

    @Test
    void endToEndTest() {
        RestAssuredConfig config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(DOUBLE));

        // create 2 accounts
        AccountDto phillipDto = new AccountDto("Phillip", 35000.00);
        JsonPath phillip = given().config(config)
                .body(phillipDto).post(url + "/accounts")
                .then().statusCode(201)
                .assertThat().body("id", notNullValue())
                .assertThat().body("name", is(phillipDto.name))
                .assertThat().body("balance", is(phillipDto.balance))
                .extract().body().jsonPath();
        AccountDto markDto = new AccountDto("Mark", 5000.00);
        JsonPath mark = given().config(config)
                .body(markDto).post(url + "/accounts")
                .then().statusCode(201)
                .assertThat().body("id", notNullValue())
                .assertThat().body("name", is(markDto.name))
                .assertThat().body("balance", is(markDto.balance))
                .extract().body().jsonPath();

        long phillipId = phillip.getLong("id");
        long markId = mark.getLong("id");

        // get all accounts
        when().get(url + "/accounts")
                .then().statusCode(200)
                .assertThat().body("$.size()", is(2))
                .assertThat().body("[0].id.toLong()", is(phillipId))
                .assertThat().body("[1].id.toLong()", is(markId));

        // check no transactions
        when().get(url + "/accounts/" + phillipId + "/transfers")
                .then().statusCode(200)
                .assertThat().body("$.size()", is(0));
        when().get(url + "/accounts/" + markId + "/transfers")
                .then().statusCode(200)
                .assertThat().body("$.size()", is(0));

        // make transfer from Phillip to Mark
        TransferDto transferDto = new TransferDto(markId, 10000.00);
        String transferUrl = String.format("%s/accounts/%d/transfers", url, phillipId);
        JsonPath transaction = given().config(config)
                .body(transferDto).post(transferUrl)
                .then().statusCode(201)
                .assertThat().body("id", notNullValue())
                .assertThat().body("senderId.toLong()", is(phillipId))
                .assertThat().body("recipientId.toLong()", is(markId))
                .assertThat().body("amount", is(transferDto.amount))
                .extract().body().jsonPath();

        // check balances
        String phillipUrl = String.format("%s/accounts/%d", url, phillipId);
        given().config(config).get(phillipUrl)
                .then().statusCode(200)
                .assertThat().body("id.toLong()", is(phillipId))
                .assertThat().body("balance", is(phillip.getDouble("balance") - transferDto.amount));
        String markUrl = String.format("%s/accounts/%d", url, markId);
        given().config(config).get(markUrl)
                .then().statusCode(200)
                .assertThat().body("id.toLong()", is(markId))
                .assertThat().body("balance", is(mark.getDouble("balance") + transferDto.amount));

        // check all have transaction
        when().get(url + "/accounts/" + phillipId + "/transfers")
                .then().statusCode(200)
                .assertThat().body("$.size()", is(1))
                .assertThat().body("[0].id.toLong()", is(transaction.getLong("id")));
        when().get(url + "/accounts/" + markId + "/transfers")
                .then().statusCode(200)
                .assertThat().body("$.size()", is(1))
                .assertThat().body("[0].id.toLong()", is(transaction.getLong("id")));

        // create Oliver
        AccountDto oliverDto = new AccountDto("Oliver", 2500.00);
        JsonPath oliver = given().config(config)
                .body(oliverDto).post(url + "/accounts")
                .then().statusCode(201)
                .extract().body().jsonPath();

        long oliverId = oliver.getLong("id");

        // create transaction from Oliver to Mark
        transferDto = new TransferDto(markId, 500.00);
        transferUrl = String.format("%s/accounts/%d/transfers", url, oliverId);
        transaction = given().config(config)
                .body(transferDto).post(transferUrl)
                .then().statusCode(201)
                .extract().body().jsonPath();

        // Phillip must not see this transaction
        when().get(url + "/accounts/" + phillipId + "/transfers")
                .then().statusCode(200)
                .assertThat().body("$.size()", is(1));
        when().get(url + "/accounts/" + phillipId + "/transfers/" + transaction.getLong("id"))
                .then().statusCode(404);

        // But Mark see
        given().config(config)
                .get(url + "/accounts/" + markId + "/transfers/" + transaction.getLong("id"))
                .then().statusCode(200)
                .assertThat().body("id.toLong()", is(transaction.getLong("id")))
                .assertThat().body("senderId.toLong()", is(oliverId))
                .assertThat().body("recipientId.toLong()", is(markId))
                .assertThat().body("amount", is(transferDto.amount));

        // test that we cannot transfer more than we have
        transferDto = new TransferDto(markId, 5000.00);
        transferUrl = String.format("%s/accounts/%d/transfers", url, oliverId);
        given().config(config)
                .body(transferDto).post(transferUrl)
                .then().statusCode(400);
    }
}
