<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">
	$(function() {

		parent.$.messager.progress('close');
		$('#form').form({
			url : '../video/add',
			onSubmit : function() {
				parent.$.messager.progress({
					title : '提示',
					text : '数据处理中，请稍后....'
				});
				var isValid = $(this).form('validate');
				if (!isValid) {
					parent.$.messager.progress('close');
				}
				return isValid;
			},
			success : function(result) {
				parent.$.messager.progress('close');
				result = $.parseJSON(result);
				if (result.success) {
					parent.$.modalDialog.openner_dataGrid.datagrid('reload');//之所以能在这里调用到parent.$.modalDialog.openner_dataGrid这个对象，是因为user.jsp页面预定义好了
					parent.$.modalDialog.handler.dialog('close');
					parent.$.messager.show({
						title : '成功',
						msg : '处理成功！'
					});
				} else {
					parent.$.messager.alert('错误', result.msg, 'error');
				}
			}
		});
	});
</script>
<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" title=""
		style="overflow: hidden;">
		<form id="form" method="post">
			<input name="id" type="hidden" value="${sdk.id}" />
			<input name="sdkOrder" type="hidden" value="${sdk.sdkOrder}" />
			<input name="create_date" type="hidden" value="${sdk.createDate}" />
			<table class="table table-hover table-condensed">
				<tr style="height:38px;">
					<th style="width:48px;">标题</th>
					<td colspan="3"><input style="width:399px;" name="title" type="text" placeholder="请输入标题"
						class="easyui-validatebox span2" data-options="required:true"
						value="${sdk.title}" /></td>
				</tr>
				<tr style="height:38px;">
					<th style="width:48px;">ip</th>
					<td><input name="ip" type="text" placeholder="请输入ip"
						class="easyui-validatebox span2" data-options="required:true"
						value="${sdk.ip}" /></td>
					<th style="width:48px;">端口</th>
					<td><input name="port" type="text" placeholder="请输入端口"
						class="easyui-validatebox span2" data-options="required:true" value="${sdk.port}"/></td>
				</tr>
				<tr style="height:38px;">
					<th>用户名</th>
					<td><input name="user" type="text" placeholder="请输入用户名称"
						class="easyui-validatebox span2" data-options="required:true" value="${sdk.user}"/></td>
					<th>密码</th>
					<td><input name="password" type="text" placeholder="请输入密码"
						class="easyui-validatebox span2" data-options="required:true" value="${sdk.password}"/></td>
				</tr>
				<tr>
					<th style="width:48px;">内容</th>
					<td colspan="3"><textarea style="width:396px;height:80px;" name="content">${sdk.content}</textarea></td>
				</tr>
			</table>
		</form>
	</div>
</div>