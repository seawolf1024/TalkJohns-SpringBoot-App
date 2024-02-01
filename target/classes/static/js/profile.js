$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	if($(btn).hasClass("btn-info")) {
	    console.log('follow');
		// 关注TA
		$.post(
            CONTEXT_PATH + "/follow",
            {"entityType":3,"entityId":$(btn).prev().val()},
            function(data){
                data = $.parseJSON(data);
                if(data.code == 0){
//                    $(btn).removeClass("btn-info").addClass("btn-secondary");
//                    console.log(data);
//                    console.log(data.msg=="Followed"?"Followed":"Follow");
//                    $(btn).text(data.msg=="Followed"?"Followed":"Follow");
                    window.location.reload();
                }else{
                    alert(data.msg);
                }
            }
		);
		//$(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
	} else {
		// 取消关注
		 console.log('unfollow');
		$.post(
            CONTEXT_PATH + "/unfollow",
            {"entityType":3,"entityId":$(btn).prev().val()},
            function(data){
                data = $.parseJSON(data);
                if(data.code == 0){
//                    $(btn).removeClass("btn-secondary").addClass("btn-info");
//                    console.log(data);
//                    console.log(data.msg=="Unfollowed"?"Follow":"Followed");
//                    $(btn).text(data.msg=="Unfollowed"?"Follow":"Followed");
                    window.location.reload();
                }else{
                    alert(data.msg);
                }
            }
        );
//		$(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
	}
}