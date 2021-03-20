package com.util.comutil.config.json;

public interface JsonVo {

    /**
     * 基础
     *
     */
    public interface Base {
    }

    /**
     * 字段前端端显示
     *
     */
    public interface FontEnd extends Base {
    }

    /**
     * 字段前端端显示
     *
     */
    public interface FontEndOwn extends FontEnd {
    }

    /**
     * 字段后端显示
     *
     */
    public interface BackEnd extends FontEndOwn {
    }

    /**
     * 字段后端显示
     *
     */
    public interface BackEndOwn extends BackEnd {
    }

    /**
     * 管理员
     *
     */
    public interface Admin extends BackEndOwn {
    }

    /**
     * 超级管理员
     *
     */
    public interface Super extends Admin {
    }

    /**
     * 不展示
     *
     */
    public interface None {

    }

}
