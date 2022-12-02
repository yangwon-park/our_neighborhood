package ywphsm.ourneighbor.api;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
class ProfileControllerUnitTest {

    @Test
    void getRealProfile() {
        String expectedProfile = "real";
        MockEnvironment env = new MockEnvironment();

        env.addActiveProfile(expectedProfile);
        env.addActiveProfile("oauth");
        env.addActiveProfile("real-db");

        ProfileController controller = new ProfileController(env);

        String profile = controller.profile();

        assertThat(profile).isEqualTo(expectedProfile);
    }

    @Test
    void noRealProfile() {
        String expectedProfile = "oauth";
        MockEnvironment env = new MockEnvironment();

        env.addActiveProfile(expectedProfile);
        env.addActiveProfile("real-db");

        ProfileController controller = new ProfileController(env);

        String profile = controller.profile();

        assertThat(profile).isEqualTo(expectedProfile);
    }

    @Test
    void noActiveProfile() {
        String expectedProfile = "default";
        MockEnvironment env = new MockEnvironment();

        ProfileController controller = new ProfileController(env);

        String profile = controller.profile();

        assertThat(profile).isEqualTo(expectedProfile);
    }
}