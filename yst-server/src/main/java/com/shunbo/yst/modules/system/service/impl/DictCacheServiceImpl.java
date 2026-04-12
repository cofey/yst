package com.shunbo.yst.modules.system.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.shunbo.yst.modules.system.service.DictCacheService;
import com.shunbo.yst.modules.system.vo.DictOptionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * DictCacheServiceImpl 服务实现，负责执行业务流程与数据编排。
 */
@Service
@RequiredArgsConstructor
public class DictCacheServiceImpl implements DictCacheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictCacheServiceImpl.class);
    private static final String KEY_PREFIX = "sys_dict:";
    private static final Duration TTL = Duration.ofHours(12);
    private static final TypeReference<List<DictOptionVO>> DICT_OPTION_LIST_TYPE = new TypeReference<>() { };

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 执行getDictOptions相关处理。
     */
    @Override
    public List<DictOptionVO> getDictOptions(String dictType) {
        if (!StringUtils.hasText(dictType)) {
            return null;
        }
        try {
            String raw = redisTemplate.opsForValue().get(buildKey(dictType));
            if (!StringUtils.hasText(raw)) {
                return null;
            }
            return objectMapper.readValue(raw, DICT_OPTION_LIST_TYPE);
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("读取字典缓存失败, dictType={}", dictType, e);
            return null;
        }
    }

    /**
     * 执行putDictOptions相关处理。
     */
    @Override
    public void putDictOptions(String dictType, List<DictOptionVO> options) {
        if (!StringUtils.hasText(dictType)) {
            return;
        }
        try {
            redisTemplate.opsForValue().set(buildKey(dictType), objectMapper.writeValueAsString(options), TTL);
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("写入字典缓存失败, dictType={}", dictType, e);
        }
    }

    /**
     * 执行evictDictOptions相关处理。
     */
    @Override
    public void evictDictOptions(String dictType) {
        if (!StringUtils.hasText(dictType)) {
            return;
        }
        try {
            redisTemplate.delete(buildKey(dictType));
        } catch (RuntimeException e) {
            LOGGER.warn("清理字典缓存失败, dictType={}", dictType, e);
        }
    }

    /**
     * 执行evictAllDictOptions相关处理。
     */
    @Override
    public void evictAllDictOptions() {
        try {
            redisTemplate.execute((RedisCallback<Void>) connection -> {
                ScanOptions options = ScanOptions.scanOptions().match(KEY_PREFIX + "*").count(200).build();
                List<byte[]> keys = new ArrayList<>();
                try (var cursor = connection.scan(options)) {
                    while (cursor.hasNext()) {
                        keys.add(cursor.next());
                    }
                }
                if (!keys.isEmpty()) {
                    connection.del(keys.toArray(new byte[0][]));
                }
                return null;
            });
        } catch (RuntimeException e) {
            LOGGER.warn("清理全部字典缓存失败", e);
        }
    }

    private String buildKey(String dictType) {
        return KEY_PREFIX + dictType;
    }
}
