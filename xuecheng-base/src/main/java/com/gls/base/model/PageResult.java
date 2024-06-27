package com.gls.base.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageResult<T> implements Serializable {    //任意可序列化数据
    //响应数据
    private List<T> items;

    //总记录数
    private long counts;

    //当前页码
    private long pageNo;

    //每页记录数
    private long pageSize;

    public PageResult(Page<T> page){
        this.items = page.getRecords();
        this.counts = page.getTotal();
        this.pageNo = page.getCurrent();
        this.pageSize = page.getSize();
    }
}
