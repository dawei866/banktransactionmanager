# 性能测试指南

本文档介绍了如何运行Bank Transaction Manager项目的性能测试。

## 目录结构

```
src/test/performance/
├── jmeter/                 # JMeter测试脚本
│   └── transaction-service.jmx  # JMeter测试计划
└── README.md               # 本文件
```


## JMeter性能测试

JMeter测试脚本提供了图形化的性能测试界面，可以模拟大量用户并发访问系统。

### 运行JMeter测试

1. 下载并安装[JMeter](https://jmeter.apache.org/download_jmeter.cgi)
2. 启动Bank Transaction Manager应用:
   ```bash
   mvn spring-boot:run
   ```
3. 打开JMeter GUI:
   ```bash
   apache-jmeter-x.x/bin/jmeter
   ```
4. 在JMeter中打开测试计划文件: `src/test/performance/jmeter/transaction-service.jmx`
5. 根据需要调整线程数、循环次数等参数
6. 点击"Start"按钮运行测试
7. 查看结果报表分析性能指标

### JMeter测试配置

默认测试配置:
- 线程数(用户数): 50
- Ramp-Up时间: 30秒
- 循环次数: 10次
- 测试接口:
  1. POST /api/transactions - 创建交易
  2. GET /api/transactions - 获取交易列表

## 性能测试指标

性能测试主要关注以下指标:

1. **响应时间** - 请求处理所需的时间
2. **吞吐量** - 单位时间内处理的请求数
3. **并发用户数** - 同时访问系统的用户数量
4. **错误率** - 请求失败的比例
5. **资源利用率** - CPU、内存、数据库连接等资源使用情况

## 性能优化建议

1. **数据库优化**:
   - 添加适当的索引
   - 优化查询语句
   - 考虑分表分库策略

2. **缓存策略**:
   - 使用Redis等分布式缓存
   - 合理设置缓存过期时间

3. **异步处理**:
   - 对于非实时性要求不高的操作，采用异步处理
   - 使用消息队列解耦服务

4. **负载均衡**:
   - 部署多个应用实例
   - 使用Nginx等负载均衡器分发请求

5. **JVM调优**:
   - 根据实际需求调整堆内存大小
   - 选择合适的垃圾回收器
