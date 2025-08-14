package ukim.finki.file_vault.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
public class UserFile {
    @Id
    @GeneratedValue
    private Long id;
    private Long ownerID;
    private Long size;
    private String filePath;
    private String fileName;
    private String contentType;
    @ManyToMany(mappedBy = "files")
    private List<User> usersWithAccess = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserFile userFile = (UserFile) o;
        return Objects.equals(id, userFile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "UserFile{" +
                "ownerID=" + ownerID +
                ", size=" + size +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
