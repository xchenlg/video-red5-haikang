var index_tabs;
var index_tabsMenu;
var index_layout;

$(function() {
  initMenu();
  initTab();
});

function initTab() {

  index_layout = $('#index_layout').layout({
    fit : true
  });

  /* index_layout.layout('collapse', 'east'); */

  index_tabs = $('#index_tabs').tabs({
    fit : true,
    border : false,
    onContextMenu : function(e, title) {
      e.preventDefault();
      index_tabsMenu.menu('show', {
        left : e.pageX,
        top : e.pageY
      }).data('tabTitle', title);
    },
    tools : [ {
      iconCls : 'arrow_arrow_refresh',
      handler : function() {
        var href = index_tabs.tabs('getSelected').panel('options').href;
        if (href) {/* 说明tab是以href方式引入的目标页面 */
          var index = index_tabs.tabs('getTabIndex', index_tabs.tabs('getSelected'));
          index_tabs.tabs('getTab', index).panel('refresh');
        } else {/* 说明tab是以content方式引入的目标页面 */
          var panel = index_tabs.tabs('getSelected').panel('panel');
          var frame = panel.find('iframe');

          try {
            if (frame.length > 0) {
              for (var i = 0; i < frame.length; i++) {
                frame[i].contentWindow.document.write('');
                frame[i].contentWindow.close();
                frame[i].src = frame[i].src;
              }
              if (navigator.userAgent.indexOf("MSIE") > 0) {// IE特有回收内存方法
                try {
                  CollectGarbage();
                } catch (e) {
                }
              }
            }
          } catch (e) {
          }
        }
      }
    }, {
      iconCls : 'arrow_cross',
      handler : function() {
        var index = index_tabs.tabs('getTabIndex', index_tabs.tabs('getSelected'));
        var tab = index_tabs.tabs('getTab', index);
        if (tab.panel('options').closable) {
          index_tabs.tabs('close', index);
        } else {
          $.messager.alert('提示', '[' + tab.panel('options').title + ']不可以被关闭！', 'error');
        }
      }
    } ]
  });

  index_tabsMenu = $('#index_tabsMenu').menu({
    onClick : function(item) {
      var curTabTitle = $(this).data('tabTitle');
      var type = $(item.target).attr('title');

      if (type === 'refresh') {
        index_tabs.tabs('getTab', curTabTitle).panel('refresh');
        return;
      }

      if (type === 'close') {
        var t = index_tabs.tabs('getTab', curTabTitle);
        if (t.panel('options').closable) {
          index_tabs.tabs('close', curTabTitle);
        }
        return;
      }

      var allTabs = index_tabs.tabs('tabs');
      var closeTabsTitle = [];

      $.each(allTabs, function() {
        var opt = $(this).panel('options');
        if (opt.closable && opt.title != curTabTitle && type === 'closeOther') {
          closeTabsTitle.push(opt.title);
        } else if (opt.closable && type === 'closeAll') {
          closeTabsTitle.push(opt.title);
        }
      });

      for (var i = 0; i < closeTabsTitle.length; i++) {
        index_tabs.tabs('close', closeTabsTitle[i]);
      }
    }
  });
}

function initMenu() {

  $.ajax({
    type : 'POST',
    url : ade.bp() + '/admin/module/topMenu',
    data : {
      t : new Date()
    },
    dataType : 'json',
    async : false,
    success : function(result) {
      handleMenuResult(result);
    },
    error : function() {
    }
  });

  parent.$.messager.progress('close');

}

var treeArray;

function handleMenuResult(jsonData) {

  var html = "";
  treeArray = new Array();

  // / 首先在画面上加载每一个节点
  for (var i = 0; i < jsonData.length; i++) {
   
    html = html + "<div id=\"menu_" + jsonData[i].id + "\" title=\"";
    html = html + jsonData[i].text;

    html = html + "\" style=\"padding: 5px;\" ";
    html = html + "data-options=\"";
    html = html + "border:false";

    if (jsonData[i].iconCls) {
      html = html + ",iconCls:'" + jsonData[i].iconCls + "'";
    }

    html = html + "\">";

    if (jsonData[i].hasGrandson) {
      html = html + "<ul id=\"menuTree_" + jsonData[i].id + "\"></ul>";
      $('#menu_' + jsonData[i].id).html(html);
    }

    html = html + "</div>";

  }

  $('#adeMenuBar').html(html);

  // 再次循环一级菜单
  for (var i = 0; i < jsonData.length; i++) {

    var html = "";

    if (jsonData[i].hasGrandson) {

      var tempTree = $('#menuTree_' + jsonData[i].id).tree({
        url : ade.bp() + "/admin/module/treeMenu?pid=" + jsonData[i].id,
        lines : true,
        onClick : function(node) {

          $('.menuItem').linkbutton({
            selected : false
          });

          if (node.attributes && node.attributes.url) {
            var url;
            if (node.attributes.url.indexOf('/') == 0) {/* 如果url第一位字符是"/"，那么代表打开的是本地的资源 */
              url = ade.bp() + node.attributes.url;
              parent.$.messager.progress({
                title : '提示',
                text : '数据处理中，请稍后....'
              });
            } else {/* 打开跨域资源 */
              url = node.attributes.url;
            }
            addTab({
              url : url,
              title : node.text,
              iconCls : node.iconCls
            });
          }
        }
      });

      treeArray.push(tempTree);

    } else if (jsonData[i].children) {

      // 生成A连接之后再将A连接转换为linkbutton
      html = html + "<div>";
      var children = jsonData[i].children;
      // 检测儿子
      for (var x = 0; x < children.length; x++) {
        html = html + "<a class=\"menuItem\" id=\"menuItem_" + children[x].id + "\" ";
        html = html + "style=\"width:98%;margin-bottom:5px;\" >";
        html = html + children[x].text;
        html = html + "</a>";
      }
      html = html + "</div>";
      $('#menu_' + jsonData[i].id).html(html);

      // 刷新按钮
      for (var x = 0; x < children.length; x++) {

        (function() {

          var iconCls = children[x].iconCls;
          if (!iconCls || iconCls.length == 0) {
            iconCls = 'notes_note';
          }

          var text = children[x].text;

          var url;
          if (children[x].attributes.url.indexOf('/') == 0) {
            // 如果url第一位字符是"/"，那么代表打开的是本地的资源
            url = ade.bp() + children[x].attributes.url;
          } else {
            // 打开跨域资源
            url = children[x].attributes.url;
          }

          // 绑定事件
          $('#menuItem_' + children[x].id).linkbutton({
            plain : true,
            iconCls : iconCls,
            group : '',
            onClick : function() {
              addTab({
                url : url,
                title : text,
                iconCls : iconCls
              });
            }
          });
          // 绑定事件
        })();
      }
    }
  }

  // 初始化折叠窗口
  $('#adeMenuBar').accordion({
    animate : true,
    fit : false,
    border : false,
    multiple : true
  });

  $('.menuItem').on('click', function() {
    $('.menuItem').linkbutton({
      selected : false
    });

    for (var i = 0; i < treeArray.length; i++) {
      $(treeArray[i]).tree('select');
    }

    $(this).linkbutton({
      selected : true
    });
  });
}

function addTab(params) {
  var iframe = '<iframe src="' + params.url
      + '" frameborder="0" style="border:0;width:100%;height:98%;"></iframe>';
  var t = $('#index_tabs');
  var opts = {
    title : params.title,
    closable : true,
    iconCls : params.iconCls,
    content : iframe,
    border : false,
    fit : true
  };
  if (t.tabs('exists', opts.title)) {
    t.tabs('select', opts.title);
    parent.$.messager.progress('close');
  } else {
    t.tabs('add', opts);
  }
}

var sysMenuTools = [ {
  iconCls : 'database_refresh',
  handler : function() {
    $('#layout_west_sys_tree').tree('reload');
  }
}, {
  iconCls : 'resultset_next',
  handler : function() {
    var node = $('#layout_west_sys_tree').tree('getSelected');
    if (node) {
      $('#layout_west_sys_tree').tree('expandAll', node.target);
    } else {
      $('#layout_west_sys_tree').tree('expandAll');
    }
  }
}, {
  iconCls : 'resultset_previous',
  handler : function() {
    var node = $('#layout_west_sys_tree').tree('getSelected');
    if (node) {
      $('#layout_west_sys_tree').tree('collapseAll', node.target);
    } else {
      $('#layout_west_sys_tree').tree('collapseAll');
    }
  }
} ];