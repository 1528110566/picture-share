package com.hf6z.service.impl;

import com.hf6z.controller.config.Constant;
import com.hf6z.dao.LikeRecordMapper;
import com.hf6z.dao.ReadRecordMapper;
import com.hf6z.model.*;
import com.hf6z.service.*;
import com.hf6z.task.CommentTask;
import com.hf6z.task.RedisScheduleTask;
import com.hf6z.task.WishTask;
import com.hf6z.vo.PictureVO;
import com.hf6z.vo.UploadRankVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author Tao Zheng
 * @date 2020-01-17 8:31
 */
@Service
public class RedisServiceImpl implements RedisService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisServiceImpl.class);
    @Resource
    private PicService picService;
    @Resource
    private CommentService commentService;
    @Resource
    private TopPicService topPicService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private LikeRecordMapper likeRecordMapper;
    @Resource
    private ReadRecordMapper readRecordMapper;
    @Resource
    private WishService wishService;
//    @Resource
//    private RedisTemplate<String, Object> redisTemplate;

    @Value(Constant.REDIS_HASH_KEY)
    private String hashKey;
    @Value(Constant.REDIS_ZSET_KEY_READ_RANK)
    private String zsetReadKey;
    @Value(Constant.REDIS_ZSET_KEY_LIKE_RANK)
    private String zsetLikeKey;
    @Value(Constant.REDIS_SET_KEY_CHANGE)
    private String setChangedKey;
    @Value(Constant.REDIS_VALUE_KEY_COMMENT)
    private String commentKey;
    @Value(Constant.REDIS_SET_KEY_TOP)
    private String topKey;
    @Value(Constant.REDIS_VALUE_KEY_WISH)
    private String wishKey;
    @Value(Constant.REDIS_VALUE_KEY_LAST_SAVE_DATE)
    private String lastSaveDateKey;
    @Value(Constant.REDIS_SET_KEY_LOGIN_STATE)
    private String loginStateSetKey;
    @Value(Constant.REDIS_ZSET_KEY_UPLOAD_RANK)
    private String uploadRankKey;
    @Value(Constant.REDIS_VALUE_KEY_READ_RECORD)
    private String readRecordKey;
    @Value(Constant.REDIS_VALUE_KEY_LIKE_RECORD)
    private String likeRecordKey;
    @Value(Constant.REDIS_HASH_KEY_SOURCE_LINK)
    private String sourceLinkKey;
    @Value(Constant.REDIS_HASH_KEY_ZIPPED_LINK)
    private String zippedLinkKey;
    @Value(Constant.REDIS_HASH_KEY_CREATE_USER)
    private String createUserKey;
    @Value(Constant.REDIS_HASH_KEY_CREATE_DATE)
    private String createDateKey;
    @Value(Constant.REDIS_HASH_KEY_READ_NUM)
    private String readNumKey;
    @Value(Constant.REDIS_HASH_KEY_LIKE_NUM)
    private String likeNumKey;
    @Value(Constant.REDIS_HASH_KEY_DESCRIPTION)
    private String descriptionKey;

    @PostConstruct
    public void init() {
        LOGGER.info("RedisServiceImpl初始化进行中，将[清除Redis中的所有信息]并将数据库中的相应信息初始化至Redis中...");
        refreshAllPicVO();
        refreshAllComment();
        refreshAllTopPic();
        refreshAllUploadRank();
        LOGGER.info("RedisServiceImpl初始化结束");
    }

    private void putPictureVO(PictureVO pictureVO) {
        putPictureVO(pictureVO, null);
    }

    private void putPictureVO(PictureVO pictureVO, Map<String, String> map) {
        if (map == null) {
            map = new HashMap<>(PictureVO.argumentNum);
        }
        String key = hashKey + pictureVO.getId();
        map.put(createUserKey, pictureVO.getCreateUser());
        map.put(createDateKey, pictureVO.getCreateDate());
        map.put(zippedLinkKey, pictureVO.getZippedLink());
        map.put(sourceLinkKey, pictureVO.getSourceLink());
        map.put(readNumKey, pictureVO.getReadNum());
        map.put(likeNumKey, pictureVO.getLikeNum());
        map.put(descriptionKey, pictureVO.getDescription());
        stringRedisTemplate.opsForHash().putAll(key, map);
//        LOGGER.info("[{}]已放入Redis", key);
    }

    @Override
    public void addReadNum(int picId, String username) {
        String key = hashKey + picId;
        // 增加redis中pic的hash中的readNum字段的值
        stringRedisTemplate.opsForHash().increment(key, readNumKey, 1);
        // 增加redis中pic:readNum的zset对应id的值
        stringRedisTemplate.opsForZSet().incrementScore(zsetReadKey, String.valueOf(picId), 1);

        String readKey = readRecordKey + picId + ":" + username + ":" + System.currentTimeMillis();
        stringRedisTemplate.opsForValue().set(readKey, "", 5, TimeUnit.MINUTES);
    }

    @Override
    public void addLikeNum(int picId, String username) {
        String key = hashKey + picId;
        // 增加redis中pic的hash中的likeNum字段的值
        stringRedisTemplate.opsForHash().increment(key, likeNumKey, 1);
        // 增加redis中pic:likeNum的zset对应id的值
        stringRedisTemplate.opsForZSet().incrementScore(zsetLikeKey, String.valueOf(picId), 1);

        String likeKey = likeRecordKey + picId + ":" + username + ":" + System.currentTimeMillis();
        stringRedisTemplate.opsForValue().set(likeKey, "", 5, TimeUnit.MINUTES);
    }

    private String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return simpleDateFormat.format(new Date());
    }

    @Override
    public String getPicZippedLink(int picId) {
        String key = Constant.REDIS_HASH_KEY + picId;
        return (String) stringRedisTemplate.opsForHash().get(key, zippedLinkKey);
    }

    @Override
    public String getPicSourceLink(int picId) {
        String key = Constant.REDIS_HASH_KEY + picId;
        return (String) stringRedisTemplate.opsForHash().get(key, sourceLinkKey);
    }


    @Override
    public List<PictureVO> getHotPicByReadNum(int num) {
        Set<String> set = stringRedisTemplate.opsForZSet().reverseRange(zsetReadKey, 0, num - 1);
        List<PictureVO> pictureVOList = new ArrayList<>(num);
        if (set == null) {
            LOGGER.error("获取hotPic为空！数据库中可能没有数据！");
        } else {
            for (String id : set) {
                pictureVOList.add(genPicVo(id, true));
            }
        }
        return pictureVOList;
    }

    @Override
    public void addChangedPic(int picId) {
        stringRedisTemplate.opsForSet().add(setChangedKey, String.valueOf(picId));
    }

    @Override
    public Set<String> getAllChangedPicId() {
        return stringRedisTemplate.opsForSet().members(setChangedKey);
    }

    @Override
    public PictureVO getPictureVoById(int picId) {
        String keyToHash = hashKey + picId;
        Set<String> key = stringRedisTemplate.keys(keyToHash);
        if (key != null && key.size() == 1) {
            return genPicVo(String.valueOf(picId), true);
        }
        return null;
    }

    @Override
    public List<PictureVO> getTopPicVO() {
        Set<String> topPicIds = stringRedisTemplate.opsForSet().members(topKey);
        List<PictureVO> pictureVOList = new LinkedList<>();
        if (topPicIds != null && topPicIds.size() != 0) {
            for (String picId : topPicIds) {
                pictureVOList.add(getPictureVoById(Integer.parseInt(picId)));
            }
        }
        return pictureVOList;
    }

    @Override
    public void clearChangedSet() {
        stringRedisTemplate.delete(setChangedKey);
    }

    @Override
    public void putVO2Redis(List<PictureVO> list) {
        Map<String, String> map = new HashMap<>(PictureVO.argumentNum);
        for (PictureVO pictureVO : list) {
            map.clear();
            putPictureVO(pictureVO, map);
        }
    }

    @Override
    public List<PictureVO> getAllPic() {
        Set<String> keys = stringRedisTemplate.keys("pic:hash:*");
        List<PictureVO> list = new LinkedList<>();
        if (keys == null || keys.size() == 0) {
            return list;
        }
        for (String key : keys) {
            list.add(genPicVo(key, false));
        }
        return list;
    }

    @Override
    public int refreshAllPicVO() {
        // 清空redis
        stringRedisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.flushAll();
            return null;
        });
        List<PictureVO> pictureVOList = picService.getAllPic();
        Map<String, String> map = new HashMap<>(PictureVO.argumentNum);
        for (PictureVO pictureVO : pictureVOList) {
            map.clear();
            putPictureVO(pictureVO, map);
            stringRedisTemplate.opsForZSet().add(zsetReadKey, String.valueOf(pictureVO.getId()), Integer.parseInt(pictureVO.getReadNum()));
            stringRedisTemplate.opsForZSet().add(zsetLikeKey, String.valueOf(pictureVO.getId()), Integer.parseInt(pictureVO.getLikeNum()));
        }
        return pictureVOList.size();
    }

    @Override
    public int refreshAllComment() {
        List<Comment> list = commentService.getAllComment();
        for (Comment comment : list) {
            String key = commentKey + comment.getPicid() + ":" + comment.getUsername() + ":" + getDate(comment.getCreateDate()) + ":" + comment.getId();
            stringRedisTemplate.opsForValue().set(key, comment.getDetail());
        }
        return list.size();
    }

    @Override
    public int refreshAllTopPic() {
        List<TopPic> topPics = topPicService.getAll();
        for (TopPic topPic : topPics) {
            stringRedisTemplate.opsForSet().add(topKey, String.valueOf(topPic.getId()));
        }
        return topPics.size();
    }

    @Override
    public void refreshAllUploadRank() {
        List<UploadRankVO> uploadRankVOList = picService.getUploadRank();
        for (UploadRankVO uploadRankVO : uploadRankVOList) {
            stringRedisTemplate.opsForZSet().add(uploadRankKey, uploadRankVO.getUsername(), uploadRankVO.getUploadNum());
        }
    }

    private long getDate(Date date) {
        return date.getTime();
    }

    @Override
    public List<String> getAllReadRecord() {
        Set<String> keys = stringRedisTemplate.keys(readRecordKey + "*");
        if (keys != null) {
            List<String> list = new ArrayList<>(keys.size());
            list.addAll(keys);
            return list;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getAllLikeRecord() {
        Set<String> keys = stringRedisTemplate.keys(likeRecordKey + "*");
        if (keys != null) {
            List<String> list = new ArrayList<>(keys.size());
            list.addAll(keys);
            return list;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void updateVO(int id, String desc) {
        if (id != 0) {
            PictureVO pictureVO = getPictureVoById(id);
            if (pictureVO != null) {
                pictureVO.setDescription(desc);
                putPictureVO(pictureVO);
            }
        }
    }

    @Override
    public int getNextId(int id) {
        Set<String> keys = stringRedisTemplate.keys(hashKey + "*");
        if (keys != null) {
            int maxId = getMaxId(keys);
            for (int i = id + 1; i <= maxId; i++) {
                String genKey = hashKey + i;
                if (keys.contains(genKey)) {
                    return i;
                }
            }
            return maxId;
        }
        return 0;
    }

    @Override
    public int getPreId(int id) {
        Set<String> keys = stringRedisTemplate.keys(hashKey + "*");
        if (keys != null) {
            int min = getMinId(keys);
            for (int i = id - 1; i >= min; i--) {
                String genKey = hashKey + i;
                if (keys.contains(genKey)) {
                    return i;
                }
            }
            return min;
        }
        return 0;
    }

    @Override
    public Boolean deleteVOById(int id) {
        String keyToHash = hashKey + id;
        return stringRedisTemplate.delete(keyToHash);
    }

    @Override
    public List<Comment> getCommentByPicId(int id) {
        Set<String> keys = stringRedisTemplate.keys(commentKey + id + "*");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Comment> list = new LinkedList<>();
        if (keys != null && keys.size() != 0) {
            for (String key : keys) {
                try {
                    String detail = stringRedisTemplate.opsForValue().get(key);
                    String[] keyInfo = key.split(":");
                    int picId = Integer.parseInt(keyInfo[2]);
                    String username = keyInfo[3];
                    Long temp = Long.parseLong(keyInfo[4]);
                    String dateStr = simpleDateFormat.format(temp);
                    Date date = simpleDateFormat.parse(dateStr);
                    Comment comment = new Comment();
                    comment.setUsername(username);
                    comment.setPicid(picId);
                    comment.setDetail(detail);
                    comment.setCreateDate(date);
                    // 长度为5，说明是新生成的comment
                    if (keyInfo.length == 5) {
                        list.add(comment);
                    }
                    // 从数据库中刷新到redis的，所以最后有commentId
                    else if (keyInfo.length == 6) {
                        int commentId = Integer.parseInt(keyInfo[5]);
                        comment.setId(commentId);
                        list.add(comment);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    LOGGER.error(Arrays.toString(e.getStackTrace()));
                }
            }
        }
        return list;
    }

    @Override
    public void deleteCommentInRedis(String key) {
        Set<String> keys = stringRedisTemplate.keys(key + "*");
        if (keys != null) {
            List<String> list = new ArrayList<>(keys);
            stringRedisTemplate.delete(list.get(0));
        }
    }

    @Override
    public List<Comment> getAllCommentOnlyInRedis() {
        Set<String> keys = stringRedisTemplate.keys(commentKey + "*");
        List<Comment> list = new LinkedList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (keys != null && keys.size() != 0) {
            for (String key : keys) {
                try {
                    String[] keyInfo = key.split(":");
                    // 随后没有commentId，说明只在redis中存在
                    if (keyInfo.length == 5) {
                        String detail = stringRedisTemplate.opsForValue().get(key);
                        int picId = Integer.parseInt(keyInfo[2]);
                        String username = keyInfo[3];
                        Long temp = Long.parseLong(keyInfo[4]);
                        String dateStr = simpleDateFormat.format(temp);
                        Date date = simpleDateFormat.parse(dateStr);
                        Comment comment = new Comment();
                        comment.setUsername(username);
                        comment.setPicid(picId);
                        comment.setDetail(detail);
                        comment.setCreateDate(date);
                        list.add(comment);
                        // 获取之后即删除
                        stringRedisTemplate.delete(key);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    LOGGER.error(Arrays.toString(e.getStackTrace()));
                }
            }
        }
        return list;
    }

    @Override
    public void addComment(Comment comment) {
        String genKey;
        StringBuilder builder = new StringBuilder();
        builder.append(commentKey)
                .append(comment.getPicid()).append(":")
                .append(comment.getUsername()).append(":")
                .append(getDate(comment.getCreateDate()));
        if (comment.getId() != null) {
            builder.append(":").append(comment.getId());
        }
        genKey = builder.toString();
        stringRedisTemplate.opsForValue().set(genKey, comment.getDetail());
//        LOGGER.info("[{}]存入Redis中", genKey);
    }

    @Override
    public void addComment(List<Comment> list) {
        for (Comment comment : list) {
            addComment(comment);
        }
    }

    @Override
    // 进行必要的保存
    public void saveAsNeeded() {
        LOGGER.info("Redis缓存更新前的保存工作进行中...");
        saveReadAndLikeRecord();
        saveAllPicInfo();
        saveCommentTask();
        saveWishTask();
        saveLoginState();
        saveUploadRank();
        LOGGER.info("Redis缓存更新前的保存工作完成");
    }


    private void saveUploadRank() {

    }

    private Set<String> members = new HashSet<>();

    private void saveLoginState() {
        members.clear();
        Set<String> members = stringRedisTemplate.opsForSet().members(loginStateSetKey + "*");
        if (members != null) {
            for (String member : members) {
                String[] keyInfo = member.split(":");
                this.members.add(keyInfo[2]);
            }
        }
    }

    @Override
    public void addWish(Wish wish) {
        String genKey = wishKey + wish.getUsername() + ":" + wish.getCreateDate().getTime();
        stringRedisTemplate.opsForValue().set(genKey, wish.getDetail(), 5, TimeUnit.MINUTES);
    }

    @Override
    public List<Wish> getWishUnderDate() {
        Set<String> keys = stringRedisTemplate.keys(wishKey + "*");
        List<Wish> wishList = new LinkedList<>();
        String saveDate = stringRedisTemplate.opsForValue().get(lastSaveDateKey);
        if (keys != null && keys.size() != 0) {
            for (String key : keys) {
                try {
                    String[] keyInfo = key.split(":");
                    String username = keyInfo[1];
                    long tempDate = Long.parseLong(keyInfo[2]);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                    String dateStr = simpleDateFormat.format(tempDate);
                    Date date = simpleDateFormat.parse(dateStr);
                    if (saveDate != null) {
                        if (Long.parseLong(saveDate) < tempDate) {
                            Wish wish = new Wish();
                            wish.setUsername(username);
                            wish.setCreateDate(date);
                            wish.setDetail(stringRedisTemplate.opsForValue().get(key));
                            wishList.add(wish);
                        }
                    } else {
                        Wish wish = new Wish();
                        wish.setUsername(username);
                        wish.setCreateDate(date);
                        wish.setDetail(stringRedisTemplate.opsForValue().get(key));
                        wishList.add(wish);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    LOGGER.error(Arrays.toString(e.getStackTrace()));
                }
            }
        }
        return wishList;
    }

    @Override
    public void setLastSavedDate() {
        stringRedisTemplate.opsForValue().set(lastSaveDateKey, String.valueOf(new Date().getTime()));
    }

    @Override
    public List<Wish> getWish() {
        long current = System.currentTimeMillis();
        long temp = (current - 1500) / 1000;
        Set<String> keys = stringRedisTemplate.keys(wishKey + "*" + temp + "*");
        List<Wish> wishList = new LinkedList<>();
        if (keys != null && keys.size() != 0) {
            for (String key : keys) {
                try {
                    String[] keyInfo = key.split(":");
                    String username = keyInfo[1];
                    long tempDate = Long.parseLong(keyInfo[2]);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                    String dateStr = simpleDateFormat.format(tempDate);
                    Date date = simpleDateFormat.parse(dateStr);
                    Wish wish = new Wish();
                    wish.setUsername(username);
                    wish.setCreateDate(date);
                    wish.setDetail(stringRedisTemplate.opsForValue().get(key));
                    wishList.add(wish);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    LOGGER.error(Arrays.toString(e.getStackTrace()));
                }
            }
        }
        return wishList;
    }

    @Override
    public void removeLoginState(String username) {
        stringRedisTemplate.delete(loginStateSetKey + username);
    }

    @Override
    public void addLoginState(String username) {
        stringRedisTemplate.opsForValue().set(loginStateSetKey + username, String.valueOf(new Date().getTime()), 30, TimeUnit.MINUTES);
    }

    @Override
    public Integer getLoginState() {
        Set<String> loginState = stringRedisTemplate.keys(loginStateSetKey + "*");
        if (loginState != null) {
            return loginState.size();
        }
        return 0;
    }

    @Override
    public int refreshAllLoginState() {
        for (String member : this.members) {
            stringRedisTemplate.opsForSet().add(loginStateSetKey, member);
        }
        return members.size();
    }

    @Override
    public void addUploadRank(String username) {
        stringRedisTemplate.opsForZSet().incrementScore(uploadRankKey, username, 1);
    }

    @Override
    public List<UploadRankVO> getUploadRank() {
        Set<ZSetOperations.TypedTuple<String>> typedTuples =
                stringRedisTemplate.opsForZSet().reverseRangeWithScores(uploadRankKey, 0, 100);
        List<UploadRankVO> uploadRankVOList = new LinkedList<>();
        if (typedTuples != null && typedTuples.size() != 0) {
            for (ZSetOperations.TypedTuple<String> temp : typedTuples) {
                UploadRankVO uploadRankVO = new UploadRankVO();
                uploadRankVO.setUsername(temp.getValue());
                uploadRankVO.setUploadNum(Integer.parseInt(String.valueOf(temp.getScore())));
                uploadRankVOList.add(uploadRankVO);
            }
        }
        return uploadRankVOList;
    }

    private void saveReadAndLikeRecord() {
        Set<String> readNumRecords = stringRedisTemplate.keys(readRecordKey + "*");
        Set<String> likeNumRecords = stringRedisTemplate.keys(likeRecordKey + "*");
        Set<String> combine = new HashSet<>();
        if (readNumRecords != null) {
            combine.addAll(readNumRecords);
        }
        if (likeNumRecords != null) {
            combine.addAll(likeNumRecords);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (String key : combine) {
            String[] keyInfo = key.split(":");
            try {
                int picId = Integer.parseInt(keyInfo[3]);
                String username = keyInfo[4];
                Long temp = Long.parseLong(keyInfo[5]);
                String dateStr = simpleDateFormat.format(temp);
                Date date = simpleDateFormat.parse(dateStr);
                if (key.startsWith(Constant.REDIS_VALUE_KEY_READ_RECORD)) {
                    ReadRecord readRecord = new ReadRecord();
                    readRecord.setUsername(username);
                    readRecord.setPicid(picId);
                    readRecord.setCreateDate(date);
                    readRecordMapper.insert(readRecord);
                } else if (key.startsWith(Constant.REDIS_VALUE_KEY_LIKE_RECORD)) {
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

    private void saveAllPicInfo() {
        new RedisScheduleTask(1, this, picService).run();
    }

    private void saveCommentTask() {
        new CommentTask(commentService, this).run();
    }

    private void saveWishTask() {
        new WishTask(wishService, this).run();
    }

    private int getMaxId(Set<String> keys) {
        int max = 0;
        for (String key : keys) {
            int id = Integer.parseInt(key.split(":")[2]);
            if (max < id) {
                max = id;
            }
        }
        return max;
    }

    private int getMinId(Set<String> keys) {
        int min = 0;
        for (String key : keys) {
            int id = Integer.parseInt(key.split(":")[2]);
            if (min > id) {
                min = id;
            }
        }
        return min;
    }

    private String getValue(String key, String field) {
        return (String) stringRedisTemplate.opsForHash().get(key, field);
    }

    /**
     * @param genKey 传入的如果是picId，则将其置为true，会自动生成hashKey
     *               否则只为false
     */
    private PictureVO genPicVo(String key, boolean genKey) {
        String keyToHash;
        if (genKey) {
            keyToHash = hashKey + key;
        } else {
            keyToHash = key;
        }
        PictureVO pictureVO = new PictureVO();
        pictureVO.setId(key);
        pictureVO.setCreateDate(getValue(keyToHash, createDateKey));
        pictureVO.setCreateUser(getValue(keyToHash, createUserKey));
        pictureVO.setLikeNum(getValue(keyToHash, likeNumKey));
        pictureVO.setReadNum(getValue(keyToHash, readNumKey));
        pictureVO.setSourceLink(getValue(keyToHash, sourceLinkKey));
        pictureVO.setZippedLink(getValue(keyToHash, zippedLinkKey));
        pictureVO.setDescription(getValue(keyToHash, descriptionKey));
        return pictureVO;
    }
}
