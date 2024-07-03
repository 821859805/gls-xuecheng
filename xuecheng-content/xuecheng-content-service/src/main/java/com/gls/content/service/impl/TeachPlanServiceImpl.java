package com.gls.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gls.base.exception.XueChengException;
import com.gls.content.mapper.TeachplanMapper;
import com.gls.content.mapper.TeachplanMediaMapper;
import com.gls.content.model.dto.TeachPlanDto;
import com.gls.content.model.po.Teachplan;
import com.gls.content.model.po.TeachplanMedia;
import com.gls.content.model.vo.TeachPlanVo;
import com.gls.content.service.TeachPlanService;
import javafx.scene.control.Tab;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.pqc.jcajce.provider.LMS;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 课程计划接口实现
 * @createDate 2024/6/30 15:41
 **/

@Service
@RequiredArgsConstructor
public class TeachPlanServiceImpl implements TeachPlanService {
    private final TeachplanMapper teachplanMapper;
    private final TeachplanMediaMapper teachplanMediaMapper;

    @Override
    @Transactional
    public void insert(TeachPlanDto dto) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid,dto.getParentid())
                .eq(Teachplan::getCourseId,dto.getCourseId());
        Integer count = teachplanMapper.selectCount(queryWrapper);

        Teachplan teachplan = new Teachplan();
        BeanUtil.copyProperties(dto,teachplan);
        teachplan.setCreateDate(LocalDateTime.now());
        if(count==0)
            teachplan.setOrderby(1);
        else
            teachplan.setOrderby(teachplanMapper.selectLastOrder(teachplan)+1);
        teachplanMapper.insert(teachplan);
    }


    private void update(TeachPlanDto dto) {
        Teachplan teachplan = new Teachplan();
        BeanUtil.copyProperties(dto,teachplan);
        teachplan.setChangeDate(LocalDateTime.now());
        teachplanMapper.updateById(teachplan);
    }

    @Override
    @Transactional
    public List<TeachPlanVo> selectListByCourseId(Long courseId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId);
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        Map<Long,TeachPlanVo> map = new HashMap<>();
        map.put(0L,new TeachPlanVo());
        teachplans.forEach(teachplan -> {
            TeachPlanVo vo = new TeachPlanVo();
            BeanUtil.copyProperties(teachplan,vo);
            map.put(vo.getId(),vo);
        });
        teachplans.forEach(teachplan -> {
            //获得父级
            TeachPlanVo parent = map.get(teachplan.getParentid());
            //获得父级的孩子
            List<TeachPlanVo> teachPlanTreeNodes = parent.getTeachPlanTreeNodes();
            if(teachPlanTreeNodes==null)
                teachPlanTreeNodes = new ArrayList<>();
            TeachPlanVo child = map.get(teachplan.getId());
            teachPlanTreeNodes.add(child);
            parent.setTeachPlanTreeNodes(teachPlanTreeNodes);
        });
        TeachPlanVo root = map.get(0L);

        //用BFS遍历刚才建立好的树，并按层次
        Deque<TeachPlanVo> queue = new ArrayDeque<>();
        queue.offer(root);
        while(!queue.isEmpty()){
            int size = queue.size();
            while((size--)>0){
                List<TeachPlanVo> teachPlanTreeNodes = queue.poll().getTeachPlanTreeNodes();
                if(teachPlanTreeNodes!=null){
                    teachPlanTreeNodes.sort(Comparator.comparingInt(Teachplan::getOrderby));
                    teachPlanTreeNodes.forEach(queue::offer);
                }
            }
        }
        return root.getTeachPlanTreeNodes();
    }

    @Override
    @Transactional
    public void updateByCourseIdAndParentId(TeachPlanDto dto) {
        Teachplan teachplan = teachplanMapper.selectById(dto.getId());
        if(teachplan==null)
            XueChengException.cast("课程计划不存在！！！");
        update(dto);
    }

    @Transactional
    public void deleteById(Long id){
        Teachplan teachplan = teachplanMapper.selectById(id);
        if(teachplan==null)
            XueChengException.cast("课程计划不存在！");
        if(teachplan.getParentid()==0){ //删除的是大章节
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid,id);    //查询父结点id为当前要删除的id的子章节数目
            Integer count = teachplanMapper.selectCount(queryWrapper);
            if(count>0)
                XueChengException.cast("当前大章节有"+count+"个小章节，不可以删除！！！");
            teachplanMapper.deleteById(id);
        }else{  //删除的是小章节
            LambdaQueryWrapper<TeachplanMedia> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(TeachplanMedia::getTeachplanId,id);
            teachplanMediaMapper.delete(deleteWrapper); //删除教学计划id为id的课程媒体资源
            teachplanMapper.deleteById(id); //删除课程计划
        }

    }

    @Override
    @Transactional
    public void moveUpById(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        if(teachplan==null)
            XueChengException.cast("课程计划不存在！");
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,teachplan.getCourseId())
                .eq(Teachplan::getParentid,teachplan.getParentid())
                .orderByAsc(Teachplan::getOrderby);
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        int i;
        for(i=0;i<teachplans.size();++i)
            if(teachplans.get(i).getId().equals(id))
                break;
        if(i==0)
            XueChengException.cast("已经是第一个了，不能继续上移");
        //与前者交换orderby值
        Teachplan preTeachPlan = teachplans.get(i - 1);
        Integer tmp = teachplan.getOrderby();
        teachplan.setOrderby(preTeachPlan.getOrderby());
        preTeachPlan.setOrderby(tmp);
        teachplanMapper.updateById(preTeachPlan);
        teachplanMapper.updateById(teachplan);
    }

    @Override
    @Transactional
    public void moveDownById(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        if(teachplan==null)
            XueChengException.cast("课程计划不存在！");
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,teachplan.getCourseId())
                .eq(Teachplan::getParentid,teachplan.getParentid())
                .orderByAsc(Teachplan::getOrderby);
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        int i;
        for(i=0;i<teachplans.size();++i)
            if(teachplans.get(i).getId().equals(id))
                break;
        if(i==teachplans.size()-1)
            XueChengException.cast("已经是最后一个了，不能继续向下移动");
        //与后者交换orderby值
        Teachplan preTeachPlan = teachplans.get(i + 1);
        Integer tmp = teachplan.getOrderby();
        teachplan.setOrderby(preTeachPlan.getOrderby());
        preTeachPlan.setOrderby(tmp);
        teachplanMapper.updateById(preTeachPlan);
        teachplanMapper.updateById(teachplan);
    }
}









