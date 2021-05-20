package com.mfexcel.bigdata.openapi.utils;



import java.util.HashMap;

public class AjaxResult extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    /** 状态码 */
    public static final String CODE_TAG = "code";

    /** 返回内容 */
    public static final String MSG_TAG = "message";

    /** 数据对象 */
    public static final String DATA_TAG = "result";

    /** 数据对象 */
    public static final String SUCCESS_TAG = "success";

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    public AjaxResult() {
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     */
    public AjaxResult(int code, String msg) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (code == 200){
            super.put(SUCCESS_TAG, true);
        }else{
            super.put(SUCCESS_TAG, false);
        }
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     * @param data 数据对象
     */
    public AjaxResult(int code, String msg, Object data)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (StringUtils.isNotNull(data))
        {
            super.put(DATA_TAG, data);
        }
        if (code == 200 || code == 204){
            super.put(SUCCESS_TAG, true);
        }else{
            super.put(SUCCESS_TAG, false);
        }

    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static AjaxResult success()
    {
        return AjaxResult.success("操作成功");
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static AjaxResult success(Object data)
    {
        return AjaxResult.success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static AjaxResult success(String msg)
    {
        return AjaxResult.success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static AjaxResult success(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.SUCCESS, msg, data);
    }


    /**
     *
     * @param data
     * @return 204 无数据消息
     */

    public static AjaxResult no_content(Object data)
    {
        return AjaxResult.no_content("无内容", data);
    }


    /**
     *
     * @param msg
     * @param data
     * @return
     */
    public static AjaxResult no_content(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.NO_CONTENT, msg, data);
    }



    /**
     *
     * @param data
     * @return 304 不能得到修改
     */

    public static AjaxResult NOT_MODIFIED(Object data)
    {
        return AjaxResult.NOT_MODIFIED("已有相同数据！", data);
    }


    /**
     *
     * @param msg
     * @param data
     * @return
     */
    public static AjaxResult NOT_MODIFIED(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.NOT_MODIFIED, msg, data);
    }




    /**
     *
     * @param data
     * @return 204 文件不存在
     */

    public static AjaxResult BAD_REQUEST(Object data)
    {
        return AjaxResult.BAD_REQUEST("File Not Exists", data);
    }


    /**
     *
     * @param msg
     * @param data
     * @return
     */
    public static AjaxResult BAD_REQUEST(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.BAD_REQUEST, msg, data);
    }


    /**
     * 返回错误消息
     *
     * @return
     */
    public static AjaxResult error()
    {
        return AjaxResult.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static AjaxResult error(String msg)
    {
        return AjaxResult.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static AjaxResult error(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.ERROR, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg 返回内容
     * @return 警告消息
     */
    public static AjaxResult error(int code, String msg)
    {
        return new AjaxResult(code, msg, null);
    }
}
