package com.quicksand.bigdata.metric.management.admin.amis.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Class SettingModel
 *
 * @Author: ap100
 * @Date: 2024/9/3
 * @Description: settings
 */
@Data
public class SettingModel {

    NavContainers nav;
    Assets assets;
    @JsonAlias("app_name")
    @JsonProperty("app_name")
    String appName;
    String locale;
    Layout layout;
    String logo;
    @JsonAlias("login_captcha")
    @JsonProperty("login_captcha")
    boolean loginCaptcha;
    @JsonAlias("locale_options")
    @JsonProperty("locale_options")
    List<LocaleOption> localeOptions;
    @JsonAlias("show_development_tools")
    @JsonProperty("show_development_tools")
    boolean showDevelopmentTools;
    @JsonAlias("system_theme_setting")
    @JsonProperty("system_theme_setting")
    SystemThemeSetting systemThemeSetting;
    @JsonAlias("enabled_extensions")
    @JsonProperty("enabled_extensions")
    List<String> enabledExtensions;

    @Data
    public static class NavContainers {
        NavContainer appendNav;
        NavContainer prependNav;
    }

    @Data
    public static class NavContainer {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String type;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Boolean hideCaret;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String trigger;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String label;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String className;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String btnClassName;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String menuClassName;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String icon;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<NavItem> buttons;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<NavItem> items;
    }

    @Data
    public static class NavItem {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String type;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String className;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String label;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String onClick;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String body;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String actionType;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String icon;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        boolean blank;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String url;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String tooltip;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String iconClassName;
    }

    @Data
    public static class Assets {
        List<String> js;
        List<String> css;
        List<String> scripts;
        List<String> styles;
    }

    @Data
    public static class Layout {
        String title;
        Header header;
        @JsonAlias("locale_options")
        @JsonProperty("locale_options")
        Map<String, String> localeOptions;
        @JsonAlias("keep_alive_exclude")
        @JsonProperty("keep_alive_exclude")
        List<Object> keepAliveExclude;
        String footer;

        @Data
        public static class Header {
            boolean refresh;
            boolean dark;
            boolean full_screen;
            boolean locale_toggle;
            boolean theme_config;
            boolean theme_config_bak;
        }

    }

    @Data
    public static class LocaleOption {
        String label;
        String value;
    }

    @Data
    public static class SystemThemeSetting {
        boolean darkTheme;
        boolean footer;
        boolean breadcrumb;
        String themeColor;
        String layoutMode;
        String siderTheme;
        String topTheme;
        String animateInType;
        int animateInDuration;
        String animateOutType;
        int animateOutDuration;
        String loginTemplate;
        boolean keepAlive;
        boolean enableTab;
        boolean tabIcon;
        boolean accordionMenu;
    }
}