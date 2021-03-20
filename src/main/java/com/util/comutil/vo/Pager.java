package com.util.comutil.vo;

import java.io.Serializable;

import javax.validation.constraints.Min;

/**
 * @author by chenlvzhou
 * @Classname Pager
 * @Description TODO
 * @Date 2021/3/20 21:54
 */
public class Pager implements Serializable {
    private static final long serialVersionUID = 1L;
    @Min(0L)
    public long total;
    @Min(1L)
    public int pageSize;
    @Min(0L)
    public int pageNo;

    public Pager() {
    }

    public Pager(int pageSize, int pageNo) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }
}
