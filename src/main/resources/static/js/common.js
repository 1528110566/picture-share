function login() {
    $.ajax({
        url: "/login",
        // sync: ,
        type: "POST",
        data: $('#loginForm').serialize(),
        success: function (message) {
            if (message.status === "fail") {
                alert("用户名密码错误");
            } else if (message.status === "ok") {
                // setHotPic();
                document.location.reload();
            }
        }, error: function () {
            alert("服务器连接失败！")
        }
    })
}

function refresh_cache() {
    $.ajax({
        url: "/admin/refresh",
        sync: false,
        type: "GET",
        success: function (message) {
            if (message.status === "ok") {
                var msg = "缓存刷新成功！共刷新" + message.picInfo + "条图片信息，" + message.comment + "条评论信息，" +
                    message.top + "条置顶图片信息，" + message.loginState + "条用户登录信息";
                alert(msg);
            } else if (message.status === "fail") {
                alert("缓存刷新失败！")
            }
        }, error: function () {
            alert("服务器连接失败！")
        }
    })
}

function getLoginNum() {
    $.ajax({
        url: "/loginNum",
        sync: false,
        success: function (message) {
            if (message.status === 'ok') {
                var num = message.num;
                if (num != null && num !== '') {
                    document.getElementById("loginNum").innerHTML = num;
                } else {
                    document.getElementById("loginNum").innerHTML = '';
                }
            }
        }
    })
}