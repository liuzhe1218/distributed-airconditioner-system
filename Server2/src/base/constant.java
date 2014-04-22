package base;

public final class constant {
	//三种风每分钟升或降温的度数
	public static double MIDDLE_SPEED = 1.6;
    public static double HIGH_SPEED = 2;
    public static double LOW_SPEED = 1.2;
    // 三种风每分钟消耗的功率
    public static double LOW_COST = 0.8; 
    public static double MIDDLE_COST = 1;
    public static double HIGH_COST = 1.3;
    //每功率单价5元
    public static int PER_COST = 5;
    
    public static int FREQUENCY = 15; //刷新频率(s)
    public static int WAIT_FREQ = 1; //等待线程sleep的时间以分钟为单位
    
    //socket服务器端口
    public static int PORT = 9234;
}
