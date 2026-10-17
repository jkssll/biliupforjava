# 飞牛 NAS（fnOS）Docker 部署说明

本目录用于构建和运行带“全局上传限速”功能的 biliupforjava 镜像。

## 方式一：GitHub Actions 构建镜像 tar（推荐，本机无需 Docker）

1. 把代码推送到 GitHub 仓库（默认分支 `web_local`）。
2. 打开仓库的 **Actions** 页面，选择 **Build Docker Image For NAS**。
3. 推送代码后会自动触发一次；也可以点击 **Run workflow** 手动触发。
4. 构建完成后，在该次运行页面底部下载产物 **biliupforjava-docker-image**。
5. 解压得到 `biliupforjava-latest.tar`。
6. 飞牛 NAS → Docker → 镜像 → 导入镜像，选择该 tar。
7. 用下面的 `docker run` 或 Compose 创建容器。

> 工作流产物为 `linux/amd64` 架构，适用于绝大多数飞牛 NAS（N100/N5105 等）。
> 如果你的设备是 ARM 架构，需要额外构建 arm64 镜像。

## 方式二：在飞牛 NAS 上直接构建

前提：NAS 已开启 SSH，并且已安装 Docker。

```sh
# 把本目录（Dockerfile、docker-compose.yml、ROOT.war）上传到 NAS
cd /path/to/Docker
docker build -t biliupforjava:latest .
docker compose up -d
```

如果目录里没有 `ROOT.war`，请在开发机先执行：

```sh
mvn -DskipTests package
cp target/*.war Docker/ROOT.war
```

## 方式三：使用已导入的镜像 + docker run

```sh
docker run -d \
  --name bup \
  --restart unless-stopped \
  -p 12380:80 \
  -v /vol1/1000/bilirecord:/bilirecord \
  -e TZ=Asia/Shanghai \
  -e JAVA_OPTS='-Drecord.userName=admin -Drecord.password=改成你的密码' \
  -m 512m \
  biliupforjava:latest
```

- `12380` 是宿主机端口，可改成未被占用的端口；容器内固定为 80。
- `/vol1/1000/bilirecord` 换成你的 NAS 实际目录；该目录保存 H2 数据库、日志和录制文件，请务必备份。
- 不设置 `JAVA_OPTS` 中的用户名密码时，网页不启用登录验证，建议设置。
- 内存建议至少 512M；使用高能剪辑/ffmpeg 时建议 1G 以上。

## 访问

浏览器打开：`http://飞牛NAS的IP:12380`

## 录播姬 / blrec Webhook

```text
http://飞牛NAS的IP:12380/recordWebHook
```

如果录播姬运行在另一个容器中：

- 不要填 `localhost` 或容器 IP，填 NAS 的局域网 IP + 映射端口；
- 或者把两个容器加入同一个 Docker 网络，使用容器名访问。

## 全局上传限速

打开网页 →「bilibili用户」→ 点击“全局上传限速”：

- 输入 `1024` 表示 1 MB/s；
- `0` 表示不限速；
- 所有直播间、所有上传线路共用该带宽；
- 保存后立即生效，并持久化到 `/bilirecord` 下的数据库。

## 常见问题

- 拉取基础镜像慢：在飞牛 Docker 设置里配置镜像加速，或改用可访问的镜像源。
- 端口冲突：修改 `docker run -p` 或 Compose 里的 `HOST_PORT`。
- 数据迁移：备份整个 `/bilirecord` 映射目录即可。
- 首次构建较慢：需要拉取 JDK 基础镜像并通过 apt 安装 ffmpeg。
