package com.rotilho.jnano.client.work;

import com.rotilho.jnano.client.NanoAPI;
import com.rotilho.jnano.client.NanoAPIAction;

import javax.annotation.Nonnull;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor(staticName = "of")
public class NanoWorkOperations {
    @NonNull
    private final NanoAPI api;

    public String generate(@Nonnull String hash) {
        WorkAction request = new WorkAction(hash);
        Work work = api.execute(request, Work.class);
        return work.getWork();
    }


    @Value
    private static class WorkAction implements NanoAPIAction {
        private final String hash;

        public String getAction() {
            return "work_generate";
        }
    }

    @Value
    private static class Work {
        private final String work;
    }
}
