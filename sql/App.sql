create schema if not exists fufu_user default character set utf8mb4;
use fufu_user;
#用户表
drop table if exists user;
create table user
(
    id           varchar(64)                        not null comment '用户id(uuid)'
        primary key,
    nickname     varchar(256)                       null comment '用户昵称',
    phone        varchar(32)                        not null comment '手机号',
    addr         varchar(32)                        not null comment '当前登录地址',
    upAddr       varchar(32)                        null comment '上一次登录地址',
    avatarUrl    varchar(512)                       null comment '用户头像',
    gender       tinyint  default 0                 null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '状态 0 -- 正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '角色: 0--普通用户 1--VIP',
    tags         varchar(512)                       null comment '标签列表json',
    profile      varchar(512)                       null comment '个人简介',
    birthday     datetime                           not null comment '生日'
)
comment '用户';

#动态表
drop table if exists dynamic;
create table dynamic
(
    id           bigint auto_increment comment 'id'
        primary key,
    userId       varchar(64)                        null comment '用户id',
    content      varchar(128)                       null comment '动态内容',
    images       varchar(1024)                      null comment '图片',
    coverImages  varchar(128)                       null comment '封面图片',
    likeCount    int      default 0                 null comment '点赞数',
    commentCount int      default 0                 null comment '评论数',
    forwardCount int      default 0                 null comment '转发数',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建|发布时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
    comment '动态';

create schema if not exists fufu_socializing default character set utf8mb4;
use fufu_socializing;
#评论表
drop table if exists comments;
create table comments
(
    id              bigint auto_increment comment 'id'
        primary key,
    userId          varchar(64)                        null comment '用户id',
    dynamicId       bigint                             null comment '评论动态',
    parentCommentId bigint                             null comment '0 表示根评论',
    content         varchar(128)                       null comment '评论内容',
    createTime      datetime default CURRENT_TIMESTAMP null comment '创建|发布时间',
    updateTime      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete        tinyint  default 0                 not null comment '是否删除'
)
    comment '评论';
#动态通知表
drop table if exists dynamic_notice;
create table dynamic_notice
(
    id         int auto_increment comment 'id'
        primary key,
    dynamicId  int                                null comment '动态Id',
    userId     varchar(64)                        null comment '用户Id',
    type       varchar(10)                        null comment '通知类型',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    userId2    varchar(64)                        null comment '用户',
    content    varchar(512)                       null comment '内容'
)
    comment '通知表';
#关注表
drop table if exists focus;
create table focus
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     varchar(64)                        null comment '用户id',
    focusId    varchar(64)                        null comment '关注的id',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建|发布时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '关注';
#群聊成员表
drop table if exists group_chat_member;
create table group_chat_member
(
    id         int auto_increment comment 'Id'
        primary key,
    groupId    int                                not null comment '群聊Id',
    userId     varchar(64)                        not null comment '用户Id',
    joinTime   datetime default CURRENT_TIMESTAMP null comment '加入时间',
    role       tinyint  default 0                 null comment '角色',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '群聊成员表';
#群聊消息表
drop table if exists group_chat_message;
create table group_chat_message
(
    id          int auto_increment comment '消息ID'
        primary key,
    groupId     int                                not null comment '群聊Id',
    senderId    varchar(64)                        not null comment '发送者Id',
    content     varchar(1024)                      null comment '消息内容',
    messageType varchar(32)                        not null comment '消息类型',
    sendTime    datetime default CURRENT_TIMESTAMP null comment '发送时间',
    isRead      tinyint  default 0                 null comment '是否已读 0 为否',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
)
    comment '群聊消息表';
#群聊会话表
drop table if exists group_chat_session;
create table group_chat_session
(
    id              int auto_increment comment '群聊id'
        primary key,
    name            varchar(64)                        not null comment '群聊名称',
    description     varchar(512)                       null comment '群聊描述',
    creatorId       varchar(64)                        not null comment '创建者Id',
    lastMessageTime datetime                           null comment '最后一条消息时间',
    memberCount     int      default 0                 not null comment '群成员数',
    createTime      datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    avatarUrl       varchar(256)                       null comment '群头像'
)
    comment '群聊会话表';
#私聊消息表
drop table if exists private_chat_Message;
create table private_chat_Message
(
    id          int auto_increment comment '消息ID'
        primary key,
    sessionId   int                                not null comment '会话Id',
    senderId    varchar(64)                        not null comment '发送者Id',
    receiverId  varchar(64)                        not null comment '接收者Id',
    content     varchar(1024)                      null comment '消息内容',
    messageType varchar(32)                        not null comment '消息类型',
    sendTime    datetime default CURRENT_TIMESTAMP null comment '发送时间',
    isRead      tinyint  default 0                 null comment '是否已读 0 为否',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
)
    comment '消息表';
#私聊会话表
drop table if exists private_chat_session;
create table private_chat_session
(
    id              int auto_increment comment '私聊会话id'
        primary key,
    userId1         varchar(64)                        not null comment '用户1',
    userId2         varchar(64)                        not null comment '用户2',
    lastMessageTime datetime                           null comment '最后一条消息时间',
    createTime      datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete        tinyint  default 0                 not null comment '是否删除'
)
    comment '私聊会话表';
#系统通知表
drop table if exists system_notice;
create table system_notice
(
    id         int auto_increment comment 'id'
        primary key,
    userId     varchar(64)                        null comment '用户Id',
    content    varchar(1024)                      null comment '内容',
    type       varchar(10)                        null comment '通知类型',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    userId2    varchar(64)                        null comment '用户'
)
    comment '系统通知表';
#点赞表
drop table if exists thumbsUp;
create table thumbsUp
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     varchar(64)                        null comment '用户id',
    dynamicId  bigint                             null comment '点赞动态',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建|发布时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '点赞';

create schema if not exists fufu_organize_bureau default character set utf8mb4;
use fufu_organize_bureau;
#活动表
drop table if exists activity;
create table activity
(
    id          bigint auto_increment comment 'id'
        primary key,
    name        varchar(256)                       null comment '队伍名',
    description varchar(1024)                      null comment '描述',
    maxNum      int      default 1                 not null comment '最大人数',
    currentNum  int      default 0                 not null comment '当前人数',
    address     varchar(128)                       not null comment '活动地址',
    userId      varchar(64)                        not null comment '发起者',
    teamImage   varchar(1024)                      null comment '活动图片',
    expireTime  datetime                           null comment '过期时间',
    startTime   datetime                           not null comment '开始时间',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    deposit     double   default 0                 null comment '押金'
)
    comment '活动';
#用户活动关系表
drop table if exists user_activity;
create table user_activity
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     varchar(64)                        null comment '用户id',
    teamId     bigint                             null comment '队伍id',
    joinTime   datetime default (now())           null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '用户活动关系';

create schema if not exists `fufu_app` default character set utf8mb4;
use `fufu_app`;

DROP TABLE IF EXISTS `luaScript`;
CREATE TABLE `luaScript` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `scriptName` varchar(64) NOT NULL,
    `scriptContent` text NOT NULL,
    `sha1Checksum` varchar(40) NOT NULL,
    `createdTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `updateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `scriptName` (`scriptName`),
    UNIQUE KEY `sha1Checksum` (`sha1Checksum`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='lua脚本';
LOCK TABLES `luaScript` WRITE;
INSERT INTO `luaScript` VALUES (1,'SocializingIsFocusAndFansLuaScript','local user_id = KEYS[1]\nlocal focus_user_id = KEYS[2]\n\nlocal user_keys = {}\nfor i = 1, 4 do\n    table.insert(user_keys, \"fufu:socializing:focus:zset:\" .. user_id .. \":\" .. i)\nend\n\nlocal focus_keys = {}\nfor i = 1, 4 do\n    table.insert(focus_keys, \"fufu:socializing:focus:zset:\" .. focus_user_id .. \":\" .. i)\nend\n\nlocal user_contains_focus = false\nfor _, key in ipairs(user_keys) do\n    if redis.call(\"ZSCORE\", key, focus_user_id) then\n        user_contains_focus = true\n        break\n    end\nend\n\nif not user_contains_focus then\n    return 0\nend\n\nlocal focus_contains_user = false\nfor _, key in ipairs(focus_keys) do\n    if redis.call(\"ZSCORE\", key, user_id) then\n        focus_contains_user = true\n        break\n    end\nend\n\nif not focus_contains_user then\n    return 0\nend\nreturn 1','79cb1814f86350da8e2f5779841b2bc582bbdb22','2025-02-20 02:27:35','2025-02-21 01:39:23'),(2,'SocializingGetFocusListLuaScript','local user_id = KEYS[1]\nlocal pageSize = tonumber(ARGV[1])\nlocal pageNumber = tonumber(ARGV[2])\nlocal skip = (pageNumber - 1) * pageSize\n\nlocal user_keys = {}\nfor i = 1, 4 do\n    table.insert(user_keys, \"fufu:socializing:focus:zset:\" .. user_id .. \":\" .. i)\nend\n\nlocal pagedResult = {}\nlocal totalCollected = 0\n\nfor _, key in ipairs(user_keys) do\n    local count = redis.call(\"ZCARD\", key)\n    if count <= skip then\n        skip = skip - count\n    else\n        local start_index = skip\n        local stop_index = skip + pageSize - 1 - totalCollected\n        local members = redis.call(\"ZRANGE\", key, start_index, stop_index)\n        for _, member in ipairs(members) do\n            table.insert(pagedResult, member)\n            totalCollected = totalCollected + 1\n            if totalCollected >= pageSize then\n                return pagedResult\n            end\n        end\n        skip = 0\n    end\nend\n\nreturn pagedResult','0aa9c11a8993ef733540e0c596108e6d05b1939f','2025-02-20 08:42:00','2025-02-24 02:59:02'),(3,'SocializingIsFocusLuaScript','local user_id = KEYS[1]\nlocal focus_user_id = KEYS[2]\n\nlocal user_keys = {}\nfor i = 1, 4 do\n    table.insert(user_keys, \"fufu:socializing:focus:zset:\" .. user_id .. \":\" .. i)\nend\n\nlocal user_contains_focus = false\nfor _, key in ipairs(user_keys) do\n    if redis.call(\"ZSCORE\", key, focus_user_id) then\n        user_contains_focus = true\n        break\n    end\nend\n\nif not user_contains_focus then\n    return 0\nend\n\nreturn 1','c55f7a354ea8b1bcf5f7056900dec074c3cfd799','2025-02-21 01:34:19','2025-02-21 01:39:23');
UNLOCK TABLES;
