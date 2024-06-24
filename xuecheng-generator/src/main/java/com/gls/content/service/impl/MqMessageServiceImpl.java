package com.gls.content.service.impl;

import com.gls.content.domain.po.MqMessage;
import com.gls.content.mapper.MqMessageMapper;
import com.gls.content.service.IMqMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 郭林赛
 * @since 2024-06-24
 */
@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements IMqMessageService {

}
