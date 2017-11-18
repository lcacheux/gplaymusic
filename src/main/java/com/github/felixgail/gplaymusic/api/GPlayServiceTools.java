package com.github.felixgail.gplaymusic.api;

import com.github.felixgail.gplaymusic.exceptions.NetworkException;
import com.github.felixgail.gplaymusic.model.MutationResponse;
import com.github.felixgail.gplaymusic.model.requests.mutations.Mutator;
import com.github.felixgail.gplaymusic.util.language.Language;

import java.io.IOException;

import retrofit2.Response;

public class GPlayServiceTools {

    private GPlayServiceTools() {

    }

    public static MutationResponse makeBatchCall(GPlayService service, String path, Mutator body)
            throws IOException {
        Response<MutationResponse> response = service.batchCall("sj/v2.5/" + path, body).execute();
        if (!response.body().checkSuccess()) {
            NetworkException exception = new NetworkException(400, Language.get("network.GenericError"));
            exception.setResponse(response.raw());
            throw exception;
        }
        return response.body();
    }
}
