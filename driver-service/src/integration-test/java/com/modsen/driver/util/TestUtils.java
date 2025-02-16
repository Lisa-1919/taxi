package com.modsen.driver.util;

import java.util.UUID;

public class TestUtils {
    public static final String ACTIVE_PARAM = "active";
    public static final Long NON_EXISTING_ID = 999L;
    public static final Long EXISTING_ID = 10L;
    public static final Long EDIT_ID = 11L;

    public static final UUID NON_EXISTING_DRIVER_ID = UUID.fromString("11111111-9999-1111-1111-111111111111");
    public static final UUID EXISTING_DRIVER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID EDIT_DRIVER_ID = UUID.fromString("11111111-0000-1111-1111-111111111111");

    public static final String DRIVER_BASE_URL = "/api/v1/drivers";
    public static final String DRIVER_BY_ID_URL = DRIVER_BASE_URL + "/{id}";
    public static final String DRIVER_EXISTS_URL = DRIVER_BY_ID_URL + "/exists";

    public static final String CAR_BASE_URL = "/api/v1/cars";
    public static final String CAR_BY_ID_URL = CAR_BASE_URL + "/{id}";

    public static final String TOKEN = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJWZUdSdWRITjNrSjNQTXoweXczQVM2ZzZTVnlVVzlxb3FmRGctak15bVQwIn0.eyJleHAiOjE3Mzk1NTA2MjgsImlhdCI6MTczOTU0ODgyOSwianRpIjoiZGU5ZjM1YmUtYThhOC00OGVkLThkOWYtYjNhYjYyZTQxMDI2IiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjgwODAvcmVhbG1zL3RheGkiLCJhdWQiOlsiYXV0aC1zZXJ2aWNlIiwiYWNjb3VudCJdLCJzdWIiOiI2YjUwZDUwYS04NDkyLTRlZjEtOTAyZi1lMWEyNjJiN2MzN2MiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhdXRoIiwic2lkIjoiMTE5OTczY2YtMjJiMS00Mzk2LTlmZWUtYzA5NjFkN2UyYTUzIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiUk9MRV9vZmZsaW5lX2FjY2VzcyIsIlJPTEVfUEFTU0VOR0VSIiwiUk9MRV9kZWZhdWx0LXJvbGVzLXRheGkiLCJST0xFX3VtYV9hdXRob3JpemF0aW9uIiwiUk9MRV9EUklWRVIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJ0ZXN0IHRlc3QiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0QGdtYWlsLmNvbSIsImdpdmVuX25hbWUiOiJ0ZXN0IiwiZmFtaWx5X25hbWUiOiJ0ZXN0IiwiZW1haWwiOiJ0ZXN0QGdtYWlsLmNvbSJ9.Be0xS77mErtKaICkQrQsqEBqgtLTPvAxizq23jL3tI4-hp4z3YbTnCxV9j8qOokf5i_q0z_26YwY6UoIEiN6zHLWexk9wI5QBPNu32C54d4YobcYUxZUtZOOzZ0_l5QXUq6qk2yjk7PK0s-tEw-WtVzvovf2SUF4UFMqGliMQdgLnGX_Na6sPB9QeX0nhhJexMthWOTtC73JtX-7uyQvcFaAaNgvfVDXe7d36kjK5MtEyskVfbOcJMawxicnNRhb1-ZIlODcOq5_2vr4JbHTOHuMU2KolBMqyn_LeSRKkikAO8bw-rh4MGHzhL0BxFsDkJzemR22qa5R2R2uputopw";
}
