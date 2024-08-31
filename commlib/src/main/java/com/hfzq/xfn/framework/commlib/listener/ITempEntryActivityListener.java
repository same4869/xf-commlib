package com.hfzq.xfn.framework.commlib.listener;

/**
 * 标记是否是入口暂时性 Activity
 * 入口 Activity 只是为了承接外部的跳转，跳转后，会 finish 掉自身，这样一些 弹窗和跳转逻辑无法直接进行
 * 因此需要一个标记，判断是否是暂时性的 Activity，自己会 finish 的，用于内部逻辑判断
 */
public interface ITempEntryActivityListener {

    boolean isTempEntryActivity();
}
