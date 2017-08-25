<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<title>录像机管理系统</title>
<script src="${ctx}/resources/plugins/jquery.min.js"></script>
<script type="text/javascript">
	$(function() {
		document.onkeydown = keyListener;
	})

	function keyListener(e) {
		e = e ? e : event;
		if (e.keyCode == 13) {
			login();
		}
	}
	function loginOK() {
		window.location.href = '${pageContext.request.contextPath}/video/sdkJsp';
	}
<%//当前页面的用户信息%>
	var codeBase = "${pageContext.request.contextPath}";
<%//当前页面的用户信息%>
	var sessionInfo_userId = '${LOGIN_USER.id}';
	if (sessionInfo_userId) {
		loginOK();
	}
</script>

<link rel="stylesheet" type="text/css"
	href="${ctx}/resources/css/admin/login.css" />

<script type="text/javascript">
	function login() {
		$('#login_main_errortip').html('&nbsp;');
		var message = validate();
		if (message != '') {// 校验失败
			$('#login_main_errortip').html(message);
			return;
		}

		$.ajax({
			type : 'POST',
			url : '${pageContext.request.contextPath}/person/login',
			data : {
				t : new Date(),
				user : $('#loginName').val(),
				password : $('#password').val()
			},
			dataType : 'json',
			success : function(result) {
				if (result) {
					loginOK();
				} else {
					$('#login_main_errortip').html("用户名或密码错误!");
				}
			},
			error : function() {
			}
		});
	}

	function validate() {

		if ($('#loginName').val() == '') {
			return '登录名不能为空，请输入登录名！';
		}
		if ($('#password').val() == '') {
			return '密码不能为空，请输入密码！';
		}
		<c:if test="${ADMIN_LOGIN_CAPTCHA}">
		if ($('#captcha').val() == '') {
			return '验证码不能为空，请输入验证码！';
		}
		</c:if>

		return '';
	}
</script>
</head>
<body>
	<div>
		<div class="login_top">
			<div class="login_title">录像机管理系统登录页</div>
		</div>
		<div style="float: left; width: 100%;">
			<div class="login_main">
				<div class="login_main_top"></div>
				<div id="login_main_errortip" class="login_main_errortip">&nbsp;</div>
				<div class="login_main_ln">
					<input type="text" id="loginName" name="loginName" value="" />
				</div>
				<div class="login_main_pw">
					<input type="password" id="password" name="password" value="" />
				</div>
				<div class="login_main_submit">
					<button onclick="login()"></button>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
