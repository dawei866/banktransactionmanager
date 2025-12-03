package com.example.banktransactionmanager.util;

/**
 * SnowFlake算法生成分布式唯一ID
 * 64位ID结构：
 * 1位符号位(不用) + 41位时间戳 + 5位数据中心ID + 5位机器ID + 12位序列号
 */
public class SnowflakeIdGenerator {
    
    // 起始时间戳 (2023-01-01)
    private final static long START_TIMESTAMP = 1672502400000L;
    
    // 各部分位数
    private final static long SEQUENCE_BIT = 12;
    private final static long MACHINE_BIT = 5;
    private final static long DATA_CENTER_BIT = 5;
    
    // 最大值计算
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private final static long MAX_DATA_CENTER_NUM = ~(-1L << DATA_CENTER_BIT);
    
    // 位移量
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;
    
    private long dataCenterId;  // 数据中心ID
    private long machineId;     // 机器ID
    private long sequence = 0L; // 序列号
    private long lastTimestamp = -1L; // 上一次时间戳
    
    /**
     * 构造函数
     * @param dataCenterId 数据中心ID
     * @param machineId 机器ID
     */
    public SnowflakeIdGenerator(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("数据中心ID不能大于" + MAX_DATA_CENTER_NUM + "或小于0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("机器ID不能大于" + MAX_MACHINE_NUM + "或小于0");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }
    
    /**
     * 获取下一个ID
     * @return 唯一ID
     */
    public synchronized long nextId() {
        long currTimestamp = getNewTimestamp();
        
        // 如果当前时间小于上一次时间戳，说明系统时钟回退，抛出异常
        if (currTimestamp < lastTimestamp) {
            throw new RuntimeException("时钟向后移动，拒绝生成ID");
        }
        
        // 如果是同一时间戳，序列号自增
        if (currTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 如果序列号已经达到最大值，阻塞到下一毫秒
            if (sequence == 0L) {
                currTimestamp = getNextMill();
            }
        } else {
            // 时间戳改变，序列号重置
            sequence = 0L;
        }
        
        lastTimestamp = currTimestamp;
        
        // 生成ID
        return (currTimestamp - START_TIMESTAMP) << TIMESTAMP_LEFT
                | dataCenterId << DATA_CENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }
    
    /**
     * 阻塞到下一毫秒
     * @return 下一毫秒时间戳
     */
    private long getNextMill() {
        long mill = getNewTimestamp();
        while (mill <= lastTimestamp) {
            mill = getNewTimestamp();
        }
        return mill;
    }
    
    /**
     * 获取当前时间戳
     * @return 当前时间戳
     */
    private long getNewTimestamp() {
        return System.currentTimeMillis();
    }
}