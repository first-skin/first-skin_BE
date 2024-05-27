//package firstskin.firstskin.admin.service;
//
//import firstskin.firstskin.admin.api.dto.request.RestudyRequest;
//import firstskin.firstskin.skin.Kind;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class RestudyServiceTest {
//
//    @Autowired
//    private RestudyService restudyService;
//
//    @Test
//    @DisplayName("재학습 하기")
//    public void restudyType() throws Exception{
//        //given
//        RestudyRequest request = RestudyRequest.builder()
//                .kind(Kind.TYPE)
//                .modelPath("/Users/wonu/Desktop/jarr/skintype_v1.h5")
//                .csvPath("/Users/wonu/Desktop/t24122/aidata/skintype/skintype_df.csv")
//                .build();
//        //when
//        restudyService.restudy(request);
//
//        //then
//
//    }
//
//}