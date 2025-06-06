# TSN：定制化社交网络



## 一、项目概述

VibeLink 是一个模拟的社交平台，用户可以在平台上分享与自己兴趣爱好相关的内容，如阅读、游戏、烹饪、徒步旅行和摄影等。平台支持用户之间的关注、点赞等社交互动。本项目使用图数据库 Neo4j 来存储和管理数据，通过 Cypher 语句进行数据的创建、查询和关系建立。



## 二、项目目录结构及介绍

该项目的目录结构简洁明了，下面是主要部分的简介：

```plaintext
social-recommend/
├── src                      		 # 项目源代码所在目录
│   └── java                  		 # 包含应用程序的主要Java代码
│   └── resources                	 # 配置资源
│    	├── static			 # 静态资源（如CSS、图片）
│	├── templates			 # 前端页面（如HTML
│    	└── application.properties 	 # 关键配置文件
├── .gitignore                           # Git忽略文件配置
├── LICENSE                              # 许可证文件，遵循MIT协议
├── README.md                            # 项目说明文档
└── pom.xml                              # Maven 构建配置文件
```



- src/：包含系统的核心业务逻辑与控制器类。
- resources/：配置和模板资源，特别是 application.properties 用于系统运行时参数配置。
- pom.xml 是Maven构建项目的配置文件，定义了依赖项、编译设置等。
- LICENSE 项目许可证（MIT）
- README.md 提供系统概览、运行步骤和配置说明。



## 三、项目配置文件

**关键配置位于resources/application.properties文件中。它包括但不限于：**

```properties
# Neo4j数据库连接配置
spring.neo4j.uri = bolt://localhost:7687
spring.neo4j.authentication.username = neo4j
spring.neo4j.authentication.password = admin2025

# 文件上传路径
file.upload-dir = upload/
file.staticAccessPath = /upload/**
```

⚠️ 请根据实际环境修改以上配置，确保数据库连接与文件路径正确。


## 四、使用说明

### 1. 环境要求

- 安装 Neo4j 图数据库
- 安装 Java 17 或以上版本
- 安装 Maven 3.6 或以上版本
- 安装 Spring Boot 2.7 或以上版本
- 推荐使用 IDE（如 IntelliJ IDEA）进行开发与调试

### 2. 操作步骤

1. 启动本地 Neo4j 数据库，确认连接信息无误。
2. 运行 Spring Boot 项目（可通过 IDE 或 mvn spring-boot:run 启动）。




#  五、注意事项

- ⚠️ 务必先启动 Neo4j 数据库，再启动后端服务，否则将因连接失败而抛出异常。
- 如出现权限、路径或数据库连接问题，请先检查 application.properties 是否正确配置。