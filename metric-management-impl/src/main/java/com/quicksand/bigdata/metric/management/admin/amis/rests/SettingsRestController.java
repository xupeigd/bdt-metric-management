package com.quicksand.bigdata.metric.management.admin.amis.rests;

import com.quicksand.bigdata.metric.management.admin.amis.consts.Vars;
import com.quicksand.bigdata.metric.management.admin.amis.model.FrameworkResponse;
import com.quicksand.bigdata.metric.management.admin.amis.model.SettingModel;
import com.quicksand.bigdata.metric.management.admin.amis.model.SqlDebugModel;
import com.quicksand.bigdata.metric.management.admin.amis.model.UserMenusModel;
import com.quicksand.bigdata.vars.security.vos.UserSecurityDetails;
import com.quicksand.bigdata.vars.util.JsonUtils;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class SettingsRestController
 *
 * @Author: ap100
 * @Date: 2024/9/3
 * @Description: 设置
 */
@RestController
public class SettingsRestController {

    @Data
    public static final class SettingResponse
            extends FrameworkResponse<SettingModel, SqlDebugModel> {

    }

    @Data
    public static final class UserMenusResponse
            extends FrameworkResponse<UserMenusModel, SqlDebugModel> {

    }

    @SneakyThrows
    @GetMapping(Vars.PATH_ROOT + "/_settings")
    public FrameworkResponse<SettingModel, SqlDebugModel> settings() throws IOException {
        ClassPathResource resource = new ClassPathResource("/static/amis-api-json/_settings.json");
        Path path = Paths.get(resource.getURI());
        byte[] content = Files.readAllBytes(path);
        FrameworkResponse<SettingModel, SqlDebugModel> frameworkResponse = JsonUtils.parseTo(new String(content), SettingResponse.class);
        assert null != frameworkResponse;
        if (null != frameworkResponse.getData() && null != frameworkResponse.getData().getLocaleOptions()) {
            frameworkResponse.getData().setLocaleOptions(null);
        }
        return frameworkResponse;
    }

    @SuppressWarnings("PatternVariableCanBeUsed")
    @GetMapping(Vars.PATH_ROOT + "/current-user")
    @PreAuthorize("isAuthenticated()")
    public FrameworkResponse<UserMenusModel, SqlDebugModel> userMenus() throws IOException {
        ClassPathResource resource = new ClassPathResource("/static/amis-api-json/current-user.json");
        Path path = Paths.get(resource.getURI());
        byte[] content = Files.readAllBytes(path);
        FrameworkResponse<UserMenusModel, SqlDebugModel> frameworkResponse = JsonUtils.parseTo(new String(content), UserMenusResponse.class);
        if (null != SecurityContextHolder.getContext()
                && null != SecurityContextHolder.getContext().getAuthentication()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (null != frameworkResponse
                    && null != frameworkResponse.getData()
                    && null != authentication &&
                    null != authentication.getPrincipal()
                    && authentication.getPrincipal() instanceof UserSecurityDetails) {
                UserSecurityDetails userSecurityDetails = (UserSecurityDetails) authentication.getPrincipal();
                frameworkResponse.getData().setUserName(userSecurityDetails.getName());
                frameworkResponse.getData().getMenus().getButtons().remove(1);
            }
        }
        return frameworkResponse;
    }


}
