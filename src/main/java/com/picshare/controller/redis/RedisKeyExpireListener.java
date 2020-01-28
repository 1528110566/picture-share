package com.picshare.controller.redis;

import com.picshare.controller.config.Constant;
import com.picshare.dao.LikeRecordMapper;
import com.picshare.dao.ReadRecordMapper;
import com.picshare.model.LikeRecord;
import com.picshare.model.ReadRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-19 16:26
 */
@Component
public class RedisKeyExpireListener extends KeyExpirationEventMessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisKeyExpireListener.class);
    @Resource
    private ReadRecordMapper readRecordMapper;
    @Resource
    private LikeRecordMapper likeRecordMapper;

    public RedisKeyExpireListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // message.toString()可以获取失效的key
        String expiredKey = message.toString();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (expiredKey.contains(Constant.REDIS_VALUE_KEY_READ_RECORD)
                || expiredKey.contains(Constant.REDIS_VALUE_KEY_LIKE_RECORD)) {
            String[] keyInfo = expiredKey.split(":");
            try {
                int picId = Integer.parseInt(keyInfo[3]);
                String username = keyInfo[4];
                Long temp = Long.parseLong(keyInfo[5]);
                String dateStr = simpleDateFormat.format(temp);
                Date date = simpleDateFormat.parse(dateStr);
                if (expiredKey.startsWith(Constant.REDIS_VALUE_KEY_READ_RECORD)) {
                    ReadRecord readRecord = new ReadRecord();
                    readRecord.setUsername(username);
                    readRecord.setPicid(picId);
                    readRecord.setCreateDate(date);
                    readRecordMapper.insert(readRecord);
                } else if (expiredKey.startsWith(Constant.REDIS_VALUE_KEY_LIKE_RECORD)) {
                    LikeRecord likeRecord = new LikeRecord();
                    likeRecord.setUsername(username);
                    likeRecord.setPicid(picId);
                    likeRecord.setCreateDate(date);
                    likeRecordMapper.insert(likeRecord);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                LOGGER.error(Arrays.toString(e.getStackTrace()));
            }
        }
    }
}
