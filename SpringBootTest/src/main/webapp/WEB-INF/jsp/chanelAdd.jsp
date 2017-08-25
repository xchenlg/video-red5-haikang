<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">
	$(function() {

		parent.$.messager.progress('close');
		$('#form').form({
			url : '../video/addChanel',
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
			<input name="id" type="hidden" value="${chanel.id}" />
			<input name="sdkId" type="hidden" value="${chanel.sdkId}" />
			<input name="chanelOrder" type="hidden" value="${chanel.chanelOrder}" />
			<input name="create_date" type="hidden" value="${chanel.createDate}" />
			<table class="table table-hover table-condensed">
				<tr style="height:38px;">
					<th style="width:48px;">标题</th>
					<td colspan="3"><input style="width:399px;" name="title" type="text" placeholder="请输入标题"
						class="easyui-validatebox span2" data-options="required:true"
						value="${chanel.title}" /></td>
				</tr>
				<tr style="height:38px;">
					<th style="width:48px;">通道号</th>
					<td><input name="chanel" style="width:399px;" type="text" placeholder="请输入ip"
						class="easyui-validatebox span2" data-options="required:true"
						value="${chanel.chanel}" /></td>
				</tr>
				<tr>
					<th style="width:48px;">备注</th>
					<td colspan="3"><textarea style="width:396px;height:80px;" name="content">${chanel.content}</textarea></td>
				</tr>
			</table>
		</form>
	</div>
</div>