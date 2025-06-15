package com.tecnocampus.examsimulation;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class KingdomTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateKingdom() throws Exception {
        mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":10,\"citizens\":10,\"food\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.dateOfCreation").exists())
                .andExpect(jsonPath("$.gold").value(10))
                .andExpect(jsonPath("$.citizens").value(10))
                .andExpect(jsonPath("$.food").value(10));
    }

    @Test
    public void testCreateKingdomWithInvalidParams() throws Exception {
        mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":-1,\"citizens\":30,\"food\":25}"))
                .andExpect(status().isNotAcceptable());
    }


    @Test
    public void testStartProduction() throws Exception {
        MvcResult result = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":10,\"citizens\":5,\"food\":10}"))
                .andExpect(status().isCreated())
                .andReturn();

        String kingdomId = JsonPath.read(result.getResponse().getContentAsString(), "$.id").toString();

        mockMvc.perform(post("/kingdoms/" + kingdomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gold").value(20))
                .andExpect(jsonPath("$.citizens").value(5))
                .andExpect(jsonPath("$.food").value(5));
    }

    @Test
    public void testStartProductionWithInsufficientFood() throws Exception {
        MvcResult result = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":10,\"citizens\":10,\"food\":5}"))
                .andExpect(status().isCreated())
                .andReturn();

        String kingdomId = JsonPath.read(result.getResponse().getContentAsString(), "$.id").toString();

        mockMvc.perform(post("/kingdoms/" + kingdomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gold").value(20))
                .andExpect(jsonPath("$.citizens").value(5))
                .andExpect(jsonPath("$.food").value(0));
    }

    @Test
    public void testStartProductionWithNoCitizens() throws Exception {
        MvcResult result = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":10,\"citizens\":2,\"food\":0}"))
                .andExpect(status().isCreated())
                .andReturn();

        String kingdomId = JsonPath.read(result.getResponse().getContentAsString(), "$.id").toString();

        mockMvc.perform(get("/kingdoms/" + kingdomId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/kingdoms/" + kingdomId))
                .andExpect(status().isNotAcceptable());

        mockMvc.perform(get("/kingdoms/" + kingdomId))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testInvestFood() throws Exception {
        MvcResult result = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":10,\"citizens\":5,\"food\":5}"))
                .andExpect(status().isCreated())
                .andReturn();

        String kingdomId = JsonPath.read(result.getResponse().getContentAsString(), "$.id").toString();

        mockMvc.perform(post("/kingdoms/" + kingdomId + "/invest?type=food")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gold").value(5))
                .andExpect(jsonPath("$.food").value(15));
    }

    @Test
    public void testInvestFoodWithoutEnoughGold() throws Exception {
        MvcResult result = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":2,\"citizens\":5,\"food\":5}"))
                .andExpect(status().isCreated())
                .andReturn();

        String kingdomId = JsonPath.read(result.getResponse().getContentAsString(), "$.id").toString();

        mockMvc.perform(post("/kingdoms/" + kingdomId + "/invest?type=food")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":5}"))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void testInvestCitizens() throws Exception {
        MvcResult result = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":10,\"citizens\":5,\"food\":5}"))
                .andExpect(status().isCreated())
                .andReturn();

        String kingdomId = JsonPath.read(result.getResponse().getContentAsString(), "$.id").toString();

        mockMvc.perform(post("/kingdoms/" + kingdomId + "/invest?type=citizens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gold").value(5))
                .andExpect(jsonPath("$.citizens").value(10));
    }

    @Test
    public void testInvestCitizensWithoutEnoughGold() throws Exception {
        MvcResult result = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":2,\"citizens\":5,\"food\":5}"))
                .andExpect(status().isCreated())
                .andReturn();

        String kingdomId = JsonPath.read(result.getResponse().getContentAsString(), "$.id").toString();

        mockMvc.perform(post("/kingdoms/" + kingdomId + "/invest?type=citizens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":5}"))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void testInvestWithInvalidType() throws Exception {
        MvcResult result = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":10,\"citizens\":5,\"food\":5}"))
                .andExpect(status().isCreated())
                .andReturn();

        String kingdomId = JsonPath.read(result.getResponse().getContentAsString(), "$.id").toString();

        mockMvc.perform(post("/kingdoms/" + kingdomId + "/invest?type=invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":5}"))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void testGetKingdom() throws Exception {
        MvcResult result = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":10,\"citizens\":10,\"food\":10}"))
                .andExpect(status().isCreated())
                .andReturn();

        String kingdomId = JsonPath.read(result.getResponse().getContentAsString(), "$.id").toString();

        mockMvc.perform(get("/kingdoms/" + kingdomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(kingdomId))
                .andExpect(jsonPath("$.gold").value(10))
                .andExpect(jsonPath("$.citizens").value(10))
                .andExpect(jsonPath("$.food").value(10))
                .andExpect(jsonPath("$.dateOfCreation").exists());
    }


    @Test
    public void testGetInvalidKingdom() throws Exception {
        MvcResult result = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":10,\"citizens\":10,\"food\":10}"))
                .andExpect(status().isCreated())
                .andReturn();

        String kingdomId = JsonPath.read(result.getResponse().getContentAsString(), "$.id").toString();

        mockMvc.perform(get("/kingdoms/" + "macarena"))
                .andExpect(status().isNotFound());

    }


    @Test
    public void testGetRichestKingdom() throws Exception {
        mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":50,\"citizens\":20,\"food\":30}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":60,\"citizens\":15,\"food\":25}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/kingdoms/richest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gold").value(60))
                .andExpect(jsonPath("$.citizens").value(15))
                .andExpect(jsonPath("$.food").value(25))
                .andExpect(jsonPath("$.dateOfCreation").exists());
    }


    @Test
    public void testAttackKingdom() throws Exception {
        MvcResult attackerResult = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":30,\"citizens\":10,\"food\":20}"))
                .andExpect(status().isCreated())
                .andReturn();

        String attackerId = JsonPath.read(attackerResult.getResponse().getContentAsString(), "$.id").toString();

        MvcResult targetResult = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":20,\"citizens\":10,\"food\":20}"))
                .andExpect(status().isCreated())
                .andReturn();

        String targetId = JsonPath.read(targetResult.getResponse().getContentAsString(), "$.id").toString();

        mockMvc.perform(post("/kingdoms/" + attackerId + "/attack/" + targetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gold").value(0))
                .andExpect(jsonPath("$.citizens").value(5));

        mockMvc.perform(get("/kingdoms/" + targetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gold").value(50))
                .andExpect(jsonPath("$.citizens").value(15));
    }

    @Test
    public void testAttackKingdomWithEmptyTarget() throws Exception {
        MvcResult attackerResult = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":30,\"citizens\":10,\"food\":20}"))
                .andExpect(status().isCreated())
                .andReturn();

        String attackerId = JsonPath.read(attackerResult.getResponse().getContentAsString(), "$.id").toString();

        MvcResult targetResult = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":10,\"citizens\":6,\"food\":6}"))
                .andExpect(status().isCreated())
                .andReturn();

        String targetId = JsonPath.read(targetResult.getResponse().getContentAsString(), "$.id").toString();

        mockMvc.perform(post("/kingdoms/" + attackerId + "/attack/" + targetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gold").value(40))
                .andExpect(jsonPath("$.citizens").value(13))
                .andExpect(jsonPath("$.food").value(20));

        mockMvc.perform(get("/kingdoms/" + targetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gold").value(0))
                .andExpect(jsonPath("$.citizens").value(3))
                .andExpect(jsonPath("$.food").value(6));
    }

    @Test
    public void testAttackKingdomWithStrongDefender() throws Exception {
        MvcResult attackerResult = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":30,\"citizens\":5,\"food\":20}"))
                .andExpect(status().isCreated())
                .andReturn();

        String attackerId = JsonPath.read(attackerResult.getResponse().getContentAsString(), "$.id").toString();

        MvcResult targetResult = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":50,\"citizens\":20,\"food\":30}"))
                .andExpect(status().isCreated())
                .andReturn();

        String targetId = JsonPath.read(targetResult.getResponse().getContentAsString(), "$.id").toString();

        mockMvc.perform(post("/kingdoms/" + attackerId + "/attack/" + targetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gold").value(0))
                .andExpect(jsonPath("$.citizens").value(2));


        mockMvc.perform(get("/kingdoms/" + targetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gold").value(80))
                .andExpect(jsonPath("$.citizens").value(22));
    }

    @Test
    public void testAttackKingdomWithDefenderHavingMoreGold() throws Exception {
        MvcResult attackerResult = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":10,\"citizens\":10,\"food\":10}"))
                .andExpect(status().isCreated())
                .andReturn();

        String attackerId = JsonPath.read(attackerResult.getResponse().getContentAsString(), "$.id").toString();

        MvcResult targetResult = mockMvc.perform(post("/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"gold\":50,\"citizens\":10,\"food\":10}"))
                .andExpect(status().isCreated())
                .andReturn();

        String targetId = JsonPath.read(targetResult.getResponse().getContentAsString(), "$.id").toString();

        mockMvc.perform(post("/kingdoms/" + attackerId + "/attack/" + targetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gold").value(0))
                .andExpect(jsonPath("$.citizens").value(5));


        mockMvc.perform(get("/kingdoms/" + targetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gold").value(60))
                .andExpect(jsonPath("$.citizens").value(15));
    }

}
