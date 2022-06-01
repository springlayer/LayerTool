/**
 * 隐藏部分暂时用不到的菜单
 * Case模型  决策表  应用程序  登录账号
 * @author Ronaldo
 */

jQuery(document).ready(function (){
    jQuery("#main-nav").children().each(function (index, element) {

        if(1 === index || 3 === index){
            jQuery(this).css("display","none");
        }
    });

});