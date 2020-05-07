package quicksolution.digitalshiksha.Model;

public class Contacts {
    public String name, status, image,contact;

    public Contacts()
    {

    }

    public Contacts(String name, String status, String image, String contact) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
