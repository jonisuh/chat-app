$(document).ready(function () {
    var myID = sessionStorage.userID;
    var basicCredentials = sessionStorage.credentials;
    console.log(myID);
    console.log(basicCredentials);

    
    $(document).on('click', '.usercontainer', function () {
        var divID = $(this).attr("id");
        var idSplit = divID.split("_");
        var selectedUserID = idSplit[1];
        if(parseInt(selectedUserID) != parseInt(myID)){
            $(this).toggleClass('selectedContainer');
            var selectedCount = $(".selectedContainer").length;
            if(selectedCount >= 2){
                $("#createGroup").show();
            }else{
                $("#createGroup").hide();
            }
        }
    });
    
    $("#createNewGroup").click(function(){
        var groupname = $("#newGroupName").val();
        
        $.ajax({
            type: "POST",
            url: "/ProjectV1/API/Groups/",
            data: {groupname:groupname,groupstarterID:myID},
            headers:{
                "Authorization": basicCredentials
            },success: function (createdGroupID) {
               $("#groupWrapper").append("<div class='group' id='group_" + createdGroupID + "'><h3>" + groupname + "</h3></div>");
               $('.selectedContainer').each(function(i, obj) {
                   var divID = $(this).attr("id");
                   var idSplit = divID.split("_");
                   var thisuserID = idSplit[1];
                   $.ajax({
                        type: "POST",
                        url: "/ProjectV1/API/Groups/"+createdGroupID+"/users/",
                        data: {userID:thisuserID}
                        ,success: function () {
                            
                        }
                    });    
                }); 
            }
            
        });
        
    });

});
