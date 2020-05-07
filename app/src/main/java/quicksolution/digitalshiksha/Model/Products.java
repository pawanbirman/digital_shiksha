package quicksolution.digitalshiksha.Model;

public class Products
{
    private  String pname,image,pid,description,Service,date,time;

    public Products() {
    }

    public Products(String pname, String image, String pid, String description, String service) {
        this.pname = pname;
        this.image = image;
        this.pid = pid;
        this.description = description;
        this.Service = service;
        this.date = date;
        this.time = time;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getService() {
        return Service;
    }

    public void setService(String service) {
        Service = service;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}