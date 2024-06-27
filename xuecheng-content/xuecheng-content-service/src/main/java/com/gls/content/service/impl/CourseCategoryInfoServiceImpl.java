package com.gls.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.gls.content.mapper.CourseCategoryMapper;
import com.gls.content.model.po.CourseCategory;
import com.gls.content.model.vo.CourseCategoryInfoVO;
import com.gls.content.service.CourseCategoryInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * @author 郭林赛
 * @version 1.0
 * @description
 * @createDate 2024/6/27 11:45
 **/

@Service
@RequiredArgsConstructor
public class CourseCategoryInfoServiceImpl implements CourseCategoryInfoService {
    private final CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryInfoVO> queryTreeNodes() {
        //查询数据
        List<CourseCategory> courseCategories = courseCategoryMapper.selectList(null);

        //处理时间复杂度O(n)，n为上述list的当前大小
        Map<String, CourseCategoryInfoVO> map = new HashMap<>();
        CourseCategoryInfoVO rootParent = new CourseCategoryInfoVO();   //根节点的父亲“0”
        map.put("0",rootParent);

        //对每个结点建立映射
        for (CourseCategory courseCategory : courseCategories) {
            CourseCategoryInfoVO cvo = new CourseCategoryInfoVO();
            BeanUtil.copyProperties(courseCategory,cvo);
            map.put(courseCategory.getId(),cvo);
        }

        //构建树
        for (CourseCategory courseCategory : courseCategories) {
            //将courseCategory放到父结点的list中
            CourseCategoryInfoVO courseCategoryInfoVO = map.get(courseCategory.getParentid());  //父结点
            List<CourseCategoryInfoVO> childrenTreeNodes = courseCategoryInfoVO.getChildrenTreeNodes();     //父节点的孩子list
            if(childrenTreeNodes==null)
                childrenTreeNodes = new ArrayList<>();
            CourseCategoryInfoVO child = map.get(courseCategory.getId());   //孩子的vo
            childrenTreeNodes.add(child);
            courseCategoryInfoVO.setChildrenTreeNodes(childrenTreeNodes);
        }

        //孩子排序
        for (CourseCategoryInfoVO categoryInfoVO : map.values()) {
            List<CourseCategoryInfoVO> children = categoryInfoVO.getChildrenTreeNodes();
            if(CollectionUtil.isNotEmpty(children))
                children.sort(Comparator.comparingInt(CourseCategory::getOrderby));
        }

        //第一层排序
        List<CourseCategoryInfoVO> resultVO = map.get("1").getChildrenTreeNodes();
        resultVO.sort(Comparator.comparing(CourseCategory::getOrderby));
        return resultVO;
    }
}
