$(document).ready(function () {
    
    var userID = getCookie("userID");
    var wsUri = "ws://localhost:8080/ProjectV1/chatendpoint";
    var websocket;
    
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
    }
    
    //Sending a message
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
                sendMsg(groupID);
            }
        });
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
        console.log("Scrolling to bot "+height);
    }
    
    //Loads a groups
    $(document).on('click', '.group', function () {
        var groupID = $(this).attr("id");
        var groupIDSplit = groupID.split("_");
        $("#groupplaceholder").attr("id", groupIDSplit[1]);
        $(".groupnamespan").html($(this).text());
        $("#messageSpace").html(" ");

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
                    //Getting the username
                    var messageclasses = "message";
                    
                    if (senderID === userID) {
                        messageclasses = "message mymessage"
                        senderName = "Me";
                    }
                    $("#messageSpace").append("<div class='" + messageclasses + "' id='message_" + messageID + "'><p>" + message + "</p><p>" + senderName + " <span class='timestamp'>" + timestamp + "</span></p></div>");
                });
                scrollToBot();                 
            }           
        });

        websocket = new WebSocket(wsUri);
        websocket.onopen = function (event) {
            //onOpen(event);
        };
        websocket.onmessage = function (event) {
            onMessage(event);
        };
        websocket.onerror = function (event) {
            writeToScreen('<span style="color: red;">ERROR:</span> ' + event.data);
        };
        websocket.onclose = function (event) {
            console.log('Closing');
        }; 
    });
      
    
    function onMessage(event) {
        //$('#messageSpace').append(event.data + '<br>');
        loadMessages(event.data);
    }
    function sendMsg(msg) {
        //console.log('Sending message to group: ' + msg);
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
    
    function loadMessages(groupID){
        var currentGroup = $(".usergroup").attr("id");
        if(parseInt(groupID) == parseInt(currentGroup)){
            var messageAttrID = $("#messageSpace div:last").attr("id");
            var messageIDSplit = messageAttrID.split("_");
            var latestMessage = messageIDSplit[1];

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
    $(".groupnamespan").click(function () {
        $("#groupfunctions").toggle();
    });   
       
});