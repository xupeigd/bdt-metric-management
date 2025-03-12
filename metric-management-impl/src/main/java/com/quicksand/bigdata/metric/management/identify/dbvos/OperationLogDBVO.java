package com.quicksand.bigdata.metric.management.identify.dbvos;

import com.quicksand.bigdata.metric.management.consts.OperationLogType;
import com.quicksand.bigdata.metric.management.consts.TableNames;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

/**
 * OperationLogDBVO
 *
 * @author page
 * @date 2020/8/18 16:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TableNames.TABLE_OPERATION_LOG)
@Where(clause = " status =1 ")
public class OperationLogDBVO {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TableNames.TABLE_IDS)
    @Column(columnDefinition = " bigint(20) NOT NULL DEFAULT 0 COMMENT '逻辑主键' ")
    Long id;

    @Column(name = "operation_time", columnDefinition = " datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间' ")
    Date operationTime;

    @Column(length = 32, columnDefinition = " varchar(32) NOT NULL DEFAULT '' COMMENT '操作IP' ")
    String ip;

    @Column(length = 64, columnDefinition = " varchar(64) NOT NULL DEFAULT '' COMMENT '所属地址' ")
    String address;

    /**
     * 操作用户名
     */
    @Column(name = "user_id", columnDefinition = " bigint(11) NOT NULL DEFAULT 0 COMMENT '操作用户' ")
    Integer userId;

    /**
     * 日志详情
     */
    @Column(columnDefinition = " text DEFAULT NULL COMMENT '日志详情' ")
    String detail;

    /**
     * 分类类型
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = " smallint(4) DEFAULT NULL COMMENT '日志分类 0默认 1登陆 2登出' ")
    OperationLogType type;

}
