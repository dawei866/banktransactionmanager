# Kubernetes部署指南

本指南介绍了如何在Kubernetes集群中部署银行交易管理系统。

## 目录结构

```
k8s/
├── deployment.yaml      # Deployment和Service配置
├── hpa.yaml            # Horizontal Pod Autoscaler配置
├── configmap.yaml      # 应用配置
├── secret.yaml         # 敏感信息配置
├── ingress.yaml        # Ingress配置
├── namespace.yaml      # 命名空间配置
└── kustomization.yaml  # Kustomize配置
```

## 部署步骤

### 1. 创建命名空间
```bash
kubectl apply -f k8s/namespace.yaml
```

### 2. 创建ConfigMap和Secret
```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
```

### 3. 部署应用
```bash
kubectl apply -f k8s/deployment.yaml
```

### 4. 配置自动扩缩容（可选）
```bash
kubectl apply -f k8s/hpa.yaml
```

### 5. 配置Ingress（可选）
```bash
kubectl apply -f k8s/ingress.yaml
```

或者使用Kustomize一键部署：
```bash
kubectl apply -k k8s/
```

## 访问应用

### 通过Service访问
```bash
kubectl get svc -n bank-transaction-manager
```

### 通过Ingress访问
如果配置了Ingress，可以通过以下URL访问：
```
http://bank-transaction-manager.local
```

## 监控和管理

### 查看Pod状态
```bash
kubectl get pods -n bank-transaction-manager
```

### 查看日志
```bash
kubectl logs -n bank-transaction-manager -l app=bank-transaction-manager
```

### 扩缩容
```bash
kubectl scale deployment bank-transaction-manager -n bank-transaction-manager --replicas=5
```

## 配置说明

### 环境变量
应用通过环境变量获取配置信息：
- `SPRING_PROFILES_ACTIVE`: 激活的Spring Profile
- `SPRING_DATASOURCE_URL`: 数据库连接URL
- `SPRING_DATASOURCE_USERNAME`: 数据库用户名
- `SPRING_DATASOURCE_PASSWORD`: 数据库密码

### 资源限制
每个Pod的资源限制：
- CPU请求: 250m
- CPU限制: 500m
- 内存请求: 256Mi
- 内存限制: 512Mi

### 健康检查
应用配置了存活探针和就绪探针：
- 存活探针: `/actuator/health`
- 就绪探针: `/actuator/health`

## 故障排除

### 查看事件
```bash
kubectl get events -n bank-transaction-manager
```

### 进入Pod调试
```bash
kubectl exec -it -n bank-transaction-manager <pod-name> -- /bin/sh
```