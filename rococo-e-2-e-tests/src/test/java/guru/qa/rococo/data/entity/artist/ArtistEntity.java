package guru.qa.rococo.data.entity.artist;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.artist.Artist;
import guru.qa.rococo.model.rest.ArtistJson;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "\"artist\"")
public class ArtistEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, length = 2000)
  private String biography;

  @Lob
  @Column(name = "photo", columnDefinition = "LONGBLOB")
  private byte[] photo;

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    ArtistEntity that = (ArtistEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }

  public Artist toGrpcMessage() {
    return Artist.newBuilder()
            .setId(id.toString())
            .setName(name)
            .setBiography(biography)
            .setPhoto(photo != null ? ByteString.copyFrom(photo) : ByteString.empty())
            .build();
  }

  public static ArtistEntity fromGrpcMessage(Artist artist) {
    ArtistEntity ae = new ArtistEntity();
    entityToProto(artist, ae);
    return ae;
  }

  public static ArtistEntity fromGrpcMessage(Artist artist, ArtistEntity ae) {
    entityToProto(artist, ae);
    return ae;
  }

  public static ArtistEntity fromJson(ArtistJson json){
    ArtistEntity ae = new ArtistEntity();
    ae.setId(json.id());
    ae.setName(json.name());
    ae.setBiography(json.biography());
    ae.setPhoto(json.photo() != null ? json.photo().getBytes(StandardCharsets.UTF_8) : null);
    return ae;
  }

  private static void entityToProto(Artist artist, ArtistEntity ae) {
    ae.setId(!artist.getId().isEmpty() ? UUID.fromString(artist.getId()) : null);
    ae.setName(artist.getName());
    ae.setBiography(artist.getBiography());
    ae.setPhoto(artist.getPhoto().toByteArray());
  }

}