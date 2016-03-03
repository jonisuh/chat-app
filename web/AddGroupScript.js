$(document).ready(function () {
    var myID = sessionStorage.userID;
    var basicCredentials = sessionStorage.credentials;
    console.log(myID);
    console.log(basicCredentials);

    $("#newgroupbutton").click(function () {
        $(".dropdown-content").hide();
        $(".newGroupUsers").html("");
        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Users/",
            dataType: 'xml'
            , success: function (userlist) {
                $('user', userlist).each(function () {
                    var thisuserID = $(this).find("userID").text();
                    var thisuserName = $(this).find("username").text();
                    if (parseInt(thisuserID) === parseInt(myID)) {
                        thisuserName = "Me";
                    }
                    $(".newGroupUsers").append("<div class='newGroupUser' id='newgroupuser_" + thisuserID + "'><h3>" + thisuserName + "</h3></div>")
                });
            }
        });
        $("#createGroup").show();
        
    });
    
    $(document).on('click', '.startChatWithUser',function(){
        var thisUserID = $(this).attr("id");
        var idSplit = thisUserID.split("_");
        var selectedUserID = idSplit[1];
        var groupname = $("#user_"+selectedUserID+" h3").clone().children().remove().end().text();
        groupname = groupname +", "+$(".usernamespan").text();
        var chatExists = false;
        
      /*  $(".group").each(function(){
            if($(this).children("h3").text() === groupname){
                $(this).click();
                chatExists = true;
            }
        }); */
        
        if(chatExists === false){
            $.ajax({
                type: "POST",
                url: "/ProjectV1/API/Groups/",
                data: {groupname:groupname,groupstarterID:myID},
                headers:{
                    "Authorization": basicCredentials
                },success: function (createdGroupID) {
                       $.ajax({
                            type: "POST",
                            url: "/ProjectV1/API/Groups/"+createdGroupID+"/users/",
                            data: {userID:selectedUserID},
                            headers:{
                                "Authorization": basicCredentials
                            },success: function () {
                                $("#noGroupsFound").remove();
                                $("#groupWrapper").prepend("<div class='group' id='group_" + createdGroupID + "'><h3>" + groupname + "</h3><span class='moreGroupInfo'>▼</span></div>");
                                $("#group_" + createdGroupID).append("<div class='groupInformation' id='group_" + createdGroupID + "_info'><hr>\n\
                                <div class='leaveThisGroupButton userInfoButton' id='leaveGroup_" + createdGroupID + "'><img src='pictures/hide.png' alt='Leave group' title='Leave group' height='20' width='20'></div>\n\
                                </div>");
                            
                                var thisGroupAdmins = [];
                                //checking if user is admin of this group
                                $.ajax({
                                    type: "GET",
                                    url: "/ProjectV1/API/Groups/" + createdGroupID + "/admins/",
                                    dataType: 'xml',
                                    headers: {
                                        "Authorization": basicCredentials
                                    },
                                    success: function (admins) {
                                        $('user', admins).each(function () {
                                            thisGroupAdmins.push($(this).find("userID").text());
                                        });

                                        if ($.inArray(myID, thisGroupAdmins) > -1) {
                                            $("#group_" + createdGroupID + "_info").append("<div class='deleteThisGroupButton userInfoButton' id='deleteGroup_" + createdGroupID + "'><img src='pictures/hide.png' alt='Delete group' title='Delete group' height='20' width='20'></div>");
                                        }
                                    }
                                });
                                $("#group_"+createdGroupID+" h3").click();
                            }
                        });    

                }

            });
        }
    });
    
    $(document).on('click', '.newGroupUser', function () {
        var divID = $(this).attr("id");
        var idSplit = divID.split("_");
        var selectedUserID = idSplit[1];
        if(parseInt(selectedUserID) != parseInt(myID)){
            $(this).toggleClass('selectedContainer');
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
               $('.selectedContainer').each(function(i, obj) {
                   var divID = $(this).attr("id");
                   var idSplit = divID.split("_");
                   var thisuserID = idSplit[1];
                   $.ajax({
                        type: "POST",
                        url: "/ProjectV1/API/Groups/"+createdGroupID+"/users/",
                        data: {userID:thisuserID},
                        headers:{
                            "Authorization": basicCredentials
                        },success: function () {
                            
                        }
                    });    
                });
                setTimeout(function() {
                   $(".selectedContainer").toggleClass('selectedContainer');
                   $("#createGroup").hide();
                }, 100);
                $("#noGroupsFound").remove();
                $("#groupWrapper").prepend("<div class='group' id='group_" + createdGroupID + "'><h3>" + groupname + "</h3><span class='moreGroupInfo'>▼</span></div>");
                
                $("#group_" + createdGroupID).append("<div class='groupInformation' id='group_" + createdGroupID + "_info'><hr>\n\
                <div class='leaveThisGroupButton userInfoButton' id='leaveGroup_" + createdGroupID + "'><img src='pictures/hide.png' alt='Leave group' title='Leave group' height='20' width='20'></div>\n\
                </div>");

                var thisGroupAdmins = [];
                //checking if user is admin of this group
                $.ajax({
                    type: "GET",
                    url: "/ProjectV1/API/Groups/" + createdGroupID + "/admins/",
                    dataType: 'xml',
                    headers: {
                        "Authorization": basicCredentials
                    },
                    success: function (admins) {
                        $('user', admins).each(function () {
                            thisGroupAdmins.push($(this).find("userID").text());
                        });

                        if ($.inArray(myID, thisGroupAdmins) > -1) {
                            $("#group_" + createdGroupID + "_info").append("<div class='deleteThisGroupButton userInfoButton' id='deleteGroup_" + createdGroupID + "'><img src='pictures/hide.png' alt='Delete group' title='Delete group' height='20' width='20'></div>");
                        }
                    }
                });
            }
            
        });
        
    });

});
