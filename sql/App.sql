#fufu-user
create schema if not exists fufu_user charset utf8mb4; #用户服务
#用户表
create table user
(
    id           varchar(64) not null comment 'id'  primary key comment '用户id(uuid)',
    nickname     varchar(256)                       null comment '用户昵称',
    phone        varchar(32)                       not null comment '手机号',
    avatarUrl    varchar(512)                      null comment '用户头像',
    gender       tinyint  default 0                 null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '状态 0 -- 正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '角色: 0--普通用户 1--VIP',
    tags         varchar(512)                       null comment '标签列表json',
    profile      varchar(512)                       null comment '个人简介'
)comment '用户' charset utf8mb4;