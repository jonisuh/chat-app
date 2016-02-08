$(document).ready(function () {
    var userID = getCookie("userID");
    $.ajax({
        type: "GET",
        url: "/ProjectV1/API/Users/" + userID + "/",
        dataType: 'xml'
        , success: function (userInformation) {
            var userName = $(userInformation).find("username");
            $(".usernamespan").html(userName);
        }
    });

    $.ajax({
        type: "GET",
        url: "/ProjectV1/API/Users/" + userID + "/groups/",
        dataType: 'xml'
        , success: function (groupInformation) {
            $("#messageSpace").append("<h2>Groups</h2>");
            $groups = $(groupInformation);
            $(groupInformation).find("group").each(function () {
                var groupName = $(this).find("groupName").text();
                var groupID = $(this).find("groupID").text();
                $("#messageSpace").append("<div class='group' id='group_" + groupID + "'><h3>" + groupName + "</h3></div>");
            });

        }
    });
    $("#placeholder").attr("id", userID);

    $("#sendMessage").click(function () {
        var message = $("#message").val();
        $("#message").val("");
        var userID = $(".username").attr("id");
        var groupID = $(".usergroup").attr("id");

        $.ajax({
            type: "POST",
            url: "/ProjectV1/API/Groups/" + groupID + "/users/" + userID + "/messages/",
            data: {message: message}
            , success: function (messageCreation) {

            }
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
    
    function scrollToBot(){
        var height = 0;
        $('.message').each(function(i, value){
            height += parseInt($(this).height());
        });

        height += '';

        $('#messageSpace').scrollTop(height);
    }
    
    $(document).on('click', '.group', function () {
        var groupID = $(this).attr("id");
        var groupIDSplit = groupID.split("_");
        $("#groupplaceholder").attr("id", groupIDSplit[1]);
        $(".groupnamespan").html($(this).text());
        $("#messageSpace").html(" ");
        
        //var userID = $(".username").attr("id");
        
        
        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Groups/" + groupIDSplit[1] + "/messages/",
            dataType: 'xml'
            , success: function (messages) {
                //$messages = $( messages );

                $('messageroot', messages).each(function () {
                    var senderID = $(this).find("userID").text();
                    var message = $(this).find("message").text();
                    var messageID = $(this).find("messageID").text();
                    var timestamp = $(this).find("timestamp").text();
                    //Getting the username
                    var messageclasses = "message";
                    $.ajax({
                        type: "GET",
                        async: false,
                        url: "/ProjectV1/API/Users/" + senderID + "/",
                        dataType: 'xml'
                        , success: function (senderInfo) {
                            var senderName = $(senderInfo).find("username").text();
                            if (senderID === userID) {
                                messageclasses = "message mymessage"
                            }
                            $("#messageSpace").append("<div class='" + messageclasses + "' id='message_" + messageID + "'><p>" + message + "</p><p>" + senderName + " <span class='timestamp'>" + timestamp + "</span></p></div>");
                        }
                    });

                });
                scrollToBot();
                checkMessages(groupIDSplit[1]);   
            }           
        });
        
        
     
    });
    function loadMessages(groupID){
        var messageAttrID = $("#messageSpace div:last").attr("id");
        var messageIDSplit = messageAttrID.split("_");
        var latestMessage = messageIDSplit[1];
        
        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Groups/" + groupID + "/messages/",
            dataType: 'xml'
            , success: function (messages) {
                              
                $('messageroot', messages).each(function () {
                    var senderID = $(this).find("userID").text();
                    var message = $(this).find("message").text();
                    var messageID = $(this).find("messageID").text();
                    var timestamp = $(this).find("timestamp").text();
                    
                    if(parseInt(messageID) > parseInt(latestMessage)){
                    //Getting the username
                        var messageclasses = "message";
                        $.ajax({
                            type: "GET",
                            async: false,
                            url: "/ProjectV1/API/Users/" + senderID + "/",
                            dataType: 'xml'
                            , success: function (senderInfo) {
                                var senderName = $(senderInfo).find("username").text();
                                if (senderID === userID) {
                                    messageclasses = "message mymessage"
                                }
                                $("#messageSpace").append("<div class='" + messageclasses + "' id='message_" + messageID + "'><p>" + message + "</p><p>" + senderName + " <span class='timestamp'>" + timestamp + "</span></p></div>");
                            }
                        });
                    }
                });
                scrollToBot();
                checkMessages(groupID);   
            }           
        });
    }
    
    function checkMessages(groupID){
        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Groups/" + groupID + "/messages/",
            dataType: 'xml'
            , success: function (messages) {
                //$messages = $( messages );
                var $lastmessage = $(messages).find('messageroot:last');                
                var messageID = $lastmessage.find("messageID").text();
                
                var messageAttrID = $("#messageSpace div:last").attr("id");
                var messageIDSplit = messageAttrID.split("_");
                var latestMessage = messageIDSplit[1];
                
                if(parseInt(messageID) > parseInt(latestMessage)){
                    loadMessages(groupID);
                }else{
                    setTimeout(function(){
                      checkMessages(groupID);  
                    }, 1000);                   
                }
            }           
        });
    }
    
    $("#logout").click(function () {
        $.ajax({
            type: "POST",
            url: "/ProjectV1/API/Users/logout",
            success: function (logoutstatus) {
                
                document.cookie="userID=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
                window.location.href = "/ProjectV1/index.jsp";
            }
        });
    });

});