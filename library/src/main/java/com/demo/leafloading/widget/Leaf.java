package com.demo.leafloading.widget;

/**
 * Author: ZhengHuaizhi
 * Date: 2019/12/17
 * Description: 树叶对象
 */
class Leaf {
    int id;
    // 相对于canvas原点坐标
    float left;
    float top;
    // 旋转的角度
    float degree;
    boolean isShow;

    Leaf(int id, float left, float top, float degree) {
        this.id = id;
        this.left = left;
        this.top = top;
        this.degree = degree;
        this.isShow = true;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getDegree() {
        return degree;
    }

    public void setDegree(float degree) {
        this.degree = degree;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
