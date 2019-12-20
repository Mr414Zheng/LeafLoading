package com.demo.leafloading.widget;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: ZhengHuaizhi
 * Date: 2019/12/17
 * Description: 绘制的树叶的储存库
 */

public class LeafFactory {
    private static LeafFactory mInstance = new LeafFactory();
    // 享元池
    private ConcurrentHashMap<Integer, Leaf> leafHashMap = new ConcurrentHashMap<>();

    static LeafFactory getInstance() {
        return mInstance;
    }

    ArrayList<Leaf> getLeaves() {
        ArrayList<Leaf> list = new ArrayList<>();
        int index = 0;

        Set set = leafHashMap.keySet();
        for (Object o : set) {
            list.add(index, getLeaf((Integer) o, 0, 0));
            index++;
        }
        return list;
    }

    Leaf getLeaf(int key, float defLeft, float defTop) {
        if (leafHashMap.containsKey(key)) {
            return leafHashMap.get(key);
        }
        return new Leaf(key, defLeft, defTop, 0f);
    }

    void putLeaf(int key, Leaf leaf) {
        leafHashMap.put(key, leaf);
    }

    void removeLeft(int key) {
        leafHashMap.remove(key);
    }
}
