package com.abhi.FitnessTracker.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user-1");
        user.setEmail("test@example.com");
    }

    @Test
    void addProfile_addsToProfilesList() {
        Profile profile = new Profile();
        profile.setId("p1");
        profile.setName("John");

        user.addProfile(profile);

        assertEquals(1, user.getProfiles().size());
        assertEquals("John", user.getProfiles().get(0).getName());
    }

    @Test
    void addProfile_nullProfilesList_initializesAndAdds() {
        user.setProfiles(null);

        Profile profile = new Profile();
        profile.setId("p1");

        user.addProfile(profile);

        assertNotNull(user.getProfiles());
        assertEquals(1, user.getProfiles().size());
    }

    @Test
    void addProfile_multipleProfiles() {
        Profile p1 = new Profile();
        p1.setId("p1");
        p1.setName("Profile A");

        Profile p2 = new Profile();
        p2.setId("p2");
        p2.setName("Profile B");

        user.addProfile(p1);
        user.addProfile(p2);

        assertEquals(2, user.getProfiles().size());
    }

    @Test
    void getProfileById_existingProfile_returnsProfile() {
        Profile profile = new Profile();
        profile.setId("p1");
        profile.setName("My Profile");
        user.addProfile(profile);

        Profile found = user.getProfileById("p1");

        assertNotNull(found);
        assertEquals("My Profile", found.getName());
    }

    @Test
    void getProfileById_nonExisting_returnsNull() {
        Profile profile = new Profile();
        profile.setId("p1");
        user.addProfile(profile);

        assertNull(user.getProfileById("p999"));
    }

    @Test
    void getProfileById_nullProfiles_returnsNull() {
        user.setProfiles(null);
        assertNull(user.getProfileById("p1"));
    }

    @Test
    void updateProfile_existingProfile_returnsTrue() {
        Profile original = new Profile();
        original.setId("p1");
        original.setName("Original");
        user.addProfile(original);

        Profile updated = new Profile();
        updated.setId("p1");
        updated.setName("Updated");

        assertTrue(user.updateProfile("p1", updated));
        assertEquals("Updated", user.getProfileById("p1").getName());
    }

    @Test
    void updateProfile_nonExisting_returnsFalse() {
        Profile profile = new Profile();
        profile.setId("p1");
        user.addProfile(profile);

        Profile updated = new Profile();
        updated.setId("p999");

        assertFalse(user.updateProfile("p999", updated));
    }
}
