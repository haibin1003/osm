-- OSM 开源软件治理平台 - 初始化数据
-- 执行方式: mysql -h 114.66.38.81 -P 3036 -u root -p osm < 02_data.sql

USE osm;

-- 初始化业务域数据
INSERT INTO osm_domain (name, description, created_by, updated_by) VALUES
('金融域', '金融业务相关系统', 'system', 'system'),
('电商域', '电商业务相关系统', 'system', 'system'),
('供应链域', '供应链业务相关系统', 'system', 'system'),
('基础架构域', '基础设施和中间件系统', 'system', 'system');

-- 初始化业务系统数据
INSERT INTO osm_system (name, domain_id, description, owners, created_by, updated_by) VALUES
('支付系统', 1, '核心支付交易系统', 'zhangsan,lisi', 'system', 'system'),
('清算系统', 1, '资金清算结算系统', 'wangwu', 'system', 'system'),
('订单系统', 2, '电商订单管理系统', 'zhaoliu', 'system', 'system'),
('商品系统', 2, '商品管理系统', 'zhaoliu', 'system', 'system'),
('仓储系统', 3, '仓储物流管理系统', 'qianqi', 'system', 'system'),
('消息中心', 4, '统一消息推送服务', 'admin', 'system', 'system');

-- 初始化开源软件数据
INSERT INTO osm_software (name, tech_category, license_type, description, doc_url, status, created_by, updated_by) VALUES
('Redis', '缓存', 'BSD-3-Clause', '高性能键值存储数据库', 'https://redis.io/documentation', 1, 'system', 'system'),
('MySQL', '数据库', 'GPL-2.0', '关系型数据库管理系统', 'https://dev.mysql.com/doc/', 1, 'system', 'system'),
('Kafka', '消息队列', 'Apache-2.0', '分布式流处理平台', 'https://kafka.apache.org/documentation/', 1, 'system', 'system'),
('Elasticsearch', '搜索引擎', 'Apache-2.0', '分布式搜索和分析引擎', 'https://www.elastic.co/guide/', 1, 'system', 'system'),
('Spring Boot', 'Web框架', 'Apache-2.0', 'Java微服务框架', 'https://spring.io/projects/spring-boot', 1, 'system', 'system'),
('Nginx', '负载均衡', 'BSD-2-Clause', '高性能Web服务器和反向代理', 'https://nginx.org/en/docs/', 1, 'system', 'system');

-- 初始化软件版本数据
INSERT INTO osm_software_version (software_id, version, description, release_date, status, created_by, updated_by) VALUES
(1, '6.2.7', 'Redis 6.2 稳定版', '2021-04-27', 0, 'system', 'system'),
(1, '7.0.12', 'Redis 7.0 最新版', '2023-04-18', 0, 'system', 'system'),
(2, '5.7.42', 'MySQL 5.7 LTS版', '2023-04-18', 0, 'system', 'system'),
(2, '8.0.33', 'MySQL 8.0 最新版', '2023-04-18', 0, 'system', 'system'),
(3, '2.8.2', 'Kafka 2.8 稳定版', '2021-04-19', 0, 'system', 'system'),
(3, '3.4.0', 'Kafka 3.4 最新版', '2023-02-07', 0, 'system', 'system');
