package guru.qa.rococo.data;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.museum.Museum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "\"museum\"")
public class MusuemEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
  private UUID id;

  @Column(nullable = false)
  private String title;

  @Column(length = 1000)
  private String description;

  @Column
  private String city;

  @Column(nullable = false, name = "country_id", columnDefinition = "BINARY(16)")
  private UUID countryId;

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
    MusuemEntity that = (MusuemEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }

  public static MusuemEntity fromGrpcMessage(Museum museum) {
    MusuemEntity me = new MusuemEntity();
    entityToProto(museum, me);
    return me;
  }

  public static MusuemEntity fromGrpcMessage(Museum museum, MusuemEntity me) {
    entityToProto(museum, me);
    return me;
  }

  public Museum toGrpcMessage() {
    return Museum.newBuilder()
            .setId(id.toString())
            .setTitle(title)
            .setDescription(description != null ? description : "")
            .setCity(city != null ? city : "")
            .setCountryId(countryId != null ? countryId.toString() : "")
            .setPhoto(photo != null ? ByteString.copyFrom(photo) : ByteString.empty())
            .build();
  }


  private static void entityToProto(Museum museum, MusuemEntity me) {
    me.setId(!museum.getId().isEmpty() ? UUID.fromString(museum.getId()) : null);
    me.setTitle(museum.getTitle());
    me.setDescription(museum.getDescription());
    me.setCity(museum.getCity());
    me.setCountryId(!museum.getCountryId().isEmpty() ? UUID.fromString(museum.getCountryId()) : null);
    me.setPhoto(museum.getPhoto().toByteArray());
  }
}