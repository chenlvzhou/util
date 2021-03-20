package com.util.comutil.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * @author by chenlvzhou
 * @Classname Result
 * @Description TODO
 * @Date 2021/3/20 21:53
 */
public class Result implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int SUCCESS_CODE = 200;
    public int code;
    public String msg;
    public Pager pager;

    public Result() {
    }

    public static Result create() {
        Result ret = new Result();
        ret.code = 200;
        ret.msg = "";
        return ret;
    }

    public static Result create(int code, String msg) {
        Result ret = new Result();
        ret.code = code;
        ret.msg = msg;
        return ret;
    }

    public Result setCodeAndMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }

    public Result setCodeAndMsg(Result result) {
        this.code = result.code;
        this.msg = result.msg;
        return this;
    }

    public void setPager(long total, int pageSize, int pageNo) {
        if (this.pager == null) {
            this.pager = new Pager();
        }

        this.pager.total = total < 0L ? 0L : total;
        this.pager.pageSize = pageSize < 1 ? 1 : pageSize;
        this.pager.pageNo = pageNo < 1 ? 1 : pageNo;
    }

    @JsonIgnore
    public boolean isSucceed() {
        return this.code == 200;
    }
}
