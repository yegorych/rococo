package guru.qa.rococo.data.entity.painting;

import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.painting.Painting;
import guru.qa.rococo.model.rest.PaintingJson;
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
@Table(name = "\"painting\"")
public class PaintingEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
  private UUID id;

  @Column(nullable = false)
  private String title;

  @Column(length = 1000)
  private String description;

  @Column(nullable = false, name = "artist_id", columnDefinition = "BINARY(16)")
  private UUID artistId;

  @Column(name = "museum_id", columnDefinition = "BINARY(16)")
  private UUID museumId;

  @Lob
  @Column(name = "content", columnDefinition = "LONGBLOB")
  private byte[] content;

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    PaintingEntity that = (PaintingEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }

  public static PaintingEntity fromJson(PaintingJson json){
    PaintingEntity pe = new PaintingEntity();
    pe.setId(json.id());
    pe.setTitle(json.title());
    pe.setDescription(json.description());
    pe.setMuseumId(json.museum() != null ? json.museum().id() : null);
    pe.setArtistId(json.artist().id());
    pe.setContent(json.content() != null ? json.content().getBytes(StandardCharsets.UTF_8) : null);
    return pe;
  }

  public static PaintingEntity fromGrpcMessage(Painting painting) {
    PaintingEntity pe = new PaintingEntity();
    entityToProto(painting, pe);
    return pe;
  }

  public static PaintingEntity fromGrpcMessage(Painting painting, PaintingEntity pe) {
    entityToProto(painting, pe);
    return pe;
  }

  public Painting toGrpcMessage() {
    return Painting.newBuilder()
            .setId(id.toString())
            .setTitle(title)
            .setDescription(description != null ? description : "")
            .setArtistId(artistId.toString())
            .setMuseumId(museumId != null ? museumId.toString() : "")
            .setContent(content != null ? ByteString.copyFrom(content) : ByteString.empty())
            .build();
  }


  private static void entityToProto(Painting painting, PaintingEntity pe) {
    pe.setId(!painting.getId().isEmpty() ? UUID.fromString(painting.getId()) : null);
    pe.setTitle(painting.getTitle());
    pe.setDescription(painting.getDescription());
    pe.setArtistId(UUID.fromString(painting.getArtistId()));
    pe.setMuseumId(!painting.getMuseumId().isEmpty() ? UUID.fromString(painting.getMuseumId()) : null);
    pe.setContent(painting.getContent().toByteArray());
  }
}