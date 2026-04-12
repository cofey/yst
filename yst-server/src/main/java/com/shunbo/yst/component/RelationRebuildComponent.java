package com.shunbo.yst.component;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 通用关联关系重建组件，用于“先清空旧关系，再按新集合重建”。
 *
 * <p>用法示例：</p>
 * <pre>{@code
 * relationRebuildComponent.rebuild(roleIds,
 *         () -> sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
 *                 .eq(SysUserRole::getUserId, userId)),
 *         roleId -> {
 *             SysUserRole relation = new SysUserRole();
 *             relation.setUserId(userId);
 *             relation.setRoleId(roleId);
 *             sysUserRoleMapper.insert(relation);
 *         });
 * }</pre>
 */
@Component
public class RelationRebuildComponent {

    /**
     * 重建关系数据：用标准化后的新 ID 集合替换旧关系。
     *
     * <p>固定执行顺序：</p>
     * <p>1) 先执行 {@code clearAction} 清空旧关系；</p>
     * <p>2) 当 {@code sourceIds} 为 null 或空集合时直接返回；</p>
     * <p>3) 过滤 null 并去重，且保持原有插入顺序；</p>
     * <p>4) 按标准化后的 ID 顺序逐条执行 {@code insertAction}。</p>
     */
    public <T> void rebuild(Collection<T> sourceIds, Runnable clearAction, Consumer<T> insertAction) {
        clearAction.run();
        if (sourceIds == null || sourceIds.isEmpty()) {
            return;
        }
        Set<T> normalizedIds = new LinkedHashSet<>();
        for (T sourceId : sourceIds) {
            if (sourceId == null) {
                continue;
            }
            normalizedIds.add(sourceId);
        }
        for (T normalizedId : normalizedIds) {
            insertAction.accept(normalizedId);
        }
    }
}
