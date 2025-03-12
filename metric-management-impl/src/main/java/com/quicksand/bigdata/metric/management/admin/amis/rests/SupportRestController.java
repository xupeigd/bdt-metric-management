package com.quicksand.bigdata.metric.management.admin.amis.rests;

import com.quicksand.bigdata.metric.management.admin.amis.consts.Vars;
import com.quicksand.bigdata.metric.management.admin.amis.model.FrameworkResponse;
import com.quicksand.bigdata.metric.management.admin.amis.model.SqlDebugModel;
import com.quicksand.bigdata.metric.management.identify.models.UserAuthModel;
import com.quicksand.bigdata.metric.management.identify.models.UserLoginModel;
import com.quicksand.bigdata.metric.management.identify.rests.AuthenticationController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class SupportRestController {

    @Resource
    AuthenticationController authenticationController;

    @Operation(description = "管理框架登陆")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "operation success ! "),
            @ApiResponse(responseCode = "404", description = "用户名不存在 !"),
            @ApiResponse(responseCode = "401", description = "用户名/密码错误 !"),
            @ApiResponse(responseCode = "423", description = "账号不可用 !")
    })
    @CrossOrigin
    @PostMapping(Vars.PATH_ROOT + "/login")
    public FrameworkResponse<UserAuthModel, SqlDebugModel> frameworkLogin(@RequestBody @Validated UserLoginModel model) {
        return FrameworkResponse.extend(authenticationController.login(model));
    }

    @GetMapping({"/admin-api/{resources}",
            "/admin-api/system/{resources}",
            "/admin-api/system/{resources}/{id}/edit"})
    public ResponseEntity<byte[]> getJson(@PathVariable("resources") String resources,
                                          @PathVariable(name = "id", required = false) Long id,
                                          @RequestParam(value = "_action", required = false) String action) throws IOException {
        ClassPathResource resource = new ClassPathResource(StringUtils.hasText(action)
                ? String.format("/static/amis-api-json/%s_action_%s.json", resources, action)
                : String.format("/static/amis-api-json/%s.json", resources));
        Path path = Paths.get(resource.getURI());
        byte[] content = Files.readAllBytes(path);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("inline;filename=/admin-api/%s", resource))
                .contentType(MediaType.APPLICATION_JSON)
                .body(content);
    }

    @PostMapping("/admin-api/{resources}")
    public ResponseEntity<byte[]> postAction(@PathVariable("resources") String resources) throws IOException {
        ClassPathResource resource = new ClassPathResource(String.format("/static/amis-api-json/%s-action.json", resources));
        Path path = Paths.get(resource.getURI());
        byte[] content = Files.readAllBytes(path);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("inline;filename=/admin-api/%s", resource))
                .contentType(MediaType.APPLICATION_JSON)
                .body(content);
    }

}