package com.gls.content.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author 郭林赛
 * @since 2024-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mq_message")
public class MqMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息类型代码: course_publish ,  media_test
     */
    @TableField("message_type")
    private String messageType;

    /**
     * 关联业务信息
     */
    @TableField("business_key1")
    private String businessKey1;

    /**
     * 关联业务信息
     */
    @TableField("business_key2")
    private String businessKey2;

    /**
     * 关联业务信息
     */
    @TableField("business_key3")
    private String businessKey3;

    /**
     * 通知次数
     */
    @TableField("execute_num")
    private Integer executeNum;

    /**
     * 处理状态，0:初始，1:成功
     */
    @TableField("state")
    private String state;

    /**
     * 回复失败时间
     */
    @TableField("returnfailure_date")
    private LocalDateTime returnfailureDate;

    /**
     * 回复成功时间
     */
    @TableField("returnsuccess_date")
    private LocalDateTime returnsuccessDate;

    /**
     * 回复失败内容
     */
    @TableField("returnfailure_msg")
    private String returnfailureMsg;

    /**
     * 最近通知时间
     */
    @TableField("execute_date")
    private LocalDateTime executeDate;

    /**
     * 阶段1处理状态, 0:初始，1:成功
     */
    @TableField("stage_state1")
    private String stageState1;

    /**
     * 阶段2处理状态, 0:初始，1:成功
     */
    @TableField("stage_state2")
    private String stageState2;

    /**
     * 阶段3处理状态, 0:初始，1:成功
     */
    @TableField("stage_state3")
    private String stageState3;

    /**
     * 阶段4处理状态, 0:初始，1:成功
     */
    @TableField("stage_state4")
    private String stageState4;


}
