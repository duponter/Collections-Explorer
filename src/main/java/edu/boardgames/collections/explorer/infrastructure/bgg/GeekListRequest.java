package edu.boardgames.collections.explorer.infrastructure.bgg;

public class GeekListRequest extends BggRequest<GeekListRequest> {
	public GeekListRequest(String id) {
		super(BggApi.V1.create(String.format("geeklist/%s", id)));
	}

	@Override
	GeekListRequest self() {
		return this;
	}
}
