$(document).ready(function() {
    $("#loginButton").click(function(){
        var username = $("#username").val();
        var password = $("#password").val();
        $.ajax({
            type: "POST",
            url: "/ProjectV1/API/Users/login/",
            data: {name: username,password: password},
            statusCode: {
                200: function (response) {
                alert('Correct login.');
               },
               401: function (response) {
                  alert('Invalid login.');
               }
            }
            ,success: function(loginResponse){
                document.cookie="userID="+loginResponse;
                
                window.location.href = "/ProjectV1/chatscreen.html";
                /*
            
                $(".content").load( "/ProjectV1/chatscreen.html", function() {
                    $.ajax({
                    type: "GET",
                    url: "/ProjectV1/API/Users/"+loginResponse+"/",
                    dataType: 'xml'
                    ,success: function(userInformation){
                            var userName = $( userInformation ).find("username");
                            $(".username").html(userName);
                        }
                    });
                    
                    $.ajax({
                    type: "GET",
                    url: "/ProjectV1/API/Users/"+loginResponse+"/groups/",
                    dataType: 'xml'
                    ,success: function(groupInformation){
                        $("#messageSpace").append("<h2>Groups</h2>");
                            $groups = $( groupInformation );
                            $( groupInformation ).find("group").each(function(){
                                var groupName = $(this).find("groupName").text();
                                var groupID = $(this).find("groupID").text();
                                $("#messageSpace").append("<div class='group' id='group_"+groupID+"'><h3>"+groupName+"</h3></div>");
                            });
                            
                        }
                    });
                    
                    $("#placeholder").attr("id",loginResponse);
                    $("#sendMessage").click(function(){
                       var message = $("#message").val();
                       $("#message").val("");
                       var userID = $(".username").attr("id");
                       var groupID = $(".usergroup").attr("id");

                       $.ajax({
                           type: "POST",
                           url: "/ProjectV1/API/Groups/"+groupID+"/users/"+userID+"/messages/",
                           data: {message: message}
                           ,success: function(messageCreation){
                                
                               }
                           });
                    });                   
                });
             */
            }
        });
        /*
        $(document).on('click', '.group', function(){
            var groupID = $(this).attr("id");
            var groupIDSplit = groupID.split("_");
            $("#groupplaceholder").attr("id",groupIDSplit[1]);
            $(".usergroup").html($(this).text());
            $("#messageSpace").html(" ");
            
            //var userID = $(".username").attr("id");
            
            $.ajax({
                type: "GET",
                url: "/ProjectV1/API/Groups/"+groupIDSplit[1]+"/messages/",
                dataType: 'xml'
                ,success: function(messages){
                        //$messages = $( messages );
                        
                        $('messageroot',messages ).each(function(){
                            var senderID = $(this).find("userID").text();
                            var message = $(this).find("message").text();
                            var messageID = $(this).find("messageID").text();
                            var timestamp = $(this).find("timestamp").text();
                            
                            $("#messageSpace").append("<div class='message' id='message_"+messageID+"'><p>"+message+"</p><p>"+senderID+"|"+timestamp+"</p></div>");
                        });
                        
                    }
            });
            
        });
       */
    });

});