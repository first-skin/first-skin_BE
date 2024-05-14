package firstskin.firstskin.dianosis.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import firstskin.firstskin.dianosis.api.request.CosmeticRequest;
import firstskin.firstskin.dianosis.api.response.CosmeticPageResponse;
import firstskin.firstskin.dianosis.service.CosmeticService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cosmetics")
public class CosmeticController {

    private final CosmeticService cosmeticService;

    @GetMapping
    public CosmeticPageResponse searchCosmetics(CosmeticRequest request) throws JsonProcessingException {

        return cosmeticService.searchCosmetics(request);

    }
}
