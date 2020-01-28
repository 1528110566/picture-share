本项目是一时兴起所做，前后端没有完全分离，暂时未发现BUG。

本项目使用SpringBoot，结合Redis和FastDFS，实现了图片的上传、删除、查看、点赞、评论等功能，是一个图片分享交流平台。

结合Redis时，使用的全部都是StringRedisTemplate进行交互，避免了序列化问题，这样可以直接在终端看到Redis存储的信息，只是在转换时比较麻烦。

本项目可具有在线人数实时显示和首页评论实时显示的功能，登录时首页只显示**部分**评论。

_使用须知_：

1.注意**application.properties**中*redis*和*fdfs_tracker*的配置，以及**图片详情页**的跳转链接、**upload-fail.html**、**upload-ok.html**的跳转连接，实际使用前需要更换；

2.运行前需要开启虚拟机或者服务器的redis和fdfs服务；

3.运行期那需要7张数据表：

    3.1 comment:存储评论
    3.2 like_record:存储点赞记录
    3.3 read_record:存储阅读记录
    3.4 pic:存储图片信息
    3.5 top_pic:存储置顶图片
    3.6 user:用户表，0为普通用户、1为领导、2为管理员
    3.7 wish:首页的新年祝福（项目在新年前完成，所以加上了新年祝福功能）
4.管理员具有刷新缓存功能，在修改了top_pic表后，可以通过刷新缓存来刷新首页置顶图片，但是这个操作会**清空**Redis的所有数据，虽然在清除之前会保存例如阅读记录、点赞记录、评论、新年祝福等信息至数据库，不过不建议在高并发的情况下使用，可能会造成部分数据丢失。

本项目时间匆忙，能力有限，如果有建议，请联系QQ:1528110566，添加时请备注：GitHub图片分享交流平台
