package com.rotilho.jnano.client.work;

import com.rotilho.jnano.client.NanoAPI;
import com.rotilho.jnano.client.NanoRequest;

import javax.annotation.Nonnull;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor(staticName = "of")
public class NanoRemoteWorkOperations implements NanoWorkOperations {
    @NonNull
    private final NanoAPI api;

    public String perform(@Nonnull String hash) {
        NanoRequest request = NanoRequest.builder()
                .action("work_generate")
                .param("hash", hash)
                .build();
        Work work = api.execute(request, Work.class);
        return work.getWork();
    }

    @Value
    private static class Work {
        private final String work;
    }
}
