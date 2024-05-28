package firstskin.firstskin.admin.api.controller;

import firstskin.firstskin.admin.api.dto.request.RestudyRequest;
import firstskin.firstskin.admin.service.RestudyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/restudy")
@Slf4j
@RequiredArgsConstructor
public class RestudyController {

    private final RestudyService restudyService;

    @PostMapping
    @Operation(summary = "재학습")
    public void restudy(@RequestBody RestudyRequest request) {
        restudyService.restudy(request);
    }
}
