package firstskin.firstskin.dianosis.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CosmeticControllerTest {

    @Autowired
    private CosmeticController cosmeticController;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("화장품 목록 조회")
    public void searchCosmetics() throws Exception{
        //given
//        CosmeticRequest request = CosmeticRequest.builder()
//                .kind(Kind.TYPE)
//                .category("스킨/케어")
//                .query("수분")
//                .page(1)
//                .size(10)
//                .sort("sim")
//                .build();

        //expected

        mockMvc.perform(get("/api/cosmetics")
                        .param("kind", "TYPE")
                        .param("category", "스킨/케어")
                        .param("query", "수분")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "sim")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

    }

}