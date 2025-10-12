package guru.qa.rococo.service.impl.api;

import guru.qa.rococo.api.PaintingApi;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.PaintingJson;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class PaintingApiClient extends RestClient {

  private final PaintingApi paintingApi;

  public PaintingApiClient() {
    super(CFG.gatewayUrl());
    this.paintingApi = create(PaintingApi.class);
  }

  @Step("Get paintings using GET /api/painting with page={1}, size={2}, title={0}")
  @Nonnull
  public Response<RestResponsePage<PaintingJson>> getPaintingPage(@Nullable String title,
                                                              int page, 
                                                              int size) {
    return requireNonNull(execute(paintingApi.getPaintingPage(title, page, size)));
  }

  @Step("Get painting by id={0} using GET /api/painting")
  @Nonnull
  public Response<PaintingJson> getPainting(String id) {
    return requireNonNull(execute(paintingApi.getPaintingById(id)));
  }

  @Step("Create painting using POST /api/painting")
  @Nonnull
  public Response<PaintingJson> createPainting(String token, PaintingJson paintingJson) {
    return requireNonNull(execute(paintingApi.createPainting(token, paintingJson)));
  }

  @Step("Update painting using PATCH /api/painting")
  @Nonnull
  public Response<PaintingJson> updatePainting(String token, PaintingJson paintingJson) {
    return requireNonNull(execute(paintingApi.updatePainting(token, paintingJson)));
  }

  @Step("Get paintings by authorId={0} using GET /api/painting/author with page={1}, size={2}")
  @Nonnull
  public Response<RestResponsePage<PaintingJson>> getPaintingsByAuthorId(@Nullable String authorId,
                                                                  int page,
                                                                  int size) {
    return requireNonNull(execute(paintingApi.getByArtistId(authorId, page, size)));
  }

}
