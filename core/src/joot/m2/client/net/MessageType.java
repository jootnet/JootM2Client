package joot.m2.client.net;

/**
 * 网络消息类型
 */
public enum MessageType {
    /**
     * 更新人物动作
     */
    HUM_ACTION_CHANGE(1001),
    /**
     * 登陆
     * <br>
     * 客户端发送用户名密码到服务端
     */
	LOGIN_REQ(1002),
	/**
	 * 登陆结果
	 */
	LOGIN_RESP(1003);

    private int id_;
    private MessageType(int id) {
        this.id_ = id;
    }
    /**
     * 获取动作的数值编号表示
     * 
     * @return 当前消息类型的数值编号表示
     */
    public int id() {
        return this.id_;
    }
}