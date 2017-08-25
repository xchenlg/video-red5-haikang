<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet"
	href="../resources/plugins/jquery-easyui/themes/default/easyui.css" />
<link rel="stylesheet" href="../resources/css/extEasyUIIcon.css" />
<script type="text/javascript" src="../resources/plugins/jquery.min.js"></script>
<script type="text/javascript"
	src="../resources/plugins/jquery-easyui/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="../resources/plugins/jquery-easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="../resources/plugins/extJquery.js"></script>
<script type="text/javascript" src="../resources/plugins/extEasyUI.js"></script>
<title>通道设置</title>
<script type="text/javascript">
	var dataGrid;
	var sdkid;
	$(function() {
		$('#department_tree').tree({
			url : '../video/getSdkTree',
			parentField : 'pid',
			//lines : true,
			onClick : function(node) {
				var obj = new Object();
				obj.id = node.id;
				sdkid = node.id;
				dataGrid.datagrid('load', obj);
			},
			onBeforeLoad : function(node, param) {
				parent.$.messager.progress({
					title : '提示',
					text : '数据处理中，请稍后....'
				});
			},
			onLoadSuccess : function(node, data) {
				parent.$.messager.progress('close');
			}
		});
		dataGrid = $('#dataGrid')
				.datagrid(
						{
							url : '../video/chaneldataGrid',
							fit : true,
							rownumbers : true,
							fitColumns : true,
							border : false,
							striped : true,
							pagination : true,
							idField : 'id',
							pageSize : 10,
							pageList : [ 10, 20, 30, 40, 50 ],
							checkOnSelect : false,
							selectOnCheck : false,
							nowrap : true,
							toolbar : '#toolbar',
							columns : [ [
									{
										field : 'title',
										title : '标题',
										width : 150
									},
									{
										field : 'chanel',
										title : '通道号',
										width : 50
									},
									{
										field : 'content',
										title : '备注',
										width : 250
									},
									{
										field : 'createDate',
										title : '创建时间',
										width : 100,
										formatter : function(value, row, index) {
											if (value) {
												return new Date(value)
														.format("yyyy-MM-dd hh:mm:ss");
											}
										}
									},
									{
										field : 'updateDate',
										title : '修改时间',
										width : 100,
										formatter : function(value, row, index) {
											if (value) {
												return new Date(value)
														.format("yyyy-MM-dd hh:mm:ss");
											}
										}
									},
									{
										field : 'action',
										title : '操作',
										width : 100,
										formatter : function(value, row, index) {
											var str = '';

											str += $
													.formatString(
															'<img onclick="editFun(\'{0}\');" src="{1}" title="编辑"/>',
															row.id,
															'${pageContext.request.contextPath}/resources/css/images/extjs_icons/pencil.png');
											str += '&nbsp;';
											str += $
													.formatString(
															'<img onclick="deleteFun(\'{0}\');" src="{1}" title="删除"/>',
															row.id,
															'${pageContext.request.contextPath}/resources/css/images/extjs_icons/delete.png');
											str += $
													.formatString(
															'<img onclick="moveUpOrDown(\'{0}\',\''
																	+ row.id
																	+ '\');" src="{1}" title="上移"/>',
															'0',
															'${pageContext.request.contextPath}/resources/css/images/extjs_icons/arrow_skip_up.png');
											str += $
													.formatString(
															'<img onclick="moveUpOrDown(\'{0}\',\''
																	+ row.id
																	+ '\');" src="{1}" title="下移"/>',
															'1',
															'${pageContext.request.contextPath}/resources/css/images/extjs_icons/arrow_skip_down.png');
											return str;
										}
									} ] ],
							toolbar : '#toolbar',
							onLoadSuccess : function() {
								$(this).datagrid('tooltip');
							}
						});
	});

	//向上或者向下移动一位
	function moveUpOrDown(flag, id) {
		$("#" + id).click(
				function(e) {
					// 防止IE快速点击出错
					if ((navigator.userAgent.indexOf('MSIE') >= 0)
							&& (navigator.userAgent.indexOf('Opera') < 0)) {
						$("#" + id).attr('ondblclick', 'this.click()');
					}
					e.stopPropagation();

				});
		$.ajax({
			type : 'POST',
			url : "../video/moveUpOrDownChanel?flag=" + flag + '&id=' + id + '&sdkId=' + sdkid,
			dataType : 'json',
			success : function(data) {
				if (data.success == true) {
					$("#dataGrid").datagrid('clearSelections').datagrid(
							'clearChecked');
					$("#dataGrid").datagrid('reload');
				} else {
					alert("移动失败!");
				}
			}
		});
	}
	function addFun() {
		if(sdkid == undefined){
			alert("请先选择一个录像机!");
			return;
		}
		$.modalDialog({
			title : '添加',
			width : 500,
			height : 320,
			href : '../video/addChanelPage?sdkid='+sdkid,
			buttons : [ {
				text : '添加',
				handler : function() {
					$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					var f = $.modalDialog.handler.find('#form');
					f.submit();
				}
			} ]
		});
	}

	function editFun(id) {
		if (id == undefined) {//点击右键菜单才会触发这个
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {//点击操作里面的删除图标会触发这个
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		$.modalDialog({
			title : '修改',
			width : 500,
			height : 320,
			href : '../video/editChanelPage?id=' + id,
			buttons : [ {
				text : '修改',
				handler : function() {
					$.modalDialog.openner_dataGrid = dataGrid;//因为添加成功之后，需要刷新这个dataGrid，所以先预定义好
					var f = $.modalDialog.handler.find('#form');
					f.submit();
				}
			} ]
		});
	}

	function deleteFun(id) {
		if (id == undefined) {//点击右键菜单才会触发这个
			var rows = dataGrid.datagrid('getSelections');
			id = rows[0].id;
		} else {//点击操作里面的删除图标会触发这个
			dataGrid.datagrid('unselectAll').datagrid('uncheckAll');
		}
		parent.$.messager.confirm('询问', '确认删除？', function(b) {
			if (b) {
				$.messager.progress({
					title : '提示',
					text : '数据处理中，请稍后....'
				});
				$.post('${pageContext.request.contextPath}/video/deleteChanel', {
					id : id
				}, function(result) {
					if (result.success) {
						parent.$.messager.alert('提示', result.msg, 'info');
						dataGrid.datagrid('reload');
					}
					parent.$.messager.progress('close');
				}, 'JSON');
			}
		});
	}
	Date.prototype.format = function(format) {
		if (isNaN(this.getMonth())) {
			return '';
		}
		if (!format) {
			format = "yyyy-MM-dd hh:mm:ss";
		}
		var o = {
			/* month */
			"M+" : this.getMonth() + 1,
			/* day */
			"d+" : this.getDate(),
			/* hour */
			"h+" : this.getHours(),
			/* minute */
			"m+" : this.getMinutes(),
			/* second */
			"s+" : this.getSeconds(),
			/* quarter */
			"q+" : Math.floor((this.getMonth() + 3) / 3),
			/* millisecond */
			"S" : this.getMilliseconds()
		};
		if (/(y+)/.test(format)) {
			format = format.replace(RegExp.$1, (this.getFullYear() + "")
					.substr(4 - RegExp.$1.length));
		}
		for ( var k in o) {
			if (new RegExp("(" + k + ")").test(format)) {
				format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
						: ("00" + o[k]).substr(("" + o[k]).length));
			}
		}
		return format;
	};
	function logoutFun() {
		 window.location.assign("${pageContext.request.contextPath}/person/logout");
	}
</script>
</head>
<body>
	<div style="position: absolute; right: 70px; top: 12px;">
	  [<strong style="font-size: 12px;">${user}</strong>]
	</div>
	<div style="position: absolute; right: 12px; top: 0px;margin-top:10px;">
		<a href="javascript:void(0);" onclick="logoutFun();" class="easyui-menubutton" style="font-size: 14px;color: black;">注销</a>
	</div>
	<div style="font-size: 28px;line-height:40px; vertical-align: middle;">录像机管理系统</div>
	<div class="easyui-layout" data-options="fit : true,border : false">
		<div data-options="region:'west',border:false"
			style="width: 200px; padding: 5px; overflow-x: scroll;">
			<div class="well_user well-small_user">
				<ul id="department_tree"></ul>
			</div>
		</div>
		<div data-options="region:'center',border:false">
			<table id="dataGrid"></table>
		</div>
	</div>
	<div id="toolbar" style="padding: 5px; height: auto">
		<div>
			<a onclick="addFun();" href="javascript:void(0);"
				class="easyui-linkbutton"
				data-options="plain:true,iconCls:'pencil_add'">添加</a>
		</div>
	</div>
</body>
</html>