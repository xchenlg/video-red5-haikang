<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="../jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="../jwplayer.js"></script>
<script type="text/javascript" src="../jwplayer.html5.js"></script>
<title>audio</title>
<script type="text/javascript">
	$(function() {
		showVideo();
		$.ajax({
			type : 'POST',
			url : '../video/getChanels',
			dataType : 'json',
			success : function(data) {
				var str = "";
				for(var i=0;i<data.length;i++){
					str += '<button onclick="turnLeftSet(\''+data[i]+'\')" >左转'+data[i]+'</button>';
					str += '<button onclick="turnRight(\''+data[i]+'\')" >右转'+data[i]+'</button>';
				}
				$("#chanel").append(str);
			}
		});
	});
	function showVideo() {
		jwplayer("jwplayer_flv").setup({
			file : "rtmp://localhost/oflaDemo//hello",
			flashplayer : '../jwplayer.flash.swf',
			image : '',
			primary : 'flash',
			width : '800',
			height : '600',
			autostart : true,
			mute : false,
			repeat : false
		});
	}
	function play() {
		$.ajax({
			type : 'POST',
			url : '../video/liveVideo',
// 			url : '../page/login',
			dataType : 'json',
			async : false,
			success : function(data) {
				//alert(data);
			}
		});
	}
	function stop() {
		$.ajax({
			type : 'POST',
			url : '../video/closeVideo',
			dataType : 'json',
			async : false,
			success : function(data) {
			}
		});
	}

	function turnLeftSet(chanel) {
		$.ajax({
			type : 'POST',
			url : '../video/turnLeft?chanel='+chanel,
			dataType : 'json',
			async : false,
			success : function(data) {
			}
		});
	}
	function turnRight(chanel) {
		$.ajax({
			type : 'POST',
			url : '../video/turnRight?chanel='+chanel,
			dataType : 'json',
			async : false,
			success : function(data) {
			}
		});
	}
</script>
</head>
<body>
	<div id="chanel">
	</div>
	<a href="###" onclick="play()">播放</a>
	<a href="###" onclick="stop()">关闭</a>
	<div id="jwplayer_flv"
		style="width: 700px; height: 300px; position: relative;">
		<object name="playerzmblbkjP" width="100%" height="100%"
			id="playerzmblbkjP"
			classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
			style="position: absolute; top: 0; left: 0;">
			<param name="movie" value="jwplayer.flash.swf">
			<param name="src" value="jwplayer.flash.swf">
			<param name="AllowScriptAccess" value="always">
		</object>
	</div>
</body>
</html>