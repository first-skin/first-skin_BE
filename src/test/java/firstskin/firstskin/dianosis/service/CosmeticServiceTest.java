package firstskin.firstskin.dianosis.service;

import firstskin.firstskin.dianosis.api.request.CosmeticRequest;
import firstskin.firstskin.skin.Kind;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CosmeticServiceTest {

    @Autowired
    private CosmeticService cosmeticService;

    @Test
    @DisplayName("화장품을 검색하면 네이버 쇼피엥서 검색 결과를 가져온다.")
    public void searchCosmetics() throws Exception{
        //given
        CosmeticRequest request = CosmeticRequest.builder()
                .kind(Kind.TYPE)
                .category("스킨/케어")
                .query("수분")
                .page(1)
                .size(10)
                .sort("sim")
                .build();


        //when
        cosmeticService.searchCosmetics(request);


        //then

    }

}