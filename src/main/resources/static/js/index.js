$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// get title and content
	var title = $("#recipient-name").val();
	var content = $("#message-text").val(); // get title and content from HTML file, and then give them to backend

	// send asynchronous request
	$.post(
	    CONTEXT_PATH + "/discuss/add",
	    {"title":title, "content":content},
	    function(data){
            data = $.parseJSON(data); // get data from the backend, e.g. {"code": 0, "msg": "Posted successfully."};
            // show response in hintbody
            $("#hintBody").text(data.msg);
            // show hintbody
            $("#hintModal").modal("show");
            // hide hintbody after 2 seconds
            setTimeout(function(){
                $("#hintModal").modal("hide");
                // if sent successfully, refresh the page
                if(data.code == 0){
                    window.location.reload();
                }
            }, 2000);
	    }
	)


}