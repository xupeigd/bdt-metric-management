package com.quicksand.bigdata.metric.management.advices;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * AppAuthRefreshEvent
 *
 * @author page
 * @date 2022/11/28
 */
public class AppAuthRefreshEvent
        extends ApplicationEvent {

    /**
     * 应用Id
     * <p>
     * > 0 表示特定的
     * = 0 表示全局
     */
    @Getter
    Integer appId;

    public AppAuthRefreshEvent(Integer appId) {
        super(appId);
        this.appId = appId;
    }

}
