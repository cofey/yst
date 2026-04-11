package com.shunbo.yst.modules.system.dict.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shunbo.yst.modules.system.dict.service.DictCacheService;
import com.shunbo.yst.modules.system.dict.vo.DictOptionVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DictCacheServiceImpl implements DictCacheService {

    private static final String KEY_PREFIX = "sys_dict:";
    private static final Duration TTL = Duration.ofHours(12);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

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
            return objectMapper.readValue(raw, new TypeReference<>() { });
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void putDictOptions(String dictType, List<DictOptionVO> options) {
        if (!StringUtils.hasText(dictType)) {
            return;
        }
        try {
            redisTemplate.opsForValue().set(buildKey(dictType), objectMapper.writeValueAsString(options), TTL);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void evictDictOptions(String dictType) {
        if (!StringUtils.hasText(dictType)) {
            return;
        }
        try {
            redisTemplate.delete(buildKey(dictType));
        } catch (Exception ignored) {
        }
    }

    private String buildKey(String dictType) {
        return KEY_PREFIX + dictType;
    }
}
