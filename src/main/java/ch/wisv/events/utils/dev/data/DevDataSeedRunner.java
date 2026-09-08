package ch.wisv.events.utils.dev.data;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "devcontainer"})
@ConditionalOnProperty(prefix = "events.dev-data", name = "seed", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
class DevDataSeedRunner implements ApplicationRunner {

    private final DevDataSeeder devDataSeeder;

    @Override
    public void run(ApplicationArguments args) {
        devDataSeeder.seedIfDatabaseIsEmpty();
    }
}
