
package ResourcePackage;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="returninformation")
public class ReturnInformation implements Serializable{
    private static final long serialVersionUID = 1L;
    private int userID;
    private String authCred;
    
    public ReturnInformation(){
        
    }
    
    public ReturnInformation(int userID, String authCred){
        this.userID = userID;
        this.authCred = authCred;
    }
    
    @XmlElement
    public int getUserID(){
        return this.userID;
    }

    @XmlElement
    public String getAuthCred() {
        return this.authCred;
    }
   
}
