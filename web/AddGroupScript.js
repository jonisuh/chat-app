$(document).ready(function () {
    var myID = getCookie("userID");
    $.ajax({
        type: "GET",
        url: "/ProjectV1/API/Users/",
        dataType: 'xml'
        , success: function (userInformation) {
             $('user', userInformation).each(function () {
                    var userID = $(this).find("userID").text();
                    var username = $(this).find("username").text();
                    if(parseInt(myID) != parseInt(userID)){
                        $("#users").append("<div class='usercontainer' id='user_"+userID+"'><p>"+username+"</p></div>");
                    }
                 });
        }
    });
    
    $("#addUsers").click(function(){
        $("#users").toggle();
    });
    
    $(document).on('click', '.usercontainer', function () {
        $(this).toggleClass('selectedContainer');
    });
    
    $("#createNewGroup").click(function(){
        var groupname = $("#newGroupName").val();
        
        $.ajax({
            type: "POST",
            url: "/ProjectV1/API/Groups/",
            data: {groupname:groupname,groupstarterID:myID}
            ,success: function (createdGroupID) {
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
                location.reload();
            }
            
        });
        
    });

});

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ')
            c = c.substring(1);
        if (c.indexOf(name) == 0)
            return c.substring(name.length, c.length);
    }
    return "";
} 