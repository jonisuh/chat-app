$(document).ready(function () {
    
    var userID = getCookie("userID");
    var wsUri = "ws://localhost:8080/ProjectV1/chatendpoint";
    var websocket;
    var wsCheck = true;
    var pollInterval;
    
    init();
    
    function init(){
        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Users/" + userID + "/",
            dataType: 'xml'
            , success: function (userInformation) {
                var userName = $(userInformation).find("username");
                $(".usernamespan").html(userName);
            }
        });
        $("#userlist").html(" ");
        
        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Users/" + userID + "/groups/",
            dataType: 'xml'
            , success: function (groupInformation) {
                $("#grouplist").append("<h2>Groups<span id='createGroup'> +</span></h2>");
                $("#grouplist").append("<div id='newGroup'></div>");

                $("#newGroup").load("newgroup.html");
                $("#grouplist").append("<div id='groupWrapper'>");
                $groups = $(groupInformation);
                $(groupInformation).find("group").each(function () {
                    var groupName = $(this).find("groupName").text();
                    var groupID = $(this).find("groupID").text();
                    $("#groupWrapper").append("<div class='group' id='group_" + groupID + "'><h3>" + groupName + "</h3></div>");
                });
                $("#grouplist").append("</div>");
            }
        });
        $("#placeholder").attr("id", userID);
    }
    
    $(document).on('click', '#createGroup', function () {
       $("#newGroup").toggle();
       $("#groupWrapper").toggle();
       
    });
    //Send message by pressing enter
    $('#message').keypress(function (e) {
    if(e.which == 13)  // the enter key code
     {
       $('#sendMessage').click();
       return false;  
     }
    }); 
    //Sending a message
    $("#sendMessage").click(function () {
        var message = $("#message").val();
        $("#message").val("");
        if(message != ""){
            var userID = $(".username").attr("id");
            var currentgroupID = $(".usergroup").attr("id");

            $.ajax({
                type: "POST",
                url: "/ProjectV1/API/Groups/" + currentgroupID + "/users/" + userID + "/messages/",
                data: {message: message}
                , success: function (messageCreation) {
                    if(wsCheck === true){
                        sendMsg(currentgroupID);
                    }
                }
            });
        }else{
            
        }
    });
    
    //Returns cookie value
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
    
    //Scrolls the messagespace to the bottom
    function scrollToBot(){
        var height = 0;
        $('.message').each(function(i, value){
            height += parseInt($(this).height()) + 2;
        });
        height += '';
        $('#messageSpace').scrollTop(height);
        
    }
    
    
    
    
    
    //Loads a groups
    $(document).on('click', '.group', function () {
        if(websocket){
            websocket.close();
        }

        var groupID = $(this).attr("id");
        var groupIDSplit = groupID.split("_");
        $(".usergroup").attr("id", groupIDSplit[1]);
        $(".groupnamespan").html($(this).text());
        $("#messageSpace").html(" ");
        $("#userlist").html(" ");
        $("#message").val("");
        //Loading users
        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Groups/" + groupIDSplit[1] + "/users/",
            dataType: 'xml'
            , success: function (users) {
                
                $('user', users).each(function () {
                    
                    var thisuserID = $(this).find("userID").text();
                    var thisuserName = $(this).find("username").text();
                    if(parseInt(thisuserID) === parseInt(userID)){
                        thisuserName = "Me";
                    }                  
                    $("#userlist").append("<div class='usercontainer' id='user_"+thisuserID+"'><p>"+thisuserName+"</p></div>")
                    
                    
                    
                });
            }    
         });  
         
        //Loading messages to the message screen
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
                    var timestamp = formatTimestamp($(this).find("timestamp").text());
                    var senderName = $(this).find("username").text();
                    
                    var messageclasses = "message";
                    
                    if (senderID === userID) {
                        messageclasses = "message mymessage"
                        senderName = "Me";
                    }
                    $("#messageSpace").append("<div class='" + messageclasses + "' id='message_" + messageID + "'><p>" + message + "</p><p>" + senderName + " <span class='timestamp'>" + timestamp + "</span></p></div>");
                });
                     scrollToBot();

                    websocket = new WebSocket(wsUri);

                    websocket.onopen = function (event) {
                        //onOpen(event);
                    };
                    websocket.onmessage = function (event) {
                        onMessage(event);
                    };
                    websocket.onerror = function (event) {
                        wsCheck = false;
                        fallbackPoll(groupIDSplit[1]);
                    };
                    websocket.onclose = function (event) {
                        console.log('Closing');
                    };
                    
                }       
                       
        });

    });
    function fallbackPoll(groupID){
        console.log("WebSocket failed, using polling.");
        
        //Closing existing polling
        console.log("Closed poll "+pollInterval);
        clearInterval(pollInterval);
        
        pollInterval = null;  
        
        pollInterval = setInterval(function(){
            checkMessages(groupID);
        }, 500);
        console.log("Started new poll on group "+groupID+" with poll "+pollInterval);
    }
    
    function onMessage(event) {
        console.log("New message in group "+event.data);
        loadMessages(event.data);
    }
    function sendMsg(msg) {
        websocket.send(msg);
    }

    /*
    // For testing purposes
    function onOpen(event) {
        //writeToScreen('Connected to ' + wsUri);
    }
    
    function writeToScreen(message) {
        //$('#messageSpace').append(message + '<br>');
    }
    */
    function checkMessages(groupID){
        
        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Groups/" + groupID + "/messages/latest/",
            success: function (latestmessageID) {
                
                
                var messageAttrID = $("#messageSpace div:last").attr("id");
                var latestMessage;
                
                if(!messageAttrID){
                    latestMessage = "0";
                }else{
                  var messageIDSplit = messageAttrID.split("_");
                  latestMessage = messageIDSplit[1];
                }
                
                if(parseInt(latestmessageID) > parseInt(latestMessage)){
                    loadMessages(groupID);
                }               
            }         
        });
    }
    
    function loadMessages(groupID){
        var currentGroup = $(".usergroup").attr("id");
        if(parseInt(groupID) == parseInt(currentGroup)){
            var messageAttrID = $("#messageSpace div:last").attr("id");
            var latestMessage;

            if(!messageAttrID){
                latestMessage = "0";
            }else{
              var messageIDSplit = messageAttrID.split("_");
              latestMessage = messageIDSplit[1];
            }
            
            
            $.ajax({
                type: "GET",
                url: "/ProjectV1/API/Groups/" + groupID + "/messages/",
                dataType: 'xml'
                , success: function (messages) {
                    $('messageroot', messages).each(function () {                       
                        var messageID = $(this).find("messageID").text();
                        if(parseInt(messageID) > parseInt(latestMessage)){
                            var timestamp = formatTimestamp($(this).find("timestamp").text());
                            var senderName = $(this).find("username").text();
                            var senderID = $(this).find("userID").text();
                            var message = $(this).find("message").text();
                            //Getting the username
                            var senderName = $(this).find("username").text();
                            //Getting the username
                            var messageclasses = "message";

                            if (senderID === userID) {
                                messageclasses = "message mymessage"
                                senderName = "Me";
                            }
                           /* var newMessage = $("<div class='" + messageclasses + "' id='message_" + messageID + "'><p>" + message + "</p><p>" + senderName + " <span class='timestamp'>" + timestamp + "</span></p></div>").hide();
                            $("#messageSpace").append(newMessage);
                            newMessage.show("slow"); */
                            $("#messageSpace").append("<div class='" + messageclasses + "' id='message_" + messageID + "'><p>" + message + "</p><p>" + senderName + " <span class='timestamp'>" + timestamp + "</span></p></div>");
                        }
                    });
                    scrollToBot();            
                }        
            });
        } 
    }
    
    function formatTimestamp(timestamp){
        var splitTimestamp = timestamp.split(" ");
        var date = splitTimestamp[0];
        var time = splitTimestamp[1];
        
        var splitDate = date.split("-");
        var splitTime = time.split(":");
        
        return splitDate[2]+"."+splitDate[1]+" "+splitTime[0]+":"+splitTime[1];
        
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
    
    //WIP
    /*
    $(".groupnamespan").click(function () {
        $("#groupfunctions").toggle();
    });  
    */

       
});